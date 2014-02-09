/*
*	This file is part of SINC.
*
*    SINC is free software: you can redistribute it and/or modify
*    it under the terms of the GNU General Public License as published by
*    the Free Software Foundation, either version 3 of the License, or
*    (at your option) any later version.
*
*    SINC is distributed in the hope that it will be useful,
*    but WITHOUT ANY WARRANTY; without even the implied warranty of
*    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
*    GNU General Public License for more details.
*
*    You should have received a copy of the GNU General Public License
*    along with SINC.  If not, see <http://www.gnu.org/licenses/>.
*/

package pt.sinc.data.input.interaction.mongo;

import com.mongodb.*;
import pt.sinc.PropertyLoader;
import pt.sinc.data.input.interaction.ISocialDataProvider;
import pt.sinc.data.input.interaction.exception.SocialDataProviderException;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

public class MongoDBInteractionProvider implements ISocialDataProvider {


    private String hostname;
    private String database;
    private int port;
    private String collName;
    private String username;
    private String password;

    private static final String maxTweetsForQueuePropertyKey = "MAX_TWEETS_QUEUE_SIZE";
    private static final String hostnamePropertyKey = "HOST";
    private static final String databasePropertyKey = "DATABASE";
    private static final String portPropertyKey = "PORT";
    private static final String collectionPropertyKey = "COLLECTION";
    private static final String usernamePropertyKey = "USERNAME";
    private static final String passwordPropertyKey = "PASSWORD";

    protected Mongo mongoConnection;
    protected Iterator<DBObject> cursor;

    private static int maxTweetsForQueue;

    private final static Object lockObject = new Object();

    public MongoDBInteractionProvider() throws MongoException, IOException {
        initializeDBDataFromProperties();

        // TODO: Validate if connection is valid, mongoDB driver doesn't do this for us (it's too "lazy")
        mongoConnection = new Mongo(hostname, port);
        DB db = mongoConnection.getDB(database);
        if (username != null && password != null && !db.authenticate(username, password.toCharArray())) {
            throw new MongoException("Invalid username and/or password");
        }
        DBCollection collection = db.getCollection(collName);
        cursor = collection.find().iterator();
    }

    private synchronized void initializeDBDataFromProperties() throws IOException {
        Properties props = PropertyLoader.getProperties(this.getClass().getSimpleName());
        //maxTweetsForQueue = Integer.parseInt(props.getProperty(maxTweetsForQueuePropertyKey));
        hostname = props.getProperty(hostnamePropertyKey);
        database = props.getProperty(databasePropertyKey);
        port = Integer.parseInt(props.getProperty(portPropertyKey));
        collName = props.getProperty(collectionPropertyKey);

        if (props.containsKey(usernamePropertyKey) && props.containsKey(passwordPropertyKey)) {
            username = props.getProperty(usernamePropertyKey);
            password = props.getProperty(passwordPropertyKey);
        }
    }

    public String getNextInteraction() {
        synchronized (lockObject) {
            return cursor.next().toString();
        }
    }

    public List<String> getNextInteractions(int numberOfInteractions) throws SocialDataProviderException {
        int count = 0;
        List<String> items = new LinkedList<String>();
        synchronized (lockObject) {
            while (count < numberOfInteractions && cursor.hasNext()) {
                items.add(cursor.next().toString());
                ++count;
            }
            return items;
        }
    }


    public boolean hasMoreInteractions() {
        synchronized (lockObject)
        {
            return cursor.hasNext();
        }
    }

    /**
     * Performs any necessary cleanup operations
     */
    public void close() {
    }
}
