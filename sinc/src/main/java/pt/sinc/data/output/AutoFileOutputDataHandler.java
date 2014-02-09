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

package pt.sinc.data.output;

import pt.sinc.logger.SincLogger;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;


public class AutoFileOutputDataHandler implements IOutputDataHandler {
    private static final String ERROR_MSG_CLOSE_STREAM = "Error closing AutoFileOutputDataHandler stream.";
    private FileWriter fw = null;
    private BufferedWriter writer = null;

    public AutoFileOutputDataHandler(String file) throws IOException {
        writer = new BufferedWriter(new FileWriter(file));
    }

    public void handleData(String dataToWrite) throws IOException {
        writer.write(dataToWrite);
        writer.newLine();
        writer.flush();
    }

    public void close() {
        try {
            writer.close();
        } catch (IOException e) {
            SincLogger.logError(ERROR_MSG_CLOSE_STREAM, e);
        }
    }
}
