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
import pt.exception.InvalidPropertiesException;
import pt.utils.TwitterUsersProvider;

import java.io.IOException;
import java.io.PrintStream;
import java.util.Map;

@CommandAnnotation(command = "refreshUserList", description = "Show all available commands", usage = "refreshUserList")
public class CommandRefreshUsersList extends Command {

    @Override
    protected void commandAction(PrintStream ps, Map<String, String> parameters) throws ExitWithoutSuccessException {
        try {
            TwitterUsersProvider.getInstance().refreshList();
        } catch (IOException e) {
            throw new ExitWithoutSuccessException(e.getMessage());
        } catch (InvalidPropertiesException e) {
            throw new ExitWithoutSuccessException(e.getMessage());
        }
    }
}
