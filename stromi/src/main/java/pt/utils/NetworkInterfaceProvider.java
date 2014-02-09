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

import pt.exception.*;
import pt.properties.PropertyFileLoader;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.*;

public class NetworkInterfaceProvider {

    private HashMap<String, NetworkInterfacePolicy> availableNICs;
    private volatile static NetworkInterfaceProvider singleton;

    private static final Object syncObj = new Object();
    private boolean hasInitialized = false;
    private int maxConnectionsPerNIC;

    private NetworkInterfaceProvider() {
        availableNICs = new HashMap<String, NetworkInterfacePolicy>();
    }

    public static NetworkInterfaceProvider getInstance() throws SocketException, InvalidPropertiesException, EmptyNetworkInterfaceProviderException, InvalidNetworkInterfacePropertiesException {
        synchronized (syncObj) {
            if (singleton == null) {
                singleton = new NetworkInterfaceProvider();
                singleton.init();
            }
            return singleton;
        }
    }

    public synchronized boolean hasMoreNICs() {
        for (NetworkInterfacePolicy nip : availableNICs.values()) {
            if (nip.canRequireThisNIC()) return true;
        }
        return false;
    }

    public synchronized String acquireNIC() throws NoMoreNetworkInterfaceConnectionsAvailable, ImpossibleToRequireNICException {
        if (hasMoreNICs()) {
            for (Map.Entry<String, NetworkInterfacePolicy> keyValuePair : availableNICs.entrySet()) {
                if (keyValuePair.getValue().canRequireThisNIC()) {
                    NetworkInterfacePolicy pol = keyValuePair.getValue();
                    return pol.acquire();
                }
            }
        }
        throw new NoMoreNetworkInterfaceConnectionsAvailable("There no exists more available network interfaces. Please call hasMoreNICs method before acquire a new NIC");
    }

    public synchronized void releaseNIC(String nicName) throws InvalidNetworkInterfaceNameException, ImpossibleToReleaseNICException {
        if (availableNICs.containsKey(nicName)) {
            NetworkInterfacePolicy networkInterfacePolicy = availableNICs.get(nicName);
            networkInterfacePolicy.release();
        } else {
            throw new InvalidNetworkInterfaceNameException(
                    String.format("Impossible to remove NIC. This NIC haven't been requested.\r\nNIC Name: %s", nicName));
        }
    }


    public synchronized void releaseNICCausedByException(String nicName) throws InvalidNetworkInterfaceNameException, ImpossibleToReleaseNICException {
        if (availableNICs.containsKey(nicName)) {
            NetworkInterfacePolicy networkInterfacePolicy = availableNICs.get(nicName);
            networkInterfacePolicy.releaseCausedByException();
        } else {
            throw new InvalidNetworkInterfaceNameException(
                    String.format("Impossible to remove NIC. This NIC haven't been requested.\r\nNIC Name: %s", nicName));
        }
    }

    private synchronized void init() throws InvalidPropertiesException, SocketException, EmptyNetworkInterfaceProviderException, InvalidNetworkInterfacePropertiesException {
        if (!hasInitialized) {
            String maxConnectionsPerNICstr = PropertyFileLoader.getProperties().getProperty("maxConnectionsPerNIC");
            String networkInterfaceNameFilter = PropertyFileLoader.getProperties().getProperty("networkInterfaceNameFilter");

            if (maxConnectionsPerNICstr != null && !maxConnectionsPerNICstr.isEmpty()) {
                maxConnectionsPerNIC = Integer.parseInt(maxConnectionsPerNICstr);
            } else {
                maxConnectionsPerNIC = 1;
            }
            boolean hasSubInterfaces = Boolean.parseBoolean(PropertyFileLoader.getProperties().getProperty("hasSubInterfaces"));
            int minNumber;
            int maxNumber;
            if (hasSubInterfaces) {
                String networkInterfaceNumberRange = PropertyFileLoader.getProperties().getProperty("networkInterfaceNumberRange");
                String[] minMax = networkInterfaceNumberRange.split("\\.\\.");
                if (minMax.length == 2) {
                    minNumber = Integer.parseInt(minMax[0]);
                    maxNumber = Integer.parseInt(minMax[1]);
                } else {
                    throw new InvalidNetworkInterfacePropertiesException("Network Interface Number Range not specified");
                }
                initializeInterfacesWithSubInterfacesRange(networkInterfaceNameFilter, minNumber, maxNumber);
            } else {
                initializeInterfaces(networkInterfaceNameFilter);
            }

            if (availableNICs.size() == 0 ) {
                throw new EmptyNetworkInterfaceProviderException("There not exists available NICs.");
            }
        }
        hasInitialized = true;
    }


    private void initializeInterfaces(String networkInterfaceNameFilter) throws SocketException {
        NetworkInterface networkInterface = NetworkInterface.getByName(networkInterfaceNameFilter);
        String nicName = networkInterface.getInetAddresses().nextElement().getHostAddress();
        availableNICs.put(nicName, new NetworkInterfacePolicy(nicName, maxConnectionsPerNIC));
    }


    private void initializeInterfacesWithSubInterfacesRange(String networkInterfaceNameFilter, int minNetworkNumber, int maxNetworkNumber) throws SocketException {
        Enumeration<NetworkInterface> subInterfaces = NetworkInterface.getByName(networkInterfaceNameFilter).getSubInterfaces();
        while (subInterfaces.hasMoreElements()) {
            NetworkInterface subInterface = subInterfaces.nextElement();
            String subInterfaceName = subInterface.getName();
            String[] split = subInterfaceName.split(":");
            if (split.length == 2) {
                if (subInterface.getInetAddresses().hasMoreElements()) {
                    int interfaceNumber = Integer.parseInt(split[1]);
                    String ipAddress = subInterface.getInetAddresses().nextElement().getHostAddress();
                    if (interfaceNumber >= minNetworkNumber && interfaceNumber <= maxNetworkNumber) {
                        availableNICs.put(ipAddress, new NetworkInterfacePolicy(ipAddress, maxConnectionsPerNIC));
                    }
                }
            }
        }
    }

    public String getStatus() {
        StringBuilder sb = new StringBuilder();
        sb.append("### Network Interface Provider Status ###\r\n");
        //TODO: better status info
        //sb.append(String.format("#  Required Network Interfaces: %d\r\n", _requiredNICs.size()));
        sb.append(String.format("# Available Network Interfaces: %d\r\n", availableNICs.size()));
        sb.append(String.format("#      Max Connections per NIC: %d\r\n", maxConnectionsPerNIC));
        sb.append(String.format("#              Has initialized: %b\r\n", hasInitialized));
        return sb.toString();
    }

    public void resetAllTimeouts() {
        for(NetworkInterfacePolicy interfacePolicy: availableNICs.values()){
            interfacePolicy.resetTimeout();
        }
    }
}
