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
import pt.intance.provider.UsersWorkerInstanceProvider;

import java.io.PrintStream;
import java.util.Map;

@CommandAnnotation(command = "usersWorker", description = "Do operations", usage = "usersWorker -action <init|awake|finish>")
public class CommandUsersWorker extends Command {

    @Override
    protected void commandAction(PrintStream ps, Map<String, String> parameters) throws ExitWithoutSuccessException {
        if (parameters.size() != 2 && !parameters.containsKey("action")) {
            throw new ExitWithoutSuccessException("Invalid Usage " + this.getUsage());
        }

        try {
            String action = parameters.get("action");
            if (action.equals("awake")) {
                UsersWorkerInstanceProvider.getInstance().awakeWorkerThread();
            } else if (action.equals("awake")) {
                UsersWorkerInstanceProvider.init();
            } else if (action.equals("init")) {
                UsersWorkerInstanceProvider.init();
            } else if (action.equals("finish")) {
                UsersWorkerInstanceProvider.finish();
            }
        } catch (InstanceInitializeException e) {
            throw new ExitWithoutSuccessException("Unable to get a valid UsersWorker instance");
        }
    }
}
