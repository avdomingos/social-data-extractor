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
import pt.exception.*;
import pt.fabric.Monitorization;
import pt.utils.NetworkInterfaceProvider;

import java.io.PrintStream;
import java.net.SocketException;
import java.util.Map;

@CommandAnnotation(command = "recycle", description = "Recycles the all of the stratex applications", usage = "recycle")
public class CommandRecycle extends Command {

    @Override
    protected void commandAction(PrintStream ps, Map<String, String> parameters) throws ExitWithoutSuccessException {
        try {
            Monitorization.getInstance().changeUsers();
            NetworkInterfaceProvider.getInstance().resetAllTimeouts();
        } catch (NotInitializedException e) {
            throw new ExitWithoutSuccessException(e);
        } catch (InvalidPropertiesException e) {
            throw new ExitWithoutSuccessException(e);
        } catch (InstanceInitializeException e) {
            throw new ExitWithoutSuccessException(e);
        } catch (SocketException e) {
            throw new ExitWithoutSuccessException(e);
        } catch (EmptyNetworkInterfaceProviderException e) {
            throw new ExitWithoutSuccessException(e);
        } catch (InvalidNetworkInterfacePropertiesException e) {
            throw new ExitWithoutSuccessException(e);
        }
    }
}
