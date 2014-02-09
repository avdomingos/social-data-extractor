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

package stratex.db;

import com.mongodb.*;
import stratex.log.StratexLogger;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

/**
 * Class for accessing MongoDB Collections. NoteS: When creating a new MongoDBDataManager the constructor will attempt to access the specified collection.
 * If the database isn't accessible an exception is thrown. This is due to a limitation of the MongoDB Java Driver.
 */
public class MongoDBDataManager {
    DB db;
    DBCollection dbCollection;
    private static final int DEFAULT_PORT = 27017;

    /**
     * Initializes the MongoDBDataManager.
     * @param host database host. E.g. "localhost"
     * @param database database name. E.g. "stratex"
     * @param collection collection to access. E.g. "tweets"
     * @param port database port. Default is 27017.
     * @param username username for authentication
     * @param password password for authentication
     * @throws UnknownHostException
     */
    public MongoDBDataManager(String host, String database, String collection, int port, String username, char[] password) throws UnknownHostException {
        Mongo mongoConnection = new Mongo(host, port);
        db = mongoConnection.getDB(database);

        dbCollection = db.getCollection(collection);
        if (!db.authenticate(username, password))
        {
            MongoException ex = new MongoException("Invalid username and/or password");
            StratexLogger.logWarn("Error authenticating mongoDB.", ex);
            throw ex;
        }
    }

    /**
     * Initializes the MongoDBDataManager. No authentication is performed.
     * @param host database host. E.g. "localhost"
     * @param database database name. E.g. "stratex"
     * @param collection collection to access. E.g. "tweets"
     * @param port database port. Default is 27017.
     * @throws UnknownHostException
     */
    public MongoDBDataManager(String host, String database, String collection, int port) throws UnknownHostException {
        Mongo mongoConnection = new Mongo(host, port);
        db = mongoConnection.getDB(database);
        dbCollection = db.getCollection(collection);
    }

    /**
     * Initializes the MongoDBDataManager using the default port. Also, no authentication is performed.
     * @param host database host. E.g. "localhost"
     * @param database database name. E.g. "stratex"
     * @param collection collection to access. E.g. "tweets"
     * @throws UnknownHostException
     */
    public MongoDBDataManager(String host, String database, String collection) throws UnknownHostException {
        Mongo mongoConnection = new Mongo(host, DEFAULT_PORT);
        db = mongoConnection.getDB(database);
        dbCollection = db.getCollection(collection);
    }

    /**
     * Obtains the size of the collection on which the current MongoDBDataManager is working with.
     * @return size of the collection
     */
    public long getCollectionSize() {
        return dbCollection.getCount();
    }

    /**
     * Saves a DBObject to the collection in use by the current MongoDBDataManager instance.
     * @param obj the object to save
     * @throws MongoException
     */
    public void saveData(DBObject obj) throws MongoException {
        WriteResult result = dbCollection.save(obj);
        if (!result.getLastError().ok())
        {
            MongoException ex = result.getLastError().getException();
            StratexLogger.logWarn("Error while saving data", ex);
            throw ex;
        }
    }

    /**
     * Gets nElements from a mongoDB collection
     * @param nElements total number of elements to return
     * @return returns n elements of a collection up to a maximum of @param
     */
    public List<BasicDBObject> getNElementsFromCollection(int nElements) {
        ArrayList<BasicDBObject> ret = new ArrayList<BasicDBObject>();
        DBCursor cursor = dbCollection.find();
        for (int i = 0; i < nElements && cursor.hasNext(); ++i) {
            ret.add((BasicDBObject) cursor.next());
        }
        return ret;
    }

    /**
     * Removes a list of BasicDBObject from the collection
     * @param objsToRemove the objects to remove
     */
    public void removeObjects(List<BasicDBObject> objsToRemove) {
        for(DBObject obj : objsToRemove)
            dbCollection.remove(obj);
    }

    public boolean authenticate(String username, char[] password)
    {
        return db.authenticate(username, password);
    }

    public DBObject getDocumentByValueName(String name, String value) {
        BasicDBObject obj = new BasicDBObject(name, value);
        return dbCollection.findOne(obj);
    }
}
