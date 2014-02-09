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
public interface IStratexHashDataHandler<DataBaseProvider, CollectionObject, QueryResultObject, StatementObject> extends IStratexDataHandler<DataBaseProvider, CollectionObject, QueryResultObject, StatementObject> {
    public abstract Iterator<QueryResultObject> findOnHashCollection(CollectionObject collection, long id, StatementObject statement);
    public abstract Iterator<QueryResultObject> findOnHashCollection(CollectionObject collection, long id, StatementObject statement, int limit);
    public abstract void insertOnHashCollection(CollectionObject collection, long id, StatementObject statement) throws StratexDataHandlerException;
    //TODO:public abstract void insertOnHashCollection(CollectionObject collection, List<StatementObject> statement) throws StratexDataHandlerException;
}
