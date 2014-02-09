/*
*	This file is part of SINC.
*
*    SINC is free software: you can redistribute it and/or modify
*    it under the terms of the GNU General Public License as published by
*    the Free Software Foundation, either version 3 of the License, or
*    (at your option) any later version.
*
*    SINC is distributed in the hope that it will be useful,
*    but WITHOUT ANY WARRANTY; without even the implied warranty of
*    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
*    GNU General Public License for more details.
*
*    You should have received a copy of the GNU General Public License
*    along with SINC.  If not, see <http://www.gnu.org/licenses/>.
*/

package pt.sinc.logger;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

/**
 * Logger class used by SINC.
 */
public class SincLogger {
    private static Logger logger;
    private static final String LOGGER_NAME = "SINC";

    static {
        PropertyConfigurator.configure("log4j_sinc.properties");
        logger = Logger.getLogger(LOGGER_NAME);
    }

    /**
     * Allows you to write some info log
     *
     * @param log - message to log
     */
    public static void logInfo(String log) {
        logger.info(log);
    }

    /**
     * Allows you to write some error log
     *
     * @param log - message to log
     */
    public static void logError(String log) {
        logger.error(log);
    }

    public static void logWarn(String log, Throwable t) {
        logger.warn(log, t);
    }

    public static void logWarn(String log) {
        logger.warn(log);
    }

    public static void logError(String log, Throwable t) {
        logger.error(log, t);
    }
}
