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

package pt.stromi.providers;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import junit.framework.Assert;
import org.bson.BSON;
import org.bson.BSONObject;
import org.bson.BasicBSONObject;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import pt.db.MongoDBDataHandler;
import pt.exception.InstanceInitializeException;
import pt.exception.NotInitializedException;
import pt.exception.StratexDataHandlerException;
import pt.utils.Utils;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class MongoDBProviderTest {


    private static final String COLLECTION_TEST = "TEST";
    private static final String COLLECTION_FROM_TEST = "FROM_TEST";
    private static final String COLLECTION_TO_TEST = "TO_TEST";

    @Before
    public void beforeTests() throws InstanceInitializeException, NotInitializedException {
        //Create collection test
        Utils.getMongoDBDataHandler().getCollection(COLLECTION_TEST).drop();
        Utils.getMongoDBDataHandler().getCollection(COLLECTION_FROM_TEST).drop();
        Utils.getMongoDBDataHandler().getCollection(COLLECTION_TO_TEST).drop();
    }

    @AfterClass
    public static void afterTests() {
        //Drop collection test
        try {
            Utils.getMongoDBDataHandler().dropCollection(COLLECTION_TEST);
            Utils.getMongoDBDataHandler().dropCollection(COLLECTION_FROM_TEST);
            Utils.getMongoDBDataHandler().dropCollection(COLLECTION_TO_TEST);
        } catch (InstanceInitializeException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (NotInitializedException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    @Test
    public void testMongoDBInsertAndGetSingleObject() throws InstanceInitializeException, NotInitializedException, StratexDataHandlerException {
        MongoDBDataHandler dataHandler = Utils.getMongoDBDataHandler();
        DBCollection collection = dataHandler.getCollection(COLLECTION_TEST);
        BSONObject bson = new BasicBSONObject();
        bson.put("_id", "12345");
        bson.put("user", "userName");
        bson.put("userID", "1");

        BasicDBObject obj = new BasicDBObject();
        obj.putAll(bson);
        dataHandler.insertStatement(collection, obj);

        Iterator<DBObject> iterator = dataHandler.getQueryResult(collection, obj);
        Assert.assertTrue(iterator.hasNext());
        Assert.assertEquals(iterator.next(), bson);
        Assert.assertFalse(iterator.hasNext());
    }
    @Test
    public void testMongoDBInsertAndGetMultipleObjects() throws InstanceInitializeException, NotInitializedException, StratexDataHandlerException {
        MongoDBDataHandler dataHandler = Utils.getMongoDBDataHandler();
        DBCollection collection = dataHandler.getCollection(COLLECTION_TEST);

        List<DBObject> bsonObjectList = new LinkedList<DBObject>();

        int insertions = 1000;
        for (int i = 0; i < insertions; ++i) {
            BSONObject bson = new BasicBSONObject();
            bson.put("_id", i);
            bson.put("user", "userName_" + i);
            bson.put("userID", i);
            BasicDBObject obj = new BasicDBObject();
            obj.putAll(bson);
            bsonObjectList.add(obj);
        }
        dataHandler.insertStatement(collection, bsonObjectList);
        Iterator<DBObject> iterator = dataHandler.getQueryResult(collection, null);
        int count = 0;
        while (iterator.hasNext()) {
            Assert.assertTrue(bsonObjectList.contains(iterator.next()));
            ++count;
        }
        Assert.assertEquals(insertions,count);
    }
    @Test
    public void testMongoDBGetRemoveAndWriteInNewCollection() throws InstanceInitializeException, NotInitializedException, StratexDataHandlerException {
        MongoDBDataHandler dataHandler = Utils.getMongoDBDataHandler();
        DBCollection collectionFrom = dataHandler.getCollection(COLLECTION_FROM_TEST);
        DBCollection collectionTo = dataHandler.getCollection(COLLECTION_TO_TEST);

        List<DBObject> bsonObjectList = new LinkedList<DBObject>();

        int insertions = 10000;
        for (int i = 0; i < insertions; ++i) {
            BSONObject bson = new BasicBSONObject();
            bson.put("_id", i);
            bson.put("user", "userName_" + i);
            bson.put("userID", i);
            BasicDBObject obj = new BasicDBObject();
            obj.putAll(bson);
            bsonObjectList.add(obj);
        }
        dataHandler.insertStatement(collectionFrom, bsonObjectList);
        Iterator<DBObject> iterator = dataHandler.getQueryResult(collectionFrom, null);
        while(iterator.hasNext()){
            DBObject object = iterator.next();
            dataHandler.remove(collectionFrom,object);
            dataHandler.insertStatement(collectionTo,object);
        }
        Iterator<DBObject> iteratorGetEmptyCollection = dataHandler.getQueryResult(collectionFrom,null);
        Assert.assertNotNull(iteratorGetEmptyCollection);
        Assert.assertFalse(iteratorGetEmptyCollection.hasNext());
        Iterator<DBObject> iteratorCollectionTo = dataHandler.getQueryResult(collectionTo, null);
        int count = 0;
        while (iteratorCollectionTo.hasNext()) {
            Assert.assertTrue(bsonObjectList.contains(iteratorCollectionTo.next()));
            ++count;
        }
        Assert.assertEquals(insertions,count);
    }
    @Test
    public void testMongoDBGetMoreThanExists() throws InstanceInitializeException, NotInitializedException, StratexDataHandlerException {
        MongoDBDataHandler dataHandler = Utils.getMongoDBDataHandler();
        DBCollection collection = dataHandler.getCollection(COLLECTION_TEST);
        List<DBObject> bsonObjectList = new LinkedList<DBObject>();
        int expected = 10000;
        for (int i = 0; i < expected; ++i) {
            BSONObject bson = new BasicBSONObject();
            bson.put("_id", i);
            bson.put("user", "userName_" + i);
            bson.put("userID", i);
            BasicDBObject obj = new BasicDBObject();
            obj.putAll(bson);
            bsonObjectList.add(obj);
        }
        dataHandler.insertStatement(collection, bsonObjectList);
        Iterator<DBObject> iterator = dataHandler.getQueryResult(collection, null,20000);
        int returnedElementsCount = 0;
        while(iterator.hasNext()){
            Assert.assertNotNull(iterator.next());
            ++returnedElementsCount;
        }
        Assert.assertEquals(expected,returnedElementsCount);
    }
    @Test
    public void testMongoDBGetLessThanExists() throws InstanceInitializeException, NotInitializedException, StratexDataHandlerException {
        MongoDBDataHandler dataHandler = Utils.getMongoDBDataHandler();
        DBCollection collection = dataHandler.getCollection(COLLECTION_TEST);
        List<DBObject> bsonObjectList = new LinkedList<DBObject>();
        int inserted = 50;
        int expected = 25;
        for (int i = 0; i < inserted; ++i) {
            BSONObject bson = new BasicBSONObject();
            bson.put("_id", i);
            bson.put("user", "userName_" + i);
            bson.put("userID", i);
            BasicDBObject obj = new BasicDBObject();
            obj.putAll(bson);
            bsonObjectList.add(obj);
        }
        dataHandler.insertStatement(collection, bsonObjectList);
        Iterator<DBObject> iterator = dataHandler.getQueryResult(collection, null,expected);
        int returnedElementsCount = 0;
        while(iterator.hasNext()){
            Assert.assertNotNull(iterator.next());
            ++returnedElementsCount;
        }
        Assert.assertEquals(expected,returnedElementsCount);
    }
}
