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

package pt.worker;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import pt.datahandler.StratexDataHandler;
import pt.exception.InvalidPropertiesException;
import pt.exception.StratexDataHandlerException;
import pt.properties.PropertyFileLoader;
import pt.utils.LogUtils;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

public class UsersWorker<DataBaseProvider, CollectionObject, QueryResultObject, Statement> extends AUsersWorker {


    /*properties names*/
    private static final String ID_COLUMN_NAME_PROPERTY_NAME = "userWorker_idColumnName";
    private static final String USER_ID_COLUMN_NAME_PROPERTY_NAME = "userWorker_userIdColumnName";
    private static final String TWEETS_USER_COLUMN_NAME_PROPERTY_NAME = "userWorker_tweetUserColumnName";
    private static final String TWEET_USER_ID_COLUMN_NAME_PROPERTY_NAME = "userWorker_tweetUserIdColumnName";
    private static final String TWEETS_COLLECTION_NAME_PROPERTY_NAME = "userWorker_tweetsCollectionName";
    private static final String TWEETS_QUEUE_COLLECTION_NAME_PROPERTY_NAME = "userWorker_tweetQueueCollectionName";
    private static final String USERS_IN_PROCESS_COLLECTION_NAME_PROPERTY_NAME = "userWorker_usersInProcessCollectionName";
    private static final String PENDING_USERS_COLLECTION_NAME_PROPERTY_NAME = "userWorker_pendingUsersCollectionMain";

    /*database properties*/
    private StratexDataHandler<DataBaseProvider, CollectionObject, QueryResultObject, Statement> dataHandler;
    private String idColumnName;
    private String userIdColumnName;
    private String tweetUserColumnName;
    private String tweetUserIdColumnName;
    private String tweetsCollectionName;
    private String tweetQueueCollectionName;
    private String usersInProcessCollectionName;
    private String pendingUsersCollectionMain;


    public UsersWorker(StratexDataHandler<DataBaseProvider, CollectionObject, QueryResultObject, Statement> dataHandler) throws UnknownHostException, InvalidPropertiesException {
        super();
        this.dataHandler = dataHandler;
        Properties properties = PropertyFileLoader.getProperties();
        this.idColumnName = properties.getProperty(ID_COLUMN_NAME_PROPERTY_NAME);
        this.userIdColumnName = properties.getProperty(USER_ID_COLUMN_NAME_PROPERTY_NAME);
        this.tweetUserColumnName = properties.getProperty(TWEETS_USER_COLUMN_NAME_PROPERTY_NAME);
        this.tweetUserIdColumnName = properties.getProperty(TWEET_USER_ID_COLUMN_NAME_PROPERTY_NAME);
        this.tweetsCollectionName = properties.getProperty(TWEETS_COLLECTION_NAME_PROPERTY_NAME);
        this.tweetQueueCollectionName = properties.getProperty(TWEETS_QUEUE_COLLECTION_NAME_PROPERTY_NAME);
        this.usersInProcessCollectionName = properties.getProperty(USERS_IN_PROCESS_COLLECTION_NAME_PROPERTY_NAME);
        this.pendingUsersCollectionMain = properties.getProperty(PENDING_USERS_COLLECTION_NAME_PROPERTY_NAME);
    }


    protected void doWork() {
        ///////Get Collections///////
        ///Tweets
        CollectionObject tweets = dataHandler.getCollection(tweetsCollectionName);
        //Tweets Queue
        CollectionObject tweetQueue = dataHandler.getCollection(tweetQueueCollectionName);
        //PendingUsers
        CollectionObject pendingUsers = dataHandler.getCollection(pendingUsersCollectionMain);
        //UsersInProcess
        CollectionObject usersInProcess = dataHandler.getCollection(usersInProcessCollectionName);

        ///////Gets a QueryResultObject to obtain all new tweets///////
        Iterator<QueryResultObject> tweetsFromQueue = dataHandler.getQueryResult(tweetQueue, null);
        ObjectMapper jsonMapper = new ObjectMapper();
        try {
            int workedElements = 0;
            int foundUsers = 0;
            while (tweetsFromQueue.hasNext()) {
                ++workedElements;
                QueryResultObject currentTweet = tweetsFromQueue.next();
                String userJsonString = dataHandler.getPropertyFromQueryResultObject(currentTweet, tweetUserColumnName);
                if (userJsonString != null) {
                    ++foundUsers;
                    Map map = jsonMapper.readValue(userJsonString, Map.class);
                    Object id = map.get(tweetUserIdColumnName);
                    if (id != null) {
                        String userId = id.toString();
                        Statement statement = dataHandler.getStatementObject();
                        dataHandler.insertStatementCondition(statement, idColumnName, userId);
                        dataHandler.insertStatementCondition(statement, userIdColumnName, userId);
                        if (!dataHandler.getQueryResult(usersInProcess, statement).hasNext() && !dataHandler.getQueryResult(pendingUsers, statement).hasNext()) {
                            dataHandler.insertStatement(pendingUsers, statement);
                        }
                    }
                }
                dataHandler.remove(tweetQueue, currentTweet);
                dataHandler.insertQueryResultObject(tweets, currentTweet);
            }
            if (workedElements > 0) {
                LogUtils.logInfo(String.format("UsersWoker: %d tweets processed with success and founded %d new users", workedElements, foundUsers));
            }
        } catch (JsonMappingException e) {
            LogUtils.logError(String.format("%s", e.getMessage()));
        } catch (JsonParseException e) {
            LogUtils.logError(String.format("%s", e.getMessage()));
        } catch (IOException e) {
            LogUtils.logError(String.format("%s", e.getMessage()));
        } catch (StratexDataHandlerException e) {
            LogUtils.logError(String.format("%s", e.getMessage()));
        } catch (Exception e) {
            LogUtils.logError(String.format("%s", e.getMessage()));
        } catch (Throwable e) {
            LogUtils.logError(String.format("%s", e.getMessage()));
        }
        LogUtils.logInfo("Users worker finished");
    }
}
