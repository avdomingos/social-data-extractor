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

//package pt.command.commands;
//
//import java.io.PrintStream;
//import java.util.Map;
//
//import pt.command.annotation.CommandAnnotation;
//import pt.command.base.Command;
//import pt.command.base.CommandResolver;
//import pt.command.exception.ExitWithoutSuccessException;
//
//@CommandAnnotation(command = "usage", description = "Show commands usage", usage = "usage -commandName <commandName>")
//public class CommandUsage extends Command {
//
//    @Override
//    protected void commandAction(PrintStream ps, Map<String, String> parameters) throws ExitWithoutSuccessException {
//        if (!parameters.containsKey("commandName")) {
//            throw new ExitWithoutSuccessException("Invalid invocation exception: " + this.getUsage());
//        }
//
//        if (!CommandResolver.AvailableCommands.containsKey(parameters.get("commandName"))) {
//            throw new ExitWithoutSuccessException("Command does not exists");
//        }
//        ps.println(CommandResolver.AvailableCommands.get(parameters.get("commandName")).getUsage());
//    }
//}
