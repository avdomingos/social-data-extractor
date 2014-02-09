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

import pt.exception.ImpossibleToReleaseNICException;
import pt.exception.ImpossibleToRequireNICException;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: Hugo Ferreira
 * Date: 02-05-2012
 * Time: 7:05
 * To change this template use File | Settings | File Templates.
 */
public class NetworkInterfacePolicy {

    private int availableInstances;
    private int maxInstances;
    private Date dateController;
    private int failCount;
    private int timeout;
    private static int TIMEOUT_INCREMENT = 10000;
    private String NIC;

    public NetworkInterfacePolicy(String NIC, int instances) {
        availableInstances = instances;
        maxInstances = instances;
        dateController = new Date();
        failCount = 0;
        timeout = 0;
        this.NIC = NIC;
    }

    public void releaseCausedByException() throws ImpossibleToReleaseNICException {
        if (availableInstances == maxInstances) {
            throw new ImpossibleToReleaseNICException(NIC);
        }
        availableInstances++;
        timeout += TIMEOUT_INCREMENT;
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        c.add(Calendar.MILLISECOND, timeout);
        dateController = c.getTime();

    }

    public void release() throws ImpossibleToReleaseNICException {
        if (availableInstances == maxInstances) {
            throw new ImpossibleToReleaseNICException(NIC);
        }
        dateController = new Date();
        failCount = 0;
        availableInstances++;
        timeout = 0;
    }

    public String acquire() throws ImpossibleToRequireNICException {
        if (canRequireThisNIC()) {
            availableInstances--;
            return NIC;
        } else throw new ImpossibleToRequireNICException(NIC);
    }

    public String getNICName() {
        return NIC;
    }

    public boolean canRequireThisNIC() {
        Date date = new Date();
        return date.after(dateController) && availableInstances > 0;
    }

    public void resetTimeout() {
        dateController = new Date();
        failCount = 0;
        timeout = 0;
    }
}
