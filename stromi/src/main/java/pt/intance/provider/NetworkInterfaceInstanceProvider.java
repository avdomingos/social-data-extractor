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

import pt.exception.EmptyNetworkInterfaceProviderException;
import pt.exception.InstanceInitializeException;
import pt.exception.InvalidNetworkInterfacePropertiesException;
import pt.exception.InvalidPropertiesException;
import pt.utils.LogUtils;
import pt.utils.NetworkInterfaceProvider;

import java.net.SocketException;

public class NetworkInterfaceInstanceProvider {

    private static volatile NetworkInterfaceProvider networkInterfaceProvider;
    private static final Object SYNC_OBJ = new Object();

    public static void init() throws InstanceInitializeException {
        synchronized (SYNC_OBJ) {
            if (networkInterfaceProvider == null) {
                try {
                    LogUtils.logInfo("NetworkInterfaceProvider initialized");
                    networkInterfaceProvider = NetworkInterfaceProvider.getInstance();
                } catch (InvalidPropertiesException e) {
                    throw new InstanceInitializeException(e,String.format("Invalid properties: %s", e.getMessage()));
                } catch (EmptyNetworkInterfaceProviderException e) {
                    throw new InstanceInitializeException(e,String.format("Network interface provider: %s", e.getMessage()));
                } catch (SocketException e) {
                    throw new InstanceInitializeException(e,String.format("Socket exception: %s", e.getMessage()));
                } catch (InvalidNetworkInterfacePropertiesException e) {
                    throw new InstanceInitializeException(e);
                }
            }
        }
    }

    public static NetworkInterfaceProvider getInstance() throws InstanceInitializeException {
        synchronized (SYNC_OBJ) {
            if (networkInterfaceProvider == null) {
                init();
                LogUtils.logInfo("NetworkInterfaceProvider: Singleton Initialized");
            }
            return networkInterfaceProvider;
        }
    }

    public static void finish() {
    }
}
