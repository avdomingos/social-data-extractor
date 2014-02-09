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
import pt.fabric.DistributedMonitorization;
import pt.fabric.Monitorization;

import java.io.PrintStream;
import java.util.Map;
import java.util.concurrent.ScheduledThreadPoolExecutor;

@CommandAnnotation(command="start",description="Starts the application fabric",usage = "start" )
public class CommandStart extends Command {

    private ScheduledThreadPoolExecutor _tpe = null;

    @Override
    protected void commandAction(PrintStream ps, Map<String, String> parameters) throws ExitWithoutSuccessException {
        if (_tpe == null) {
            _tpe = new ScheduledThreadPoolExecutor(1);
            try {
                DistributedMonitorization mon = DistributedMonitorization.getInstance();
                _tpe.execute(mon);
                mon.awakeWorkerThread();
            } catch (NotInitializedException e) {
                throw new ExitWithoutSuccessException(e.getMessage());
            } catch (InvalidPropertiesException e) {
                throw new ExitWithoutSuccessException(e.getMessage());
            } catch (InstanceInitializeException e) {
                throw new ExitWithoutSuccessException(e.getMessage());
            }
        } else {
            System.out.println("Already running");
        }
    }
}
