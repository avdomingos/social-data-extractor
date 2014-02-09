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
import pt.exception.*;
import pt.intance.provider.MongoConnectionInstanceProvider;
import pt.intance.provider.NetworkInterfaceInstanceProvider;
import pt.properties.PropertyFileLoader;
import pt.utils.*;
import pt.worker.UsersWorker;

import java.net.SocketException;
import java.security.InvalidParameterException;
import java.util.*;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ScheduledThreadPoolExecutor;

/**
 * Created with IntelliJ IDEA.
 * User: Crack
 * Date: 08-08-2012
 * Time: 22:01
 * To change this template use File | Settings | File Templates.
 */
public abstract class AMonitorization implements Runnable, Observer{
    protected static final Object syncObj = new Object();

    protected String jdkPath;
    protected String appName;
    protected ScheduledThreadPoolExecutor tpe = null;
    protected List<ProcessLauncher> processes;
    protected LinkedBlockingDeque<KeyValuePair<WorkType, Map<String, String>>> workQueue;


    protected volatile NetworkInterfaceProvider nicProvider;
    protected volatile TwitterUsersProvider twitterusersprovider;
    protected volatile UsersWorker usersworker = null;
    protected volatile Properties properties;
    protected volatile MongoDBDataHandler dataHandler;

    protected boolean toStop;
//    protected static volatile Monitorization singleton;
    protected final Object lockObjectToWakeUpRunnableThread = new Object();
    protected volatile int threadsRunning;

    protected static final int MAX_USERS_TO_FOLLOW_BY_USER = 5000;

    protected static final String FOLLOW_COMMAND = "TwitterFollow";
    protected static final String TRACK_COMMAND = "TwitterTrack";
    protected static final String BOTH_COMMAND = "TwitterFollowAndTrack";

    protected static final String NUMBER_OF_INSTANCES = "numberOfInstances";

    protected static final String USER_PARAMETER = "users";

    protected static final String[] FOLLOW_REQUIRED_PARAMETERS = {"follow"};
    protected static final String[] FOLLOW_OPTIONAL_PARAMETERS = {""};
    protected static final String[] TRACK_REQUIRED_PARAMETERS = {"track"};
    protected static final String[] TRACK_OPTIONAL_PARAMETERS = {""};


    protected AMonitorization() throws InvalidPropertiesException, InstanceInitializeException, NotInitializedException {
        properties = PropertyFileLoader.getProperties();
        twitterusersprovider = TwitterUsersProvider.getInstance();
        nicProvider = NetworkInterfaceInstanceProvider.getInstance();
        // usersworker = UsersWorkerInstanceProvider.getInstance();
        dataHandler = MongoConnectionInstanceProvider.getInstance();
        workQueue = new LinkedBlockingDeque<KeyValuePair<WorkType, Map<String, String>>>();

        processes = new LinkedList<ProcessLauncher>();
        toStop = false;
        threadsRunning = 0;

        jdkPath = properties.getProperty("jdk_path");
        appName = properties.getProperty("app_name");
        tpe = new ScheduledThreadPoolExecutor(Integer.parseInt(properties.getProperty("corePoolSize")));
        //tpe.execute(usersworker);
    }

//    public static Monitorization getInstance() throws NotInitializedException, InvalidPropertiesException, InstanceInitializeException {
//        synchronized (syncObj) {
//            if (singleton == null) {
//                LogUtils.logInfo("new monitorization");
//                singleton = new Monitorization();
//            }
//            return singleton;
//        }
//    }

    public void update(Observable o, Object arg) {
        if (arg instanceof ProcessLauncher) {
            ProcessLauncher p = (ProcessLauncher) arg;
            processes.remove(p);
        }
        usersworker.awakeWorkerThread();
        synchronized (lockObjectToWakeUpRunnableThread) {
            lockObjectToWakeUpRunnableThread.notifyAll();
        }
    }


    //usage = "TwitterTrack             -networkAddress <xxx.xxx.xxx.xxx> -username <username> -password <password> -topics <topic1,topic2,topic3,...,topicN>",
    //usage = "TwitterFollow            -networkAddress <xxx.xxx.xxx.xxx> -username <username> -password <password> -users <user1,user2,user3,...,userN>",
    //usage = "TwitterFollowAndTrack    -networkAddress <xxx.xxx.xxx.xxx> -username <username> -password <password> -users <user1;user2;user3;...;userN> -topics <topic1;topic2;topic3;...;topicN>",
    protected abstract void launchApps(String commandName, int totalOfAppsToLaunch, Map<String, String> extraParameters, boolean getUsersToFollow) throws LaunchApplicationException;

    protected String parseUsers(List<Long> users) {
        StringBuilder sb = new StringBuilder();
        Iterator<Long> iter = users.iterator();
        if (iter.hasNext()) {
            sb.append(iter.next());
        }
        while (iter.hasNext()) {
            sb.append(String.format(";%s", iter.next()));
        }
        return sb.toString();
    }

    public void run() {
        synchronized (lockObjectToWakeUpRunnableThread) {
            LogUtils.logInfo("Run on AMonitorization");
            while (!toStop) {
                if (hasElementsToWork() && hasConditionsToWork()) {
                    try {
                        KeyValuePair<WorkType, Map<String, String>> elementToWork = dequeue();
                        WorkType workType = elementToWork.getKey();
                        Map<String, String> parameters = elementToWork.getValue();
                        Map<String, String> parametersToProcessLauncher;

                        int numberOfInstances = getNumberOfInstances(parameters);
                        String commandName;
                        boolean isToGetUsers = false;
                        if (workType == WorkType.FOLLOW) {
                            commandName = FOLLOW_COMMAND;
                            parametersToProcessLauncher = getProperParametersForParameters(parameters, FOLLOW_REQUIRED_PARAMETERS, FOLLOW_OPTIONAL_PARAMETERS);
                            String follow = parameters.get("follow");
                            if (follow.equalsIgnoreCase("all")) {
                                isToGetUsers = true;
                            } else {
                                parametersToProcessLauncher.put("follow", follow);
                            }
                        } else if (workType == WorkType.TRACK) {
                            commandName = TRACK_COMMAND;
                            parametersToProcessLauncher = getProperParametersForParameters(parameters, TRACK_REQUIRED_PARAMETERS, TRACK_OPTIONAL_PARAMETERS);
                        } else if (workType == WorkType.BOTH) {
                            commandName = BOTH_COMMAND;
                            parametersToProcessLauncher = getProperParametersForParameters(parameters, FOLLOW_REQUIRED_PARAMETERS, FOLLOW_OPTIONAL_PARAMETERS);
                            parametersToProcessLauncher.putAll(getProperParametersForParameters(parameters, TRACK_REQUIRED_PARAMETERS, TRACK_OPTIONAL_PARAMETERS));
                            String follow = parameters.get("follow");
                            if (follow.equalsIgnoreCase("all")) {
                                isToGetUsers = true;
                            } else {
                                parametersToProcessLauncher.put("follow", follow);
                            }
                        } else {
                            throw new InvalidParameterException(String.format("Invalid work type element: %s", workType));
                        }
                        launchApps(commandName, numberOfInstances, parametersToProcessLauncher, isToGetUsers);
                    } catch (InvalidPropertiesException e) {
                        LogUtils.logError(e.getMessage());
                    } catch (LaunchApplicationException e) {
                        LogUtils.logError(e.getMessage());
                    } catch (MissingParameterException e) {
                        LogUtils.logError(e.getMessage());
                    }
                } else {
                    try {
                        lockObjectToWakeUpRunnableThread.wait();
                    } catch (InterruptedException e) {
                        LogUtils.logError("Thread Interrupted: AMonitorization thread");
                    }
                }
            }
            LogUtils.logInfo("Exit of AMonitorization");
        }
    }

    protected boolean hasConditionsToWork() {
        return nicProvider.hasMoreNICs() && twitterusersprovider.hasMoreUsers();
    }

    private Map<String, String> getProperParametersForParameters(Map<String, String> parameters, String[] requiredParameters, String[] optionalParameters) throws MissingParameterException {
        Map<String, String> parametersToReturn = getRequiredParameters(parameters, requiredParameters);
        parameters.putAll(getOptionalParameters(parameters, optionalParameters));
        return parametersToReturn;
    }

    /**
     * Get proper parameters for Follow Work Type
     * @param parameters         received parameters from the queued element
     * @param optionalParameters
     * @return parameters to launch a Follow Process
     */
    private Map<String, String> getOptionalParameters(Map<String, String> parameters, String[] optionalParameters) {
        Map<String, String> optionalParams = new HashMap<String, String>();
        for (String optionalParameter : optionalParameters) {
            if (parameters.containsKey(optionalParameter)) {
                optionalParams.put(optionalParameter, parameters.get(optionalParameter));
            }
        }
        return optionalParams;
    }


    private Map<String, String> getRequiredParameters(Map<String, String> parameters, String[] requiredParameters) throws MissingParameterException {
        checkRequiredParameters(parameters, requiredParameters);

        Map<String, String> paramsToReturn = new HashMap<String, String>();
        for (String param : requiredParameters) {
            paramsToReturn.put(param, parameters.get(param));
        }
        return paramsToReturn;
    }

    private void checkRequiredParameters(Map<String, String> parameters, String[] paramsToCheck) throws MissingParameterException {
        for (String param : paramsToCheck) {
            if (!parameters.containsKey(param)) {
                throw new MissingParameterException(String.format("Missing parameter: %s", param));
            }
        }
    }

    /**
     * Returns the available or required number of instances
     * The method checks the parameters Map and inspect if is possible to run the required processes
     * @param parameters - Parameters of work element
     * @return number of instances to start
     * @throws InvalidPropertiesException
     */
    private int getNumberOfInstances(Map<String, String> parameters) throws InvalidPropertiesException {
        if (checkParametersForProperty(parameters, NUMBER_OF_INSTANCES)) {
            String numberOfInstances = parameters.get(NUMBER_OF_INSTANCES);
            long availableUsers = getAvailableUsers();
            int availableInstances = (int) availableUsers / MAX_USERS_TO_FOLLOW_BY_USER;
            if (availableInstances == 0 && availableUsers > 0) {
                availableInstances = 1;
            }
            if (numberOfInstances.equalsIgnoreCase("all")) {
                return availableInstances;
            } else {
                int instances = Integer.parseInt(parameters.get(NUMBER_OF_INSTANCES));
                if (instances > availableInstances) {
                    return availableInstances;
                } else {
                    return instances;
                }
            }
        }
        throw new InvalidPropertiesException(String.format("Missing parameter: %s", NUMBER_OF_INSTANCES));
    }

    /**
     * Check if the property exists and is valid in Map parameters
     * @param parameters - Map of parameters to work
     * @param property   - Property to be checked
     * @return true if the property exists and is valid
     */
    private boolean checkParametersForProperty(Map<String, String> parameters, String property) {
        if (parameters.containsKey(property)) {
            String parameter = parameters.get(property);
            if (parameter != null && !parameter.isEmpty()) return true;
        }
        return false;
    }

    //TODO: é necessário alterar este procedimento para saber se mata todos ou só os follow

    /**
     * Method that abort all running processes to change the users to follow
     */
    public void changeUsers() {
        for (ProcessLauncher p : processes) {
            p.forceToClose();
        }
        awakeWorkerThread();
    }

    /**
     * Forces all processes to close and notify the runnable to stop working
     */
    public void forceToClose() {
        toStop = true;
        usersworker.finishWorker();
        for (ProcessLauncher p : processes) {
            p.forceToClose();
        }
        awakeWorkerThread();
    }

    /**
     * Notify the runner thread to release the wait state
     */
    public void awakeWorkerThread() {
        synchronized (lockObjectToWakeUpRunnableThread) {
            lockObjectToWakeUpRunnableThread.notifyAll();
        }
    }

    public String getStatus() {
        StringBuilder sb = new StringBuilder();
        sb.append("### Monitorization Status ###\r\n");
        sb.append(String.format("#                 JDK Path: %s\r\n", jdkPath));
        sb.append(String.format("#         Application Name: %s\r\n", appName));
        sb.append(String.format("#         Has NIC Provider? %b\r\n", nicProvider != null));
        sb.append(String.format("# Has Twitter Users Prover? %b\r\n", twitterusersprovider != null));
        sb.append(String.format("#        Running Processes: %d\r\n", processes.size()));
        return sb.toString();
    }

    public abstract String getProcessesStatus();

    private KeyValuePair<WorkType, Map<String, String>> dequeue() {
        if (!hasElementsToWork()) throw new NoSuchElementException("The queue does not contain any element");
        return workQueue.removeFirst();
    }

    public boolean hasElementsToWork() {
        return workQueue.size() > 0;
    }

    public void queue(WorkType workType, Map<String, String> parameters) {
        workQueue.addLast(new KeyValuePair<WorkType, Map<String, String>>(workType, parameters));
        synchronized (lockObjectToWakeUpRunnableThread) {
            lockObjectToWakeUpRunnableThread.notifyAll();
        }
    }

    public abstract long getAvailableUsers();

    public enum WorkType {
        TRACK, FOLLOW, BOTH
    }
}
