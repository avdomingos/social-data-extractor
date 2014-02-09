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
import pt.db.MongoDBDataHandler;
import pt.exception.*;
import pt.intance.provider.MongoConnectionInstanceProvider;
import pt.properties.PropertyFileLoader;

import java.util.*;

public class UsersToFollowProvider {

    private Hashtable<String, String> usersInProcess;
    private Hashtable<String, String> availableUsers;
    private static volatile UsersToFollowProvider singleton;

    private String usersInProcessCollectionName;
    private String pendingUsersCollectionMain;

    private static final Object syncObj = new Object();

    private UsersToFollowProvider() throws InvalidPropertiesException {
        usersInProcess = new Hashtable<String, String>();
        availableUsers = new Hashtable<String, String>();

        Properties properties = PropertyFileLoader.getProperties();
        usersInProcessCollectionName = properties.getProperty("usersInProcessCollectionName");
        pendingUsersCollectionMain = properties.getProperty("pendingUsersCollectionMain");
    }

    public static UsersToFollowProvider getInstance() throws InvalidPropertiesException {
        synchronized (syncObj) {
            if (singleton == null) {
                singleton = new UsersToFollowProvider();
            }
            return singleton;
        }
    }

    public synchronized List<Long> getUsers() throws NoMoreTwitterUsersAvailableException {
        // Connect to MongoDB database and get ids to follow
        MongoDBDataHandler dataHandler = null;
        try {
            dataHandler = MongoConnectionInstanceProvider.getInstance();

            DBCollection pendingUnsersCollection = dataHandler.getCollection(pendingUsersCollectionMain);
            DBCollection usersInProcessCollection = dataHandler.getCollection(usersInProcessCollectionName);

            //TODO: Create local var to max elements
            Iterator<DBObject> pendingUsers = dataHandler.getQueryResult(pendingUnsersCollection, null, 5000);

            List<Long> list = new LinkedList<Long>();
            while (pendingUsers.hasNext()) {
                DBObject currentUser = pendingUsers.next();
                list.add((long) Double.parseDouble(currentUser.get("userID").toString()));
                dataHandler.remove(pendingUnsersCollection,currentUser);
                dataHandler.insertQueryResultObject(usersInProcessCollection, currentUser);
            }
            return list;
        } catch (NotInitializedException e) {
            LogUtils.logError(e.getMessage());
        } catch (InstanceInitializeException e) {
            LogUtils.logError(e.getMessage());
        } catch (StratexDataHandlerException e) {
            LogUtils.logError(e.getMessage());
        } finally {
            if (dataHandler != null)
                dataHandler.closeConnection();
        }

        return null;
    }
}
