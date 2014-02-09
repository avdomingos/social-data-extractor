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

package pt.utils;

import org.apache.log4j.Logger;

/**
 * LogUtils allows you to use log4j
 */
public class LogUtils {

    private static Logger logger;
    private static String LOGGER_NAME= "Stromi";

    static {
        logger = Logger.getLogger(LOGGER_NAME);
    }


    /**
     * Allows you to write some info log
     * @param log - message to log
     */
    public static void logInfo(String log ){
        logger.info(log);
    }

    /**
     * Allows you to write some error log
     * @param log - message to log
     */
    public static void logError(String log ){
        logger.error(log);
    }





}
