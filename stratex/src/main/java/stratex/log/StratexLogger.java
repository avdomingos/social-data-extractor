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

package stratex.log;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import java.lang.management.ManagementFactory;

/**
 * StratexLogger allows you to use log4j
 */
public class StratexLogger {

    private static Logger logger;
    private static String LOGGER_NAME = "Stratex";
    private static String PROCESS_ID = ManagementFactory.getRuntimeMXBean().getName();
    private static String logFormat = "PID: %s - LogInfo: %s";

    public static final String CONFIG_FILENAME = "log4j.properties";

    static {
        PropertyConfigurator.configure(CONFIG_FILENAME);
        logger = Logger.getLogger(LOGGER_NAME);
    }

    /**
     * Allows you to write some info log
     *
     * @param log - message to log
     */
    public static void logInfo(String log) {
        logger.info(getFormattedLog(logFormat, log));
    }

    private static Object getFormattedLog(String logFormat, String log) {
        return String.format(logFormat, PROCESS_ID, log);
    }

    /**
     * Allows you to write some error log
     *
     * @param log - message to log
     */
    public static void logError(String log) {
        logger.error(getFormattedLog(logFormat, log));
    }

    public static void logWarn(String log, Throwable t) {
        logger.warn(getFormattedLog(logFormat, log), t);
    }

    public static void logWarn(String log) {
        logger.warn(getFormattedLog(logFormat, log));
    }

    public static void logError(String log, Throwable t) {
        logger.error(getFormattedLog(logFormat, log), t);
    }
}