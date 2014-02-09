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

package stratex.twitter.extractor.datahandlers.mongodb;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import stratex.db.MongoDBDataManager;
import stratex.log.StratexLogger;
import stratex.properties.PropertyLoader;
import stratex.twitter.extractor.datahandlers.IDataHandler;
import stratex.twitter.extractor.datahandlers.exception.DataHandlerException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;


public class MongoDBDataHandler implements IDataHandler {
    MongoDBDataManager manager;
    private static String DEFAULT_HOST = "localhost";
    private static String DEFAULT_DATABASE = "stratex";
    private static String DEFAULT_COLLECTION = "TweetQueue";
    private static int DEFAULT_PORT = 27017;
    private static boolean DEFAULT_OUTPUT_DATA_TO_CONSOLE = true;

    private boolean outputToConsole = DEFAULT_OUTPUT_DATA_TO_CONSOLE;

    public MongoDBDataHandler() throws IOException {
        Properties props = PropertyLoader.getProperties(this.getClass().getSimpleName());
        if (props == null) {
            manager = new MongoDBDataManager(DEFAULT_HOST, DEFAULT_DATABASE, DEFAULT_COLLECTION, DEFAULT_PORT);
            Map<String, String> defaultData = new HashMap<java.lang.String, java.lang.String>();
            defaultData.put("HOST", DEFAULT_HOST);
            defaultData.put("DATABASE", DEFAULT_DATABASE);
            defaultData.put("COLLECTION", DEFAULT_COLLECTION);
            defaultData.put("PORT", "" + DEFAULT_PORT);
            defaultData.put("OUTPUT_DATA_TO_CONSOLE", "" + DEFAULT_OUTPUT_DATA_TO_CONSOLE);
            PropertyLoader.generatePropertiesFile(this.getClass().getSimpleName(), defaultData);
        } else {
            if (props.containsKey("USERNAME") && props.containsKey("PASSWORD")) {
                manager = new MongoDBDataManager(
                        props.get("HOST").toString(),
                        props.get("DATABASE").toString(),
                        props.get("COLLECTION").toString(),
                        Integer.parseInt(props.get("PORT").toString()),
                        props.get("USERNAME").toString(),
                        props.get("PASSWORD").toString().toCharArray()
                );
            } else {
                manager = new MongoDBDataManager(
                        props.get("HOST").toString(),
                        props.get("DATABASE").toString(),
                        props.get("COLLECTION").toString(),
                        Integer.parseInt(props.get("PORT").toString())
                );
            }
            outputToConsole = Boolean.parseBoolean(props.get("OUTPUT_DATA_TO_CONSOLE").toString());
        }
    }


    public void handleData(String data) throws DataHandlerException {
        if (outputToConsole)
            StratexLogger.logInfo(data);
        ObjectMapper mapper = new ObjectMapper();
        if (data == null || data.equals(""))
            throw new DataHandlerException("Data to process is invalid.");
        try {
            Map twitterData = mapper.readValue(data, Map.class);
            DBObject obj = new BasicDBObject();
            obj.putAll(twitterData);
            manager.saveData(obj);

        } catch (IOException e) {
            StratexLogger.logError(e.getMessage(),e);
            throw new DataHandlerException("MongoDBDataHandler: An error has occurred while handling data.", e);

        }
    }
}
