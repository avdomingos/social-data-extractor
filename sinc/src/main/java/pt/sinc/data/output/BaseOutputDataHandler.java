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

import pt.sinc.PropertyLoader;
import pt.sinc.logger.SincLogger;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

/**
 *
 */
public class BaseOutputDataHandler implements  IOutputDataHandler{
    private FileWriter fw = null;
    private BufferedWriter writer = null;

    public BaseOutputDataHandler() throws IOException {
        String outputFilePath = (String) PropertyLoader.getProperties(this.getClass().getSimpleName()).get("outputFilename");
        fw = new FileWriter(outputFilePath, true);
        writer = new BufferedWriter(fw);
    }

    public void handleData(String dataToWrite) throws IOException {
        writer.write(dataToWrite);
        writer.newLine();
    }

    public void close(){
        try {
            fw.close();
            writer.close();
        } catch (IOException e) {
            SincLogger.logError("BaseOutputDataHandler: Error closing stream.", e);
        }
    }

}
