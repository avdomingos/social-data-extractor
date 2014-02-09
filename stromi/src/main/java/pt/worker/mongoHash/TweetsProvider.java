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
import pt.datahandler.StratexDataHandler;
import pt.exception.InvalidPropertiesException;
import pt.exception.NotInitializedException;
import pt.exception.StratexDataHandlerException;
import pt.properties.PropertyFileLoader;
import pt.utils.LogUtils;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.*;

public class TweetsProvider<DataBaseProvider, CollectionObject, QueryResultObject, Statement>{

    /* PROPERTIES NAMES */
    private static final String NUMBER_OF_TWEETS_TO_RETURN_PROPERTY_NAME = "tweetsProvider_numberOfTweetsToReturn";
    private static final String TWEETS_COLLECTION_NAME_PROPERTY_NAME = "tweetsCollectionName";
    private static final String TWEETS_QUEUE_COLLECTION_NAME_PROPERTY_NAME = "tweetQueueCollectionName";

    /*Concurrency objects*/
    private static Object lockObjectToInit = new Object();

    /* Singleton object */
    private static TweetsProvider singleton;

    /*database properties*/
    private StratexDataHandler<DataBaseProvider, CollectionObject, QueryResultObject, Statement> dataHandler;
    private CollectionObject tweetQueue;
    private CollectionObject tweets;
    private String tweetsCollectionName;
    private String tweetQueueCollectionName;

    private int numberOfTweetsToReturn;

    public static void init(StratexDataHandler dataHandler) throws UnknownHostException, InvalidPropertiesException {
          synchronized (lockObjectToInit){
              singleton = new TweetsProvider(dataHandler);
          }
    }

    public static TweetsProvider getInstance() throws NotInitializedException {
        if(singleton == null) throw new NotInitializedException("Singleton was not initialized");
        return singleton;
    }

    public TweetsProvider(StratexDataHandler<DataBaseProvider, CollectionObject, QueryResultObject, Statement> dataHandler) throws UnknownHostException, InvalidPropertiesException {
        Properties properties = PropertyFileLoader.getProperties();
        this.numberOfTweetsToReturn = Integer.parseInt(properties.getProperty(NUMBER_OF_TWEETS_TO_RETURN_PROPERTY_NAME));
        this.tweetsCollectionName = properties.getProperty(TWEETS_COLLECTION_NAME_PROPERTY_NAME);
        this.tweetQueueCollectionName = properties.getProperty(TWEETS_QUEUE_COLLECTION_NAME_PROPERTY_NAME);

        this.dataHandler = dataHandler;
        this.tweets = dataHandler.getCollection(this.tweetsCollectionName);
        this.tweetQueue = dataHandler.getCollection(this.tweetQueueCollectionName);
    }

    public synchronized Iterator<QueryResultObject> getTweets(){
            LinkedList<QueryResultObject> listToReturn = new LinkedList<QueryResultObject>();
            Iterator<QueryResultObject> tweetsFromQueue = dataHandler.getQueryResult(tweetQueue, null,numberOfTweetsToReturn);
            try {
                while (tweetsFromQueue.hasNext()) {
                    QueryResultObject currentTweet = tweetsFromQueue.next();
                    listToReturn.add(currentTweet);
                    dataHandler.remove(tweetQueue, currentTweet);
                    dataHandler.insertQueryResultObject(tweets, currentTweet);
                }
            } catch (StratexDataHandlerException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
            return listToReturn.iterator();
    }
}
