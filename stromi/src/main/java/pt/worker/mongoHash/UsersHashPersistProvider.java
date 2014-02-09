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

package pt.worker.mongoHash;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import pt.datahandler.IStratexHashDataHandler;
import pt.datahandler.StratexDataHandler;
import pt.exception.InvalidPropertiesException;
import pt.exception.NotInitializedException;
import pt.exception.StratexDataHandlerException;
import pt.model.UserInformation;
import pt.properties.PropertyFileLoader;
import pt.utils.LogUtils;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.*;

public class UsersHashPersistProvider<DataBaseProvider, CollectionObject, QueryResultObject, Statement>
        implements Runnable {

    /* properties names */
    private static final String SLEEP_TIME_PROPERTY_NAME = "userHashPersist_sleepTime";
    private static final String USERS_COLLECTION_NAME_PROPERTY_NAME = "userHashPersist_usersCollectionName";
    private static final String USERS_COLLECTION_USER_ID_COLUMN_NAME_PROPERTY_NAME = "userHashPersist_usersCollection_userIDColumnName_id";
    private static final String USERS_COLLECTION_POSTS_COUNT_COLUMN_NAME_PROPERTY_NAME = "userHashPersist_usersCollection_postsCountColumnName";
    private static final String USERS_COLLECTION_MENTIONS_COUNT_COLUMN_NAME_PROPERTY_NAME = "userHashPersist_usersCollection_mentionsCountColumnName";
    private static final String USERS_COLLECTION_FOLLOWS_COUNT_COLUMN_NAME_PROPERTY_NAME = "userHashPersist_usersCollection_followsCountColumnName";

    private boolean toFinish;

    /*Concurrency objects*/
    private static Object lockObjectToInit = new Object();

    /* Singleton object */
    private static UsersHashPersistProvider singleton;

    /*database properties*/
    private IStratexHashDataHandler<DataBaseProvider, CollectionObject, QueryResultObject, Statement> dataHandler;
    private HashMap<Long, UserInformation> mapOfUsers;

    private long sleepTime = 1000;
    private String usersCollectionName;
    private String usersCollectionUserIdColumnName;
    private String usersCollectionPostsCountColumnName;
    private String usersCollectionMentionsCountColumnName;
    private String usersCollectionFollowsCountColumnName;


    public static void init(IStratexHashDataHandler dataHandler) throws UnknownHostException, InvalidPropertiesException {
        synchronized (lockObjectToInit) {
            if (singleton == null) {
                singleton = new UsersHashPersistProvider(dataHandler);
            }
        }
    }

    public static UsersHashPersistProvider getInstance() throws NotInitializedException {
        if (singleton == null) throw new NotInitializedException("Singleton was not initialized");
        return singleton;
    }

    public UsersHashPersistProvider(IStratexHashDataHandler<DataBaseProvider, CollectionObject, QueryResultObject, Statement> dataHandler) throws UnknownHostException, InvalidPropertiesException {
        this.dataHandler = dataHandler;
        this.mapOfUsers = new HashMap<Long, UserInformation>();
        this.toFinish = false;

        Properties properties = PropertyFileLoader.getProperties();
        this.sleepTime = Long.parseLong(properties.getProperty(SLEEP_TIME_PROPERTY_NAME));
        this.usersCollectionName = properties.getProperty(USERS_COLLECTION_NAME_PROPERTY_NAME);
        this.usersCollectionUserIdColumnName = properties.getProperty(USERS_COLLECTION_USER_ID_COLUMN_NAME_PROPERTY_NAME);
        this.usersCollectionPostsCountColumnName = properties.getProperty(USERS_COLLECTION_POSTS_COUNT_COLUMN_NAME_PROPERTY_NAME);
        this.usersCollectionMentionsCountColumnName = properties.getProperty(USERS_COLLECTION_MENTIONS_COUNT_COLUMN_NAME_PROPERTY_NAME);
        this.usersCollectionFollowsCountColumnName = properties.getProperty(USERS_COLLECTION_FOLLOWS_COUNT_COLUMN_NAME_PROPERTY_NAME);
    }

    public void queuePostUser(long userID) {
        synchronized (mapOfUsers) {
            if (mapOfUsers.containsKey(userID)) {
                UserInformation user = mapOfUsers.get(userID);
                user.incrementPostCount();
            } else {
                mapOfUsers.put(userID, new UserInformation(userID, 1, UserInformation.CountType.POST));
            }
        }
    }

    public void queueMentionUser(long userID) {
        synchronized (mapOfUsers) {
            if (mapOfUsers.containsKey(userID)) {
                UserInformation user = mapOfUsers.get(userID);
                user.incrementMentionCount();
            } else {
                mapOfUsers.put(userID, new UserInformation(userID, 1, UserInformation.CountType.MENTION));
            }
        }
    }

    public void queueFollowedUser(long userID) {
        synchronized (mapOfUsers) {
            if (mapOfUsers.containsKey(userID)) {
                UserInformation user = mapOfUsers.get(userID);
                user.incrementFollowCount();
            } else {
                mapOfUsers.put(userID, new UserInformation(userID, 0, 0, 1));
            }
        }
    }

    public void run() {
        LogUtils.logInfo("USER HASH PERSIST PROVIDER - RUN");
        ObjectMapper jsonMapper;
        while (!toFinish) {
            Set<Map.Entry<Long, UserInformation>> setOfValuesToPersist;
            synchronized (mapOfUsers) {
                setOfValuesToPersist = new HashSet<Map.Entry<Long, UserInformation>>();
                setOfValuesToPersist.addAll(mapOfUsers.entrySet());
                mapOfUsers.clear();
            }
            CollectionObject users = dataHandler.getCollection(usersCollectionName);

            for (Map.Entry<Long, UserInformation> entry : setOfValuesToPersist) {
                jsonMapper = new ObjectMapper();
                try {
                    Long userID = entry.getKey();
                    UserInformation userInformation = entry.getValue();

                    Integer mentions = userInformation.getMentionsCount();
                    Integer postsCount = userInformation.getPostsCount();
                    Integer follows = userInformation.getFollowCount();

                    Statement statement = dataHandler.getStatementObject();
                    dataHandler.insertStatementCondition(statement, usersCollectionUserIdColumnName, userID);
                    Iterator<QueryResultObject> result = dataHandler.findOnHashCollection(users, userID, statement);
                    if (result != null && result.hasNext()) {
                        Iterator<QueryResultObject> resultFromUsersCollection = dataHandler.getQueryResult(users, statement);
                        if (resultFromUsersCollection == null || !resultFromUsersCollection.hasNext()) {
                            LogUtils.logError("Missing userID on users collection: " + userID);
                        }
                        QueryResultObject resultObjectUser = resultFromUsersCollection.next();
                        Map map = null;
                        //TODO: double check this instruction
                        map = jsonMapper.readValue(resultObjectUser.toString(), Map.class);

                        //Mentions
                        Object mentionsObject = map.get(usersCollectionMentionsCountColumnName);
                        if (mentionsObject != null) {
                            Integer mentionsFromCollection = Integer.parseInt(mentionsObject.toString());
                            mentionsFromCollection += mentions;
                            dataHandler.insertStatementCondition(statement, usersCollectionMentionsCountColumnName, mentionsFromCollection);
                        }
                        //Mentions
                        Object postsCountObject = map.get(usersCollectionPostsCountColumnName);
                        if (postsCountObject != null) {
                            Integer postsCountFromCollection = Integer.parseInt(postsCountObject.toString());
                            postsCountFromCollection += postsCount;
                            dataHandler.insertStatementCondition(statement, usersCollectionPostsCountColumnName, postsCountFromCollection);
                        }
                        //Follows
                        Object followsObject = map.get(usersCollectionFollowsCountColumnName);
                        if (followsObject != null) {
                            Integer followsFromCollection = Integer.parseInt(followsObject.toString());
                            followsFromCollection += follows;
                            dataHandler.insertStatementCondition(statement, usersCollectionFollowsCountColumnName, followsFromCollection);
                        }
                        dataHandler.update(users, statement, resultObjectUser);
                    } else {
                        dataHandler.insertStatementCondition(statement, usersCollectionMentionsCountColumnName, mentions);
                        dataHandler.insertStatementCondition(statement, usersCollectionPostsCountColumnName, postsCount);
                        dataHandler.insertStatement(users, statement);
                        dataHandler.insertOnHashCollection(users, userID, statement);
                    }
                } catch (JsonMappingException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                } catch (StratexDataHandlerException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                } catch (JsonParseException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                } catch (IOException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                } catch (Exception e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            }
            try {
                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }
    }
}
