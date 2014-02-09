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

package stratex.twitter.extractor.datahandlers.text;

import stratex.twitter.extractor.datahandlers.IDataHandler;
import stratex.twitter.extractor.datahandlers.exception.DataHandlerException;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

public class TextDataHandler implements IDataHandler {

    private String outputFilePath;

    public TextDataHandler() throws IOException {
        outputFilePath = "outputFile" + new Date().getTime() + ".twitter";
    }

    /**
     * @param outputFilePath The output file where data should be written to
     */
    public TextDataHandler(String outputFilePath) {
        this.outputFilePath = outputFilePath;
    }

    /**
     * @param data data to handle
     * @throws stratex.twitter.extractor.datahandlers.exception.DataHandlerException
     *          DataHandler Exception is thrown in case of an unexpected error
     */
    public void handleData(String data) throws DataHandlerException {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(outputFilePath, true);
            fos.write(data.getBytes());
        } catch (FileNotFoundException fEx) {
            throw new DataHandlerException("Error creating the output file!", fEx);
        } catch (IOException ioEx) {
            throw new DataHandlerException("Error writing to the output file!", ioEx);
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    throw new DataHandlerException("Error closing output stream!", e);
                }
            }
        }
    }
}
