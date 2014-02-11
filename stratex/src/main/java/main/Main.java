/*
*	This file is part of STRATEX.
*
*    STRATEX is free software: you can redistribute it and/or modify
*    it under the terms of the GNU General Public License as published by
*    the Free Software Foundation, either version 3 of the License, or
*    (at your option) any later version.
*
*    STRATEX is distributed in the hope that it will be useful,
*    but WITHOUT ANY WARRANTY; without even the implied warranty of
*    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
*    GNU General Public License for more details.
*
*    You should have received a copy of the GNU General Public License
*    along with STRATEX.  If not, see <http://www.gnu.org/licenses/>.
*/

package main;

import pt.command.base.CommandResolver;
import pt.command.exception.UnsupportedCommandException;
import pt.command.parser.CommandLineParser;
import stratex.commands.*;
import stratex.exceptions.StratexException;
import stratex.log.StratexLogger;

import java.util.Map;

public class Main {

    /**
     * Main class that receives a parameter
     *
     * @param args
     * @throws Exception
     */
    public static void main(String[] args){
        if (args == null || args.length == 0) {
            System.out.println("usage: stratex <commandName> {[<commandParameters>]}");
            StratexLogger.logInfo("usage: stratex <commandName> {[<commandParameters>]}");
            return;
        }

        try {
            StratexLogger.logInfo("Stratex started");

            // Add my command implementations to commandResolver (this will need to be fixed...)
            CommandResolver.addCommand(new TwitterAutoFollowCommand());
            CommandResolver.addCommand(new TwitterDefaultNICTrackCommand());
            CommandResolver.addCommand(new TwitterFollowAndTrackCommand());
            CommandResolver.addCommand(new TwitterFollowCommand());
            CommandResolver.addCommand(new TwitterTrackCommand());

            // Obtain command name and command parameters
            CommandLineParser clp = new CommandLineParser();
            Map<String, String> params = clp.parse(args);

            CommandResolver.execute(System.out, params, args[0]);

        } catch (UnsupportedCommandException e) {
            StratexLogger.logError(e.getMessage(), e);
        } finally {
            StratexLogger.logInfo("Execution stopped.");
        }
    }

    private static void printExceptionError(StratexException e) {
        System.out.println("Error: " + e.getMessage());
        if (e.getInnerException() != null && e.getInnerException().getMessage() != null)
            System.out.println(
                    "InnerException: "
                            + e.getInnerException().getClass().getName()
                            + " InnerException Error: "
                            + e.getInnerException().getMessage()
            );
        else if (e.getInnerException() != null)
            System.out.println(
                    "InnerException has no error message. Exception Type: "
                            + e.getInnerException().getClass().getName()
            );
    }

    private static boolean validateIPAddress(String ipAddr) {
        return true;
    }

    /*private static void LogErrorMessage(Exception e) {
        Log4JLogger logger = new Log4JLogger();
        logger.error(e.getMessage(), e);
    } */
}
