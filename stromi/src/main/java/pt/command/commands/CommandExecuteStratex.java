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

import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import pt.command.annotation.CommandAnnotation;
import pt.command.base.Command;
import pt.command.exception.ExitWithoutSuccessException;
import pt.db.MongoDBDataHandler;
import pt.exception.InstanceInitializeException;
import pt.exception.InvalidPropertiesException;
import pt.exception.NotInitializedException;
import pt.fabric.Monitorization;
import pt.intance.provider.MongoConnectionInstanceProvider;

import java.io.PrintStream;
import java.util.Map;
import java.util.concurrent.ScheduledThreadPoolExecutor;

@CommandAnnotation(command = "executeStratex",
        description = "Execute the stratex application with the specified parameters",
        usage = "executeStratex [-follow <all|userID1;userID2;userID3;..;userIDN>|-track <track1;track2;track3;..;trackN>] -numberOfInstances <all|numberOfInstances>")
public class CommandExecuteStratex extends Command {

    private static ScheduledThreadPoolExecutor threadPoolExecutor;


    @Override
    protected void commandAction(PrintStream ps, Map<String, String> parameters) throws ExitWithoutSuccessException {
        try {
            if (parameters.containsKey("follow") && parameters.containsKey("track")) {
                Monitorization.queueWork(Monitorization.WorkType.BOTH, parameters);
            } else if (parameters.containsKey("track")) {
                Monitorization.queueWork(Monitorization.WorkType.TRACK, parameters);
            } else if (parameters.containsKey("follow")) {
                Monitorization.queueWork(Monitorization.WorkType.FOLLOW, parameters);
            } else {
                throw new ExitWithoutSuccessException(getUsage());
            }
        } catch (InstanceInitializeException e) {
            throw new ExitWithoutSuccessException(e);
        } catch (NotInitializedException e) {
            throw new ExitWithoutSuccessException(e);
        } catch (InvalidPropertiesException e) {
            throw new ExitWithoutSuccessException(e);
        }
    }
}
