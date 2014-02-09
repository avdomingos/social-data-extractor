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

package pt.datahandler;


//TODO: Remove examples from JavaDoc

import pt.exception.StratexDataHandlerException;

import java.util.Iterator;
import java.util.List;

/**
 * Data handler to handle data from a data source
 *
 * @param <DataBaseProvider>  - A DataBase provider - (Mongo.class)
 * @param <CollectionObject>  - A database table/collection (DBCollection.class)
 * @param <QueryResultObject> - A key-value map that represents a topple/query result. (DBObject.class)
 * @param <StatementObject>   - An object that is used by a CollectionObject to make a query (BasicDBObject)
 */
public abstract class StratexDataHandler<DataBaseProvider, CollectionObject, QueryResultObject, StatementObject> implements IStratexDataHandler<DataBaseProvider, CollectionObject, QueryResultObject, StatementObject> {

    protected DataBaseProvider dataBaseProvider;
    protected String host;
    protected int port;
    protected String username;
    protected String password;
    protected String dataBaseName;

    public StratexDataHandler(String host, int port, String username, String password, String databaseName) {
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
        dataBaseName = databaseName;
    }
}
