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

import pt.exception.*;
import pt.utils.DistributedUsersToFollowProvider;
import pt.utils.LogUtils;
import pt.utils.NetworkInterfaceProvider;
import pt.utils.TwitterUsersProvider;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.SocketException;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Map;
import java.util.Observable;

public class ProcessLauncher extends Observable implements Runnable {

    private String nicName;
    private String jdkPath;
    private String appName;
    private String username;
    private String password;
    private String commandName;
    private Process process;
    private NetworkInterfaceProvider nicProvider;
    private Map<String,String> extraParameters;
    public LinkedList<Long> listOfFollowedUsers;

    public ProcessLauncher(String nicName, String jdk_path, String app_name, String username, String password, String commandName) throws InvalidPropertiesException, SocketException, EmptyNetworkInterfaceProviderException, InvalidNetworkInterfacePropertiesException {
        this.nicName = nicName;
        this.jdkPath = jdk_path;
        this.appName = app_name;
        this.username = username;
        this.password = password;
        this.commandName = commandName;
        this.nicProvider = NetworkInterfaceProvider.getInstance();
        this.extraParameters = new Hashtable<String, String>();
        this.listOfFollowedUsers = null;
        LogUtils.logInfo(String.format("New Process Laucnher. Command: %s ",commandName));
    }

    public void addExtraParameters(String parameterName, String parameterValue){
        this.extraParameters.put(parameterName,parameterValue);
    }

    public void addExtraParameters(Map<String,String> extraParameters){
        this.extraParameters.putAll(extraParameters);
    }

    public LinkedList<Long> getListOfFollowedUsers(){
        return listOfFollowedUsers;
    }
    public void setListOfFollowedUsers(LinkedList<Long> listOfFollowedUsers){
        this.listOfFollowedUsers = listOfFollowedUsers;
    }

    public void run() {
        int maxCharToLog = 40;
        String instructionToExec = null;
        int returnValue = 0;
        try {
            String extraParameters = getExtraParameters();
           // -networkAddress <xxx.xxx.xxx.xxx> -username <username> -password
            instructionToExec = String.format("%sjava -jar %s %s  -networkAddress %s -username %s -password %s %s" , jdkPath, appName, commandName, nicName, username, password, extraParameters);
            process = Runtime.getRuntime().exec(instructionToExec);
            returnValue = process.waitFor();
        } catch (InterruptedException e) {
            returnValue = process.exitValue();
            LogUtils.logError(String.format("ProcessLauncher thread interrupted: %s", e.getMessage()));
        } catch (IOException e) {
            returnValue = process.exitValue();
            LogUtils.logError(String.format("Error on execute instruction:\r\n Error: %s\r\n%s", instructionToExec.substring(0,maxCharToLog>instructionToExec.length()?instructionToExec.length():maxCharToLog), e.getMessage()));
        } catch (RuntimeException e) {
            returnValue = process.exitValue();
            LogUtils.logError(String.format("Error on execute instruction:\r\n Error: %s\r\n%s", instructionToExec.substring(0,maxCharToLog>instructionToExec.length()?instructionToExec.length():maxCharToLog), e.getMessage()));
        } catch (Exception e) {
            returnValue = process.exitValue();
            LogUtils.logError(String.format("Error on execute instruction:\r\n Error: %s\r\n%s", instructionToExec.substring(0,maxCharToLog>instructionToExec.length()?instructionToExec.length():maxCharToLog), e.getMessage()));
        } finally {
            try {
                if (returnValue != 0) {
                    LogUtils.logError("Release Caused by Exception");
                    nicProvider.releaseNICCausedByException(nicName);
                } else {
                    nicProvider.releaseNIC(nicName);
                }
            } catch (InvalidNetworkInterfaceNameException e) {
                LogUtils.logError(String.format("Error on NIC Release: %s", e.getMessage()));
            } catch (ImpossibleToReleaseNICException e) {
                LogUtils.logError(String.format("Error on NIC Release: %s", e.getMessage()));
            }
            if (process.exitValue() < 0) {
                getProcessError(process);
            }
            process.destroy();
            //Notify observers (Monitorization)
            TwitterUsersProvider.getInstance().releaseUser(username);
            setChanged();
            notifyObservers(this);
            LogUtils.logInfo("Exiting from process with ip " + nicName);

        }
    }

    private void registUsersInProcess() {
        if(this.listOfFollowedUsers!=null && this.listOfFollowedUsers.size()>0){

        }
    }

    private void getProcessError(Process p) {
        InputStreamReader inputStream = new InputStreamReader(p.getErrorStream());
        BufferedReader processErrorConsole = new BufferedReader(inputStream);
        String line;
        try {
            while ((line = processErrorConsole.readLine()) != null) {
                LogUtils.logError("ERROR: " + line);
            }
        } catch (IOException e) {
            LogUtils.logError(String.format("Error on get process error: %s", e.getMessage()));
        }
    }

    public String getStatus() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("NIC Name: %s\r\n", nicName));
        sb.append(String.format("JDK Path: %s\r\n", jdkPath));
        sb.append(String.format("Application Name: %s\r\n", appName));
        sb.append(String.format("Twitter Username: %s\r\n", username));
        sb.append(String.format("Twitter Password: %s\r\n", password));
        return sb.toString();
    }

    public void forceToClose() {
        process.destroy();
    }

    private String getExtraParameters() {
        StringBuilder sb = new StringBuilder("");
        for(Map.Entry<String,String> entry : extraParameters.entrySet()){
            sb.append(String.format("-%s %s", entry.getKey(),entry.getValue()));
        }
        return sb.toString();
    }
}
