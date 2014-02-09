/*
*	This file is part of STRATEX.
*
*    STRATEX is free software: you can redistribute it and/or modify
*    it under the terms of the GNU General Public License as published by
*    the Free Software Foundation, either version 3 of the License, or
*    (at your option) any later version.
*
*    STRATEX is distributed in the hope that it will be useful,
*    but WITHOUT ANY WARRANTY; without even the implied warranty of
*    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
*    GNU General Public License for more details.
*
*    You should have received a copy of the GNU General Public License
*    along with STRATEX.  If not, see <http://www.gnu.org/licenses/>.
*/

package stratex.utils;

import stratex.exceptions.StratexException;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.Enumeration;

public class InetUtils {

    public static void displayInterfaceInformation(NetworkInterface netint) throws SocketException {
        System.out.printf("Display name: %s\n", netint.getDisplayName());
        System.out.printf("Name: %s\n", netint.getName());
        Enumeration<InetAddress> inetAddresses = netint.getInetAddresses();

        for (InetAddress inetAddress : Collections.list(inetAddresses)) {
            System.out.printf("InetAddress: %s\n", inetAddress);
        }
    }

    private static InetAddress getInetAddressFromInterface(String interfaceName) throws SocketException, StratexException {
        //TODO: some error handling, please...
        InetAddress address = null;
        NetworkInterface ni = NetworkInterface.getByName(interfaceName);
        if (ni == null) {
            Enumeration<NetworkInterface> nis = NetworkInterface.getNetworkInterfaces();
            while (nis.hasMoreElements()) {
                displayInterfaceInformation(nis.nextElement());
            }
            throw new StratexException("Could not find the specified network interface: " + interfaceName);
        } else {
            if (ni.getInetAddresses().hasMoreElements())
                address = ni.getInetAddresses().nextElement();
            else
                throw new StratexException("Specified network interface has no InetAddresses");
        }
        return address;
    }

    public static InetAddress getInetAddressFromContextualRepresentation(String address) throws UnknownHostException {
        return InetAddress.getByName(address);
    }
}
