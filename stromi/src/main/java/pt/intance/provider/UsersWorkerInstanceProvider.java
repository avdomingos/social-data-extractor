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

package pt.intance.provider;

import pt.exception.InstanceInitializeException;
import pt.exception.InvalidPropertiesException;
import pt.exception.NotInitializedException;
import pt.properties.PropertyFileLoader;
import pt.utils.LogUtils;
import pt.worker.UsersWorker;

import java.net.UnknownHostException;
import java.util.Properties;

public class UsersWorkerInstanceProvider {

    private static volatile UsersWorker usersWorker;
    private static volatile Properties properties;
    private static final Object SYNC_OBJ = new Object();

    public static void init() throws InstanceInitializeException {
        synchronized (SYNC_OBJ) {
            if (usersWorker == null) {
                try {
                    if (properties == null) {
                        properties = PropertyFileLoader.getProperties();
                    }

                    usersWorker = new UsersWorker(
                            MongoConnectionInstanceProvider.getInstance());
                LogUtils.logInfo("UsersWork initialized");
                } catch (UnknownHostException e) {
                    throw new InstanceInitializeException(String.format("Unknown Host Exception %s", e.getMessage()));
                } catch (NotInitializedException e) {
                    throw new InstanceInitializeException(String.format("Not Initialized Exception %s", e.getMessage()));
                } catch (InvalidPropertiesException e) {
                    throw new InstanceInitializeException(String.format("Invalid properties: %s", e.getMessage()));
                }
            }
        }
    }

    public static UsersWorker getInstance() throws InstanceInitializeException {
        synchronized (SYNC_OBJ) {
            if (usersWorker == null) {
                init();
                LogUtils.logInfo("UsersWorker: New Instance Initialized");
            }
            return usersWorker;
        }
    }

    public static void finish() {
        synchronized (SYNC_OBJ) {
            usersWorker.finishWorker();
        }
    }
}
