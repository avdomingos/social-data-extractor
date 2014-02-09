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

package pt.fabric;

import pt.db.MongoDBDataHandler;
import pt.intance.provider.NetworkInterfaceInstanceProvider;
import pt.utils.*;
import pt.worker.UsersWorker;
import pt.exception.*;
import pt.intance.provider.MongoConnectionInstanceProvider;
import pt.intance.provider.UsersWorkerInstanceProvider;
import pt.properties.PropertyFileLoader;

import java.net.SocketException;
import java.security.InvalidParameterException;
import java.util.*;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ScheduledThreadPoolExecutor;

public final class Monitorization extends AMonitorization  {

    protected static volatile Monitorization singleton;

    protected Monitorization() throws InvalidPropertiesException, InstanceInitializeException, NotInitializedException {
        super();
        LogUtils.logInfo("Monitorization: Singleton Initialized");
    }

    public static Monitorization getInstance() throws NotInitializedException, InvalidPropertiesException, InstanceInitializeException {
        synchronized (syncObj) {
            if (singleton == null) {
                LogUtils.logInfo("new monitorization");
                singleton = new Monitorization();
            }
            return singleton;
        }
    }

    //usage = "TwitterTrack             -networkAddress <xxx.xxx.xxx.xxx> -username <username> -password <password> -topics <topic1,topic2,topic3,...,topicN>",
    //usage = "TwitterFollow            -networkAddress <xxx.xxx.xxx.xxx> -username <username> -password <password> -users <user1,user2,user3,...,userN>",
    //usage = "TwitterFollowAndTrack    -networkAddress <xxx.xxx.xxx.xxx> -username <username> -password <password> -users <user1;user2;user3;...;userN> -topics <topic1;topic2;topic3;...;topicN>",
    protected void launchApps(String commandName, int totalOfAppsToLaunch, Map<String, String> extraParameters, boolean getUsersToFollow) throws LaunchApplicationException {
        LogUtils.logInfo("######### Launching Applications #########");
        ProcessLauncher p;
        int count = 0;

        try {
            Map.Entry<String, String> usernameAndPassword;
            while (nicProvider.hasMoreNICs() && twitterusersprovider.hasMoreUsers() && totalOfAppsToLaunch-- > 0) {
                usernameAndPassword = twitterusersprovider.acquireUserAndPassword();
                p = new ProcessLauncher(nicProvider.acquireNIC(), jdkPath, appName, usernameAndPassword.getKey(), usernameAndPassword.getValue(), commandName);
                for (Map.Entry<String, String> entry : extraParameters.entrySet()) {
                    p.addExtraParameters(entry.getKey(), entry.getValue());
                }
                if (getUsersToFollow) {
                    String toFollow = parseUsers(UsersToFollowProvider.getInstance().getUsers());
                    p.addExtraParameters(USER_PARAMETER, toFollow);
                }
                processes.add(p);
                p.addObserver(this);
                tpe.execute(p);
                count++;
            }
        } catch (NoMoreNetworkInterfaceConnectionsAvailable e) {
            throw new LaunchApplicationException(e);
        } catch (NoMoreTwitterUsersAvailableException e) {
            throw new LaunchApplicationException(e);
        } catch (SocketException e) {
            throw new LaunchApplicationException(e);
        } catch (EmptyNetworkInterfaceProviderException e) {
            throw new LaunchApplicationException(e);
        } catch (ImpossibleToRequireNICException e) {
            throw new LaunchApplicationException(e);
        } catch (InvalidPropertiesException e) {
            throw new LaunchApplicationException(e);
        } catch (InvalidNetworkInterfacePropertiesException e) {
            throw new LaunchApplicationException(e);
        }
        LogUtils.logInfo(String.format("Started %s processes", count));
    }

    public static void queueWork(WorkType workType, Map<String, String> parameters) throws InstanceInitializeException, NotInitializedException, InvalidPropertiesException {

        Monitorization.getInstance().queue(workType, parameters);
    }

    public long getAvailableUsers() {
        String pendingUsersCollectionMain = properties.getProperty("pendingUsersCollectionMain");
        return dataHandler.getCountOfAvailableElementsInCollection(pendingUsersCollectionMain);
    }


    public String getProcessesStatus() {
        StringBuilder sb = new StringBuilder();
        sb.append("### Processes Status\r\n");
        sb.append(String.format("### Processes Count %d\r\n", processes.size()));
        for (ProcessLauncher process : processes) {
            sb.append(String.format("%s\r\n", process.getStatus()));
        }
        return sb.toString();
    }
}
