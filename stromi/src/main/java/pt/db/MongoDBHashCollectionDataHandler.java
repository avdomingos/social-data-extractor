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
import pt.datahandler.IStratexHashDataHandler;
import pt.datahandler.StratexDataHandler;
import pt.exception.StratexDataHandlerException;
import pt.exception.WrongUsernameOrPasswordException;

import java.net.UnknownHostException;
import java.util.Iterator;
import java.util.List;

public class MongoDBHashCollectionDataHandler
        extends MongoDBDataHandler
        implements IStratexHashDataHandler<Mongo, DBCollection, DBObject, DBObject> {

    private int hashNumber;

    public MongoDBHashCollectionDataHandler(String host, int port, String username, String password, String databaseName, int hashNumber) throws UnknownHostException, WrongUsernameOrPasswordException {
        super(host, port, username, password, databaseName);
        this.hashNumber = hashNumber;
    }


    public Iterator<DBObject> findOnHashCollection(DBCollection collection, long id, DBObject statement) {
        return findOnHashCollection(collection,id,statement,-1);
    }

    public Iterator<DBObject> findOnHashCollection(DBCollection collection, long id, DBObject statement, int limit) {
        String collectionName = collection.getName();
        long collectionHashNumber = id % this.hashNumber;
        collectionName = String.format("%s_%d", collectionName, collectionHashNumber);
        if (database.collectionExists(collectionName)) {
            DBCollection hashCollection = getCollection(collectionName);
            if (limit <= 0) {
                return this.getQueryResult(hashCollection, statement);
            } else {
                return this.getQueryResult(hashCollection, statement, limit);
            }
        } else {
            return null;
        }

    }

    public void insertOnHashCollection(DBCollection collection, long id, DBObject statement) throws StratexDataHandlerException {
        String collectionName = collection.getName();
        long collectionHashNumber = id % this.hashNumber;
        collectionName = String.format("%s_%d", collectionName, collectionHashNumber);
        DBCollection hashCollection = getCollection(collectionName);
        WriteResult writeResult = hashCollection.insert(statement, writeConcertInUse);
        if (!writeResult.getLastError().ok())
            throw new StratexDataHandlerException(writeResult.getLastError().getException().getMessage());
    }
}
