/*
*	This file is part of STRoMI.
*
*    STRoMI is free software: you can redistribute it and/or modify
*    it under the terms of the GNU General Public License as published by
*    the Free Software Foundation, either version 3 of the License, or
*    (at your option) any later version.
*
*    STRoMI is distributed in the hope that it will be useful,
*    but WITHOUT ANY WARRANTY; without even the implied warranty of
*    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
*    GNU General Public License for more details.
*
*    You should have received a copy of the GNU General Public License
*    along with STRoMI.  If not, see <http://www.gnu.org/licenses/>.
*/

package pt.utils;


import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import pt.datahandler.IStratexDataHandler;
import pt.db.MongoDBDataHandler;
import pt.exception.*;
import pt.intance.provider.MongoConnectionInstanceProvider;
import pt.properties.PropertyFileLoader;
import pt.worker.mongoHash.UsersHashPersistProvider;

import java.util.*;

public class DistributedUsersToFollowProvider {

    private LinkedList<Long> usersInProcess;
    //    private Hashtable<String, String> usersInProcess;
    private Hashtable<String, String> availableUsers;
    private static volatile DistributedUsersToFollowProvider singleton;

    private String usersCollectionName;
    private UsersHashPersistProvider usersHashPersistProvider;

    private static final String USERS_COLLECTION_NAME_PROPERTY_NAME = "distributedMonitorization_usersCollectionName";



    private static final Object syncObj = new Object();

    private DistributedUsersToFollowProvider() throws InvalidPropertiesException, NotInitializedException {
//        usersInProcess = new Hashtable<String, String>();
        usersInProcess = new LinkedList<Long>();
        availableUsers = new Hashtable<String, String>();
        Properties properties = PropertyFileLoader.getProperties();
        usersCollectionName = properties.getProperty(USERS_COLLECTION_NAME_PROPERTY_NAME);
        usersHashPersistProvider = UsersHashPersistProvider.getInstance();
    }

    public static DistributedUsersToFollowProvider getInstance() throws InvalidPropertiesException, NotInitializedException {
        synchronized (syncObj) {
            if (singleton == null) {
                singleton = new DistributedUsersToFollowProvider();
            }
            return singleton;
        }
    }

    public void registerUsersInProcess(LinkedList<Long> usersInProcess) {
        for (long user : usersInProcess) {
            if (!this.usersInProcess.contains(user))
                this.usersInProcess.add(user);
        }
    }

    public void unregisterUsersInProcess(LinkedList<Long> usersInProcess) {
        for (long user : usersInProcess) {
            if (this.usersInProcess.contains(user))
                this.usersInProcess.remove(user);
        }
    }

    public synchronized ArrayList<LinkedList<Long>> getUsers(int numberOfProcesses, int numberOfUsersPerProcess) throws NoMoreTwitterUsersAvailableException {

        // Connect to MongoDB database and get ids to follow
        ArrayList<LinkedList<Long>> users = new ArrayList<LinkedList<Long>>(numberOfProcesses);
        MongoDBDataHandler dataHandler = null;
        try {
            dataHandler = MongoConnectionInstanceProvider.getInstance();
            DBCollection usersCollection = dataHandler.getCollection(usersCollectionName);

            int totalOfUsersToRetreive = (int) (numberOfProcesses * numberOfUsersPerProcess * 0.1);
            LogUtils.logInfo("Getting Most Mentioned Users: " + totalOfUsersToRetreive);
            Iterator<DBObject> mostMentionedUsers = dataHandler.getQueryResultOrderedBy(
                    usersCollection,
                    null,
                    "mentions_count",
                    IStratexDataHandler.OrderType.DESCENDENT,
                    totalOfUsersToRetreive);
            LogUtils.logInfo("makeDistributionOfResults: first call");
            int pos = makeDistributionOfResults(users, mostMentionedUsers, 0, totalOfUsersToRetreive,numberOfProcesses);

            totalOfUsersToRetreive = (int) (numberOfProcesses * numberOfUsersPerProcess * 0.9);
            LogUtils.logInfo("Getting Less Mentioned Users: " + totalOfUsersToRetreive);
            Iterator<DBObject> lessMentionedUsers = dataHandler.getQueryResultOrderedBy(
                    usersCollection,
                    null,
                    "mentions_count",
                    IStratexDataHandler.OrderType.ASCENDENT,
                    totalOfUsersToRetreive);
            LogUtils.logInfo("makeDistributionOfResults: second call");
            pos = makeDistributionOfResults(users, lessMentionedUsers, pos,totalOfUsersToRetreive,numberOfProcesses);
            return users;
        } catch (NotInitializedException e) {
            LogUtils.logError(e.getMessage());
        } catch (InstanceInitializeException e) {
            LogUtils.logError(e.getMessage());
        }catch (Exception e) {
            LogUtils.logError(e.getMessage());
        }
        finally {
            if (dataHandler != null)
                dataHandler.closeConnection();
        }
        return null;
    }

    /**
     * Distributes the users into an ArrayList. Each position of the array list is a LinkedList that will be assigned to a process.
     *
     * @param users             - The arrayList of users ids - Each position of array represents a list of users that will be assigned to a process
     * @param usersToDistribute - An Iterator that contains the set of users to be distributed
     * @param pos               - Represents the next position of the ArrayList to be filled
     * @return Returns the next position to fill of users ArrayList
     */
    private int makeDistributionOfResults(ArrayList<LinkedList<Long>> users, Iterator<DBObject> usersToDistribute, int pos, int totalOfUsersToGet, int numberOfInstances) {
        int count = 0;
        LogUtils.logInfo("DistributedUsersToFollowProvider: makeDistributionOfResults");
        while (usersToDistribute.hasNext() && count < totalOfUsersToGet) {
            DBObject currentUser = usersToDistribute.next();
            long userID = Long.parseLong(currentUser.get("_id").toString());
            if (!users.contains(userID) && !usersInProcess.contains(userID)) {
                if(users.size()<=pos){
                    users.add(pos, new LinkedList<Long>());
                }
                users.get(pos++).add(userID);
                usersHashPersistProvider.queueFollowedUser(userID);
                pos %= numberOfInstances;
                count++;
            }
        }
        return pos;
    }
}
