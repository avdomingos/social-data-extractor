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
import pt.exception.InstanceInitializeException;
import pt.exception.InvalidPropertiesException;
import pt.exception.NotInitializedException;
import pt.exception.StratexDataHandlerException;
import pt.intance.provider.hash.MongoHashConnectionInstanceProvider;
import pt.properties.PropertyFileLoader;
import pt.utils.LogUtils;
import pt.worker.AUsersWorker;

import java.awt.*;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

public class UsersWorkerHashCollection<DataBaseProvider, CollectionObject, QueryResultObject, Statement> extends AUsersWorker {

    /*database properties*/
    private IStratexHashDataHandler<DataBaseProvider, CollectionObject, QueryResultObject, Statement> dataHandler;
    private UsersHashPersistProvider usersHashPersis;

    /* Properties Names*/
    private static final String USER_ID_PROPERTY_NAME = "userWorkerHash_userIdColumnName";
    private static final String USER_PROPERTY_NAME = "userWorkerHash_userColumnName";
    private static final String ENTITIES_PROPERTY_NAME = "userWorkerHash_entitiesColumnName";
    private static final String USER_MENTIONS_PROPERTY_NAME = "userWorkerHash_userMentionsColumnName";

    private TweetsProvider tweetsProvider;
    private String userIdColumnName;
    private String userColumnName;
    private String entitiesColumnName;
    private String userMentionsColumnName;

    public UsersWorkerHashCollection(IStratexHashDataHandler dataHandler, TweetsProvider tweetsProvider) throws UnknownHostException, NotInitializedException, InstanceInitializeException, InvalidPropertiesException {
        super();
        this.usersHashPersis = UsersHashPersistProvider.getInstance();
        this.tweetsProvider = tweetsProvider;
        this.dataHandler = dataHandler;
        this.userIdColumnName = PropertyFileLoader.getProperties().getProperty(USER_ID_PROPERTY_NAME);
        this.userColumnName = PropertyFileLoader.getProperties().getProperty(USER_PROPERTY_NAME);
        this.entitiesColumnName = PropertyFileLoader.getProperties().getProperty(ENTITIES_PROPERTY_NAME);
        this.userMentionsColumnName = PropertyFileLoader.getProperties().getProperty(USER_MENTIONS_PROPERTY_NAME);
    }

    protected void doWork() {
        Iterator<QueryResultObject> tweetsFromQueue;
        ObjectMapper jsonMapper = new ObjectMapper();
        while ((tweetsFromQueue = tweetsProvider.getTweets()) != null && tweetsFromQueue.hasNext()) {
            while (tweetsFromQueue.hasNext()) {
                try {
                    Map entities = null;
                    Map userMap = null;
                    Map user = null;
                    QueryResultObject currentTweet = tweetsFromQueue.next();
                    String userJsonString = dataHandler.getPropertyFromQueryResultObject(currentTweet, userColumnName);
                    String entitiesJsonString = dataHandler.getPropertyFromQueryResultObject(currentTweet, entitiesColumnName);
                    if (entitiesJsonString != null) {
                        entities = jsonMapper.readValue(entitiesJsonString, Map.class);
                        if (entities != null) {
                            ArrayList userMentions = (ArrayList) entities.get(userMentionsColumnName);
                            for (int i = 0; i < userMentions.size(); ++i) {
                                user = (Map) userMentions.get(i);
                                Object id = user.get(userIdColumnName);
                                if (id != null) {
                                    String userId = id.toString();
                                    usersHashPersis.queueMentionUser(Long.parseLong(userId));
                                }
                            }
                            if (userJsonString != null) {
                                userMap = jsonMapper.readValue(userJsonString, Map.class);
                                if (userMap != null) {
                                    Object id = userMap.get(userIdColumnName);
                                    if (id != null) {
                                        String userId = id.toString();
                                        usersHashPersis.queuePostUser(Long.parseLong(userId));
                                    }
                                }
                            }
                        }
                    }
                } catch (JsonMappingException e) {
                    LogUtils.logError(String.format("UsersWorkerHashCollection: %s", e.getStackTrace()));
                } catch (JsonParseException e) {
                    LogUtils.logError(String.format("UsersWorkerHashCollection: %s", e.getStackTrace()));
                } catch (IOException e) {
                    LogUtils.logError(String.format("UsersWorkerHashCollection: %s", e.getStackTrace()));
                } catch (Exception e) {
                    LogUtils.logError(String.format("UsersWorkerHashCollection: %s", e.getStackTrace()));
                }
            }
        }
    }
}
