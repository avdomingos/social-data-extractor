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

import pt.db.MongoDBDataHandler;
import pt.exception.InstanceInitializeException;
import pt.exception.InvalidPropertiesException;
import pt.exception.NotInitializedException;
import pt.exception.WrongUsernameOrPasswordException;
import pt.properties.PropertyFileLoader;
import pt.utils.LogUtils;
import pt.worker.mongoHash.TweetsProvider;

import java.net.UnknownHostException;
import java.util.Properties;

/**
 * Created with IntelliJ IDEA.
 * User: Crack
 * Date: 28-07-2012
 * Time: 18:51
 * To change this template use File | Settings | File Templates.
 */
public class TweetsProviderInstanceProvider {
    private static volatile TweetsProvider tweetsProvider;

    private static final Object SYNC_OBJ = new Object();

    public static void init() throws InstanceInitializeException {
        synchronized (SYNC_OBJ) {
            if (tweetsProvider == null) {
                try {
                    TweetsProvider.init(MongoConnectionInstanceProvider.getInstance());
                    tweetsProvider = TweetsProvider.getInstance();
                } catch (InvalidPropertiesException e) {
                    throw new InstanceInitializeException(String.format("Invalid properties: %s", e.getMessage()));
                } catch (UnknownHostException e) {
                    throw new InstanceInitializeException(String.format("Unknown Host: %s", e.getMessage()));
                } catch (NotInitializedException e) {
                    throw new InstanceInitializeException(String.format("Not initialized exception: %s", e.getMessage()));
                }
            }
        }
    }

    public static TweetsProvider getInstance() throws NotInitializedException, InstanceInitializeException {
        synchronized (SYNC_OBJ) {
            if (tweetsProvider == null) {
                init();
                LogUtils.logInfo("TweetsProvider: Singleton Initialized");
            }
            return tweetsProvider;
        }
    }
}
