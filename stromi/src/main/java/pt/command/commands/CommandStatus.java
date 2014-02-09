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

package pt.command.commands;

import pt.command.annotation.CommandAnnotation;
import pt.command.base.Command;
import pt.command.exception.ExitWithoutSuccessException;
import pt.exception.InstanceInitializeException;
import pt.exception.InvalidPropertiesException;
import pt.exception.NotInitializedException;
import pt.fabric.Monitorization;
import pt.intance.provider.NetworkInterfaceInstanceProvider;
import pt.intance.provider.UsersWorkerInstanceProvider;

import java.io.PrintStream;
import java.util.Map;

@CommandAnnotation(command = "status", description = "Prints the status of a specified resource", usage = "status -resource <usersWorker|networkInterface|monitorization|processes>")
public class CommandStatus extends Command {

    @Override
    protected void commandAction(PrintStream ps, Map<String, String> parameters) throws ExitWithoutSuccessException {
        if (!parameters.containsKey("resource")) {
            throw new ExitWithoutSuccessException(this.getUsage());
        }
        //<usersWorker|networkInterface>
        String resourceName = parameters.get("resource");
        if (resourceName.equalsIgnoreCase("usersWorker")) {
            printUsersWorkerStatus(ps);
        } else if (resourceName.equalsIgnoreCase("networkInterface")) {
            printNetworkInterfaceStatus(ps);
        } else if (resourceName.equalsIgnoreCase("monitorization")) {
            printMonitorizationStatus(ps);
        } else if (resourceName.equalsIgnoreCase("processes")) {
            printProcessesStatus(ps);
        } else {
            throw new ExitWithoutSuccessException(String.format("Invalid resource name. %s", getUsage()));
        }
    }

    private void printProcessesStatus(PrintStream ps) {
        try {
            ps.println(Monitorization.getInstance().getProcessesStatus());
        } catch (NotInitializedException e) {
            ps.println("Not initialized");
        } catch (InvalidPropertiesException e) {
            ps.println("Impossible to start. Invalid Properties");
        } catch (InstanceInitializeException e) {
            ps.println(String.format("Impossible to start. %s", e.getMessage()));
        }
    }

    private void printMonitorizationStatus(PrintStream ps) {
        try {
            ps.println(Monitorization.getInstance().getStatus());
        } catch (NotInitializedException e) {
            ps.println("Not initialized");
        } catch (InvalidPropertiesException e) {
            ps.println("Impossible to start. Invalid Properties");
        } catch (InstanceInitializeException e) {
            ps.println(String.format("Impossible to start. %s", e.getMessage()));
        }
    }

    private void printUsersWorkerStatus(PrintStream ps) {
        try {
            ps.println(UsersWorkerInstanceProvider.getInstance().getStatus());
        } catch (InstanceInitializeException e) {
            ps.println("Instance Initialize Exception ");
        }
    }

    private void printNetworkInterfaceStatus(PrintStream ps) {
        try {
            ps.println(NetworkInterfaceInstanceProvider.getInstance().getStatus());
        } catch (InstanceInitializeException e) {
            ps.println("Instance Initialize Exception ");
        }
    }
}
