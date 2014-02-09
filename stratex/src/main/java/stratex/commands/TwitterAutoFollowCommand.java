/*
*	This file is part of STRATEX.
*
*    STRATEX is free software: you can redistribute it and/or modify
*    it under the terms of the GNU General Public License as published by
*    the Free Software Foundation, either version 3 of the License, or
*    (at your option) any later version.
*
*    STRATEX is distributed in the hope that it will be useful,
*    but WITHOUT ANY WARRANTY; without even the implied warranty of
*    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
*    GNU General Public License for more details.
*
*    You should have received a copy of the GNU General Public License
*    along with STRATEX.  If not, see <http://www.gnu.org/licenses/>.
*/

package stratex.commands;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import pt.command.annotation.CommandAnnotation;
import pt.command.exception.ExitWithoutSuccessException;
import stratex.db.MongoDBDataManager;
import stratex.exceptions.StratexException;
import stratex.log.StratexLogger;
import stratex.properties.PropertyLoader;
import stratex.twitter.extractor.BaseExtractor;
import stratex.twitter.provider.parameter.AbstractParameter;
import stratex.twitter.provider.parameter.streamingAPI.Follow;
import stratex.twitter.provider.parameter.streamingAPI.StreamingAPIMethod;

import java.io.IOException;
import java.io.PrintStream;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Map;


@CommandAnnotation
(
    command = "TwitterAutoFollow",
    usage = "TwitterAutoFollow -networkAddress <xxx.xxx.xxx.xxx> -username <username> -password <password>",
    description = "Fetches a list of users from mongoDB and follows users using Twitter's StreamingAPI"
)
public class TwitterAutoFollowCommand extends TwitterCommand {

    private static final int MAX_NUMBER_OF_USERS_TO_FOLLOW = 5000;
    private static final String APPLICATION_NAME = "Stratex";

    private static final String USERS_IN_PROCESS_DATABASE_HOST = "UsersInProcessDatabaseHost";
    private static final String USERS_IN_PROCESS_DATABASE_NAME = "UsersInProcessDatabaseName";
    private static final String USERS_IN_PROCESS_DATABASE_COLLECTION = "UsersInProcessDatabaseCollection";
    private static final String USERS_IN_PROCESS_DATABASE_PORT = "UsersInProcessDatabasePort";
    private static final String USERS_IN_PROCESS_DATABASE_USERNAME = "UsersInProcessDatabaseUsername";
    private static final String USERS_IN_PROCESS_DATABASE_PASSWORD = "UsersInProcessDatabasePassword";
    private static final String PENDING_USERS_DATABASE_HOST = "PendingUsersDatabaseHost";
    private static final String PENDING_USERS_DATABASE_NAME = "PendingUsersDatabaseName";
    private static final String PENDING_USERS_DATABASE_COLLECTION = "PendingUsersDatabaseCollection";
    private static final String PENDING_USERS_DATABASE_PORT = "PendingUsersDatabasePort";
    private static final String PENDING_USERS_DATABASE_USERNAME = "PendingUsersDatabaseUsername";
    private static final String PENDING_USERS_DATABASE_PASSWORD = "PendingUsersDatabasePassword";

    private static final String ERROR_MSG_PROBLEM_READING_CONFIGURATION_FILE = "Problem reading configuration file.";
    private static final String ERROR_MSG_ERROR_WITH_HOST = "Error with host.";
    private static final String ERROR_MSG_STRATEX_ERROR = "Stratex error.";
    private static final String ERROR_MSG_IO_ERROR = "IO error.";
    private static final String ERROR_MSG_GENERIC_EXCEPTION_CAUGHT = "Generic Exception caught.";
    private static final String ERROR_MSG_NO_USERS_TO_FOLLOW = "No elements in 'PendingUsers' collection. No users to follow.";

    public TwitterAutoFollowCommand() {
        super();
    }

    @Override
    protected void commandAction(PrintStream printStream, Map<String, String> stringStringMap) throws ExitWithoutSuccessException {
        BaseExtractor baseExtractor = null;
        try {
            baseExtractor = this.initializeBaseExtractor();
            StreamingAPIMethod method = StreamingAPIMethod.STATUSES_FILTER;

            // Connect to MongoDB database and get ids to follow
            MongoDBDataManager pendingUsersManager = getMongoDBManagerForPendingUsersCollection();
            MongoDBDataManager usersInProcessManager = getMongoDBManagerForUsersInProcessCollection();


            List<BasicDBObject> objs = pendingUsersManager.getNElementsFromCollection(MAX_NUMBER_OF_USERS_TO_FOLLOW);
            if (pendingUsersManager.getCollectionSize() == 0)
                throw new StratexException(ERROR_MSG_NO_USERS_TO_FOLLOW);

            long[] userIds = new long[objs.toArray().length];

            for (int i = 0; i < userIds.length; ++i) {
                DBObject aux = objs.get(i);
                if (aux.get("userID") != null) {
                    userIds[i] = (long) Double.parseDouble(aux.get("userID").toString());
                    usersInProcessManager.saveData(aux);
                }
            }
            pendingUsersManager.removeObjects(objs);

        baseExtractor.extract(method, new AbstractParameter[] { new Follow(userIds)});
        } catch (UnknownHostException e) {
            StratexLogger.logError(ERROR_MSG_ERROR_WITH_HOST,e);
        } catch (StratexException e) {
            StratexLogger.logError(ERROR_MSG_STRATEX_ERROR, e);
        } catch (IOException e) {
            StratexLogger.logError(ERROR_MSG_IO_ERROR, e);
        } catch (Exception e) {
            StratexLogger.logError(ERROR_MSG_GENERIC_EXCEPTION_CAUGHT, e);
        }


    }

    private static MongoDBDataManager getMongoDBManagerForUsersInProcessCollection() throws IOException {
        return getDataManagerFromProperties
                (
                        USERS_IN_PROCESS_DATABASE_HOST,
                        USERS_IN_PROCESS_DATABASE_NAME,
                        USERS_IN_PROCESS_DATABASE_COLLECTION,
                        USERS_IN_PROCESS_DATABASE_PORT,
                        USERS_IN_PROCESS_DATABASE_USERNAME,
                        USERS_IN_PROCESS_DATABASE_PASSWORD
                );
    }

    private static MongoDBDataManager getMongoDBManagerForPendingUsersCollection() throws IOException {
        return getDataManagerFromProperties
                (
                        PENDING_USERS_DATABASE_HOST,
                        PENDING_USERS_DATABASE_NAME,
                        PENDING_USERS_DATABASE_COLLECTION,
                        PENDING_USERS_DATABASE_PORT,
                        PENDING_USERS_DATABASE_USERNAME,
                        PENDING_USERS_DATABASE_PASSWORD
                );
    }

    private static MongoDBDataManager getDataManagerFromProperties
            (
                    String hostKey,
                    String databaseKey,
                    String collectionKey,
                    String portKey,
                    String usernameKey,
                    String passwordKey
            ) throws IOException {
        try {
            String host = PropertyLoader.getProperties(APPLICATION_NAME).get(hostKey).toString();
            String db = PropertyLoader.getProperties(APPLICATION_NAME).get(databaseKey).toString();
            int port = Integer.parseInt(PropertyLoader.getProperties(APPLICATION_NAME).get(portKey).toString());
            String collection = PropertyLoader.getProperties(APPLICATION_NAME).get(collectionKey).toString();

            if (PropertyLoader.getProperties(APPLICATION_NAME).containsKey(usernameKey) && PropertyLoader.getProperties(APPLICATION_NAME).containsKey(passwordKey)) {
                String username = PropertyLoader.getProperties(APPLICATION_NAME).get(usernameKey).toString();
                char[] password = PropertyLoader.getProperties(APPLICATION_NAME).get(passwordKey).toString().toCharArray();
                return new MongoDBDataManager(host, db, collection, port, username, password);
            }
            return new MongoDBDataManager(host, db, collection, port);
        } catch (NullPointerException e) {
            throw new IOException(ERROR_MSG_PROBLEM_READING_CONFIGURATION_FILE, e);
        }


    }
}
