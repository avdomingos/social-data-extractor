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

import pt.exception.StratexDataHandlerException;

import java.util.Iterator;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Crack
 * Date: 10-07-2012
 * Time: 21:14
 * To change this template use File | Settings | File Templates.
 */
public interface IStratexDataHandler<DataBaseProvider, CollectionObject, QueryResultObject, StatementObject> {

    public enum OrderType{
        ASCENDENT,
        DESCENDENT
    }

    public abstract StatementObject getStatementObject();

    public abstract void insertStatementCondition(StatementObject statement, String fieldName, String condition);

    public abstract void insertStatementCondition(StatementObject statement, String fieldName, Number condition);

    public abstract void insertStatement(CollectionObject collection, StatementObject statement) throws StratexDataHandlerException;

    public abstract void insertStatement(CollectionObject collection, List<StatementObject> statement) throws StratexDataHandlerException;

    public abstract void insertQueryResultObject(CollectionObject collection, QueryResultObject queryResultObject) throws StratexDataHandlerException;

    public abstract void update(CollectionObject collection, StatementObject queryResultObjectNew, QueryResultObject queryResultObjectDirty) throws StratexDataHandlerException;

    public abstract Iterator<QueryResultObject> getQueryResult(CollectionObject collection, StatementObject statement);

    public abstract Iterator<QueryResultObject> getQueryResult(CollectionObject collection, StatementObject statement, int limit);

    public abstract Iterator<QueryResultObject> getQueryResultOrderedBy(CollectionObject collection, StatementObject statement, String orderBy, OrderType orderType);

    public abstract Iterator<QueryResultObject> getQueryResultOrderedBy(CollectionObject collection, StatementObject statement, String orderBy, OrderType orderType, int limit);

    public abstract CollectionObject getCollection(String collectionName);

    public abstract void remove(CollectionObject collection, QueryResultObject queryResultObject) throws StratexDataHandlerException;

    public abstract String getPropertyFromQueryResultObject(QueryResultObject queryResultObject, String propertyName);

    public abstract long getCountOfAvailableElementsInCollection(String collectionName);

    public abstract void dropCollection(String collectionName);

    public abstract void beingTransaction();

    public abstract void commitTransaction();

    public abstract void closeConnection();
}
