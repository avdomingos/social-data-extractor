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

package pt.utils;

import pt.db.MongoDBDataHandler;
import pt.exception.InstanceInitializeException;
import pt.exception.NotInitializedException;
import pt.intance.provider.MongoConnectionInstanceProvider;

/**
 * Created with IntelliJ IDEA.
 * User: Crack
 * Date: 13-06-2012
 * Time: 13:48
 * To change this template use File | Settings | File Templates.
 */
public class Utils {

    public static MongoDBDataHandler getMongoDBDataHandler() throws InstanceInitializeException, NotInitializedException {
        return MongoConnectionInstanceProvider.getInstance();
    }

}
