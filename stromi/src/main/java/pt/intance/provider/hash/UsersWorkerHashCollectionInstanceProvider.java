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

package pt.intance.provider.hash;

import pt.exception.InstanceInitializeException;
import pt.exception.InvalidPropertiesException;
import pt.exception.NotInitializedException;
import pt.utils.LogUtils;
import pt.worker.mongoHash.TweetsProvider;
import pt.worker.mongoHash.UsersWorkerHashCollection;

import java.net.UnknownHostException;


/**
 * Created with IntelliJ IDEA.
 * User: Crack
 * Date: 28-07-2012
 * Time: 18:52
 * To change this template use File | Settings | File Templates.
 */
public class UsersWorkerHashCollectionInstanceProvider {

    private static final Object SYNC_OBJ = new Object();

    public static UsersWorkerHashCollection init() throws InstanceInitializeException {
        synchronized (SYNC_OBJ) {
            try {
                return new UsersWorkerHashCollection(MongoHashConnectionInstanceProvider.getInstance(), TweetsProvider.getInstance());
            } catch (UnknownHostException e) {
                throw new InstanceInitializeException(String.format("Unknown Host Exception %s", e.getMessage()));
            } catch (NotInitializedException e) {
                throw new InstanceInitializeException(String.format("Not Initialized Exception %s", e.getMessage()));
            } catch (InvalidPropertiesException e) {
                throw new InstanceInitializeException(String.format("Invalid properties: %s", e.getMessage()));
            }

        }
    }

    public static UsersWorkerHashCollection getInstance() throws InstanceInitializeException {
        LogUtils.logInfo("UsersWorkerHashCollection: New Instance Initialized");
        return init();
    }
}
