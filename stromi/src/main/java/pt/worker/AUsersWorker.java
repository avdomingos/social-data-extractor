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

package pt.worker;

import pt.exception.InvalidPropertiesException;
import pt.properties.PropertyFileLoader;

/**
 * Created with IntelliJ IDEA.
 * User: Crack
 * Date: 05-08-2012
 * Time: 16:08
 * To change this template use File | Settings | File Templates.
 */
public abstract class AUsersWorker implements Runnable {

    /*Users work properties */
    protected long refreshPeriod;
    protected boolean toFinish;

    /*Concurrency objects*/
    protected final Object lockObjectToWakeUpRunnableThread = new Object();

    public AUsersWorker() throws InvalidPropertiesException {
        toFinish = false;
        refreshPeriod = Long.parseLong(PropertyFileLoader.getProperties().getProperty("usersWorkerRefreshPeriod"));
    }

    public String getStatus() {
        StringBuilder sb = new StringBuilder();
        sb.append("### User Worker Properties###\r\n");
        sb.append(String.format("# Refresh period: %d\r\n", refreshPeriod));
        sb.append(String.format("#    Is finished? %b\r\n", toFinish));
        return sb.toString();
    }

    public void run() {
        synchronized (lockObjectToWakeUpRunnableThread) {
            while (!toFinish) {
                doWork();
                try {
                    lockObjectToWakeUpRunnableThread.wait(refreshPeriod);
                } catch (InterruptedException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            }
        }
    }

    public void awakeWorkerThread() {
        synchronized (lockObjectToWakeUpRunnableThread) {
            lockObjectToWakeUpRunnableThread.notifyAll();
        }
    }

    public void finishWorker() {
        synchronized (lockObjectToWakeUpRunnableThread) {
            toFinish = true;
            //Notifies the thread that is running
            lockObjectToWakeUpRunnableThread.notifyAll();
        }
    }

    protected abstract void doWork();
}
