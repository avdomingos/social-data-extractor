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

import java.net.UnknownHostException;
import java.util.Properties;

public class MongoConnectionInstanceProvider {

    private static volatile MongoDBDataHandler dataHandler;
    private static volatile Properties properties;
    private static final Object SYNC_OBJ = new Object();

    public static MongoDBDataHandler init() throws InstanceInitializeException {
        synchronized (SYNC_OBJ) {
            //if (dataHandler == null) {
                try {
                    if (properties == null) {
                        properties = PropertyFileLoader.getProperties();
                    }
                    String host = properties.getProperty("host");
                    int port = Integer.parseInt(properties.getProperty("port"));
                    String username = properties.getProperty("mongoUsername");
                    String password = properties.getProperty("mongoPassword");
                    String databaseName = properties.getProperty("databaseName");
                    //dataHandler = new MongoDBDataHandler(

                    return new MongoDBDataHandler(
                            host,
                            port,
                            username,
                            password,
                            databaseName);
                     ///System.out.println("data handler initialized");
                    //dataHandler
                } catch (InvalidPropertiesException e) {
                    throw new InstanceInitializeException(String.format("Invalid properties: %s", e.getMessage()));
                } catch (UnknownHostException e) {
                    throw new InstanceInitializeException(String.format("Unknown Host: %s", e.getMessage()));
                } catch (WrongUsernameOrPasswordException e) {
                    throw new InstanceInitializeException(String.format("Wrong Username Or Password: %s", e.getMessage()));
                }
            }
        //}
    }

    public static MongoDBDataHandler getInstance() throws NotInitializedException, InstanceInitializeException {
        synchronized (SYNC_OBJ) {
            //if (dataHandler == null) {
               // init();
            //}
            //return dataHandler;
            LogUtils.logInfo("MongoDBDataHandler: New Instance Initialized");
            return init();
        }
    }

    public static void finish() {
        synchronized (SYNC_OBJ) {
            dataHandler.closeConnection();
        }
    }
}
