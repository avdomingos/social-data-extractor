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

package pt.db;

import com.mongodb.*;
import pt.datahandler.IStratexDataHandler;
import pt.datahandler.StratexDataHandler;
import pt.exception.StratexDataHandlerException;
import pt.exception.WrongUsernameOrPasswordException;

import java.net.UnknownHostException;
import java.util.Iterator;
import java.util.List;

public class MongoDBDataHandler extends StratexDataHandler<Mongo, DBCollection, DBObject, DBObject> {

    protected DB database;

    protected WriteConcern writeConcertInUse = WriteConcern.NONE;

    public MongoDBDataHandler(String host, int port, String username, String password, String databaseName) throws UnknownHostException, WrongUsernameOrPasswordException {
        super(host, port, username, password, databaseName);
        ServerAddress serverAddress = new ServerAddress(this.host, this.port);
        dataBaseProvider = new Mongo(serverAddress);
        database = dataBaseProvider.getDB(databaseName);
        if (!username.isEmpty() && !password.isEmpty()) {
            if (!database.authenticate(username, password.toCharArray())) {
                throw new WrongUsernameOrPasswordException("Mongo Db Data Handler .ctor");
            }
        }
    }


    public DBObject getStatementObject() {
        return new BasicDBObject();
    }

    public void insertStatementCondition(DBObject statement, String fieldName, String condition) {
        statement.put(fieldName, condition);
    }

    public void insertStatementCondition(DBObject statement, String fieldName, Number condition) {
        statement.put(fieldName, condition);
    }

    public Iterator<DBObject> getQueryResult(DBCollection collection, DBObject statement) {
        return getQueryResult(collection, statement, -1);
    }

    public Iterator<DBObject> getQueryResult(DBCollection collection, DBObject statement, int limit) {
        if (limit <= 0) {
            if (statement == null) {
                return collection.find().iterator();
            } else {
                return collection.find(statement).iterator();
            }
        } else {
            if (statement == null) {
                return collection.find().limit(limit).iterator();
            } else {
                return collection.find(statement).limit(limit).iterator();
            }
        }
    }

    public Iterator<DBObject> getQueryResultOrderedBy(DBCollection collection, DBObject statement, String orderBy, OrderType orderType) {
        return getQueryResultOrderedBy(collection,statement,orderBy,orderType,-1);
    }

    public Iterator<DBObject> getQueryResultOrderedBy(DBCollection collection, DBObject statement, String orderBy, OrderType orderType, int limit) {
        DBObject dbObject = new BasicDBObject(orderBy,orderType == OrderType.ASCENDENT ? 1 : -1);
        if (limit <= 0) {
            if (statement == null) {
                return collection.find().sort(dbObject);
            } else {
                return collection.find(statement).sort(dbObject);
            }
        } else {
            if (statement == null) {
                return collection.find().sort(dbObject).limit(limit);
            } else {
                return collection.find(statement).sort(dbObject).limit(limit);
            }
        }
    }

    public DBCollection getCollection(String collectionName) {
        return database.getCollection(collectionName);
    }

    public void insertStatement(DBCollection collection, DBObject statement) throws StratexDataHandlerException {
        WriteResult writeResult = collection.insert(statement,writeConcertInUse);
        if (!writeResult.getLastError().ok())
            throw new StratexDataHandlerException(writeResult.getLastError().getException().getMessage());
    }

    public void insertStatement(DBCollection collection, List<DBObject> statement) throws StratexDataHandlerException {
        WriteResult writeResult = collection.insert(statement,writeConcertInUse);
        if (!writeResult.getLastError().ok())
            throw new StratexDataHandlerException(writeResult.getLastError().getException().getMessage());
    }
    public void insertQueryResultObject(DBCollection collection, DBObject dbObject) throws StratexDataHandlerException {
        WriteResult writeResult = collection.insert(dbObject,writeConcertInUse);
        if (!writeResult.getLastError().ok())
            throw new StratexDataHandlerException(writeResult.getLastError().getException().getMessage());
    }

    public void update(DBCollection collection, DBObject queryResultObjectNew, DBObject queryResultObjectDirty) throws StratexDataHandlerException {
        WriteResult writeResult = collection.update(queryResultObjectDirty,queryResultObjectNew);
        if (!writeResult.getLastError().ok())
            throw new StratexDataHandlerException(writeResult.getLastError().getException().getMessage());
    }

    public void remove(DBCollection collection, DBObject dbObject) throws StratexDataHandlerException {
        WriteResult writeResult = collection.remove(dbObject,writeConcertInUse);
        if (!writeResult.getLastError().ok())
            throw new StratexDataHandlerException(writeResult.getLastError().getException().getMessage());
    }

    public String getPropertyFromQueryResultObject(DBObject dbObject, String propertyName) {
        if (dbObject.containsField(propertyName)) {
            return dbObject.get(propertyName).toString();
        } else {
            return null;
        }
    }

    public long getCountOfAvailableElementsInCollection(String collectionName) {
        return database.getCollection(collectionName).count();
    }

    public void dropCollection(String collectionName) {
        database.getCollection(collectionName).drop();
    }

    public void beingTransaction() {
        database.requestStart();
        database.requestEnsureConnection();
    }

    public void commitTransaction() {
        database.requestDone();
    }

    public void closeConnection() {
        database.getMongo().close();
    }
}
