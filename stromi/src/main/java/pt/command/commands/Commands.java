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
//import pt.command.base.Command;
//import pt.command.exception.UnsupportedCommandException;
//import pt.command.parser.CommandParser;
//
//import java.io.PrintStream;
//import java.util.Hashtable;
//import java.util.Map;
//import java.util.Set;
//
//public class Commands {
//
//    public static final Map<String, Command> AvailableCommands;
//
//    static {
//        AvailableCommands = new Hashtable<String, Command>();
//        AvailableCommands.put("start", new CommandStart());
//        AvailableCommands.put("help", new CommandHelper());
//        AvailableCommands.put("refreshUserList", new CommandRefreshUsersList());
//        AvailableCommands.put("recycle", new CommandRecycle());
//        AvailableCommands.put("copy", new CommandCopyUsersCollection());
//        AvailableCommands.put("usage", new CommandUsage());
//        AvailableCommands.put("status", new CommandStatus());
//        AvailableCommands.put("timeout", new CommandTimeout());
//    }
//
//
//    public static void execute(PrintStream ps, String commandLine) throws UnsupportedCommandException {
//        Map<String, String> parameterMap;
//        String[] com = commandLine.split("\\+");
//        for (int i = 0; i < com.length; ++i) {
//            parameterMap = CommandParser.parser(com[i]);//commandLine
//            Set<String> set = AvailableCommands.keySet();
//            String command = null;
//            //iterar Map ate encontrar chave preendida pelo utilizador
//            for (String str : set) {
//                if (parameterMap.containsKey(str)) {
//                    command = str;
//                    break;
//                }
//            }
//            if (command != null && AvailableCommands.containsKey(command)) {
//                Command c = AvailableCommands.get(command);
//                c.run(ps, parameterMap);
//            } else {
//                throw new UnsupportedCommandException();
//            }
//        }
//    }
//}
