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

package pt.properties;

import pt.exception.InvalidPropertiesException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class PropertyFileLoader {

    private static Properties properties = null;
    private static final String PROPERTIES_FILE_PATH = "AppFabric.Properties";

    public static Properties getProperties() throws InvalidPropertiesException {
        if (properties == null)
            //TODO:English please
            throw new InvalidPropertiesException(
                    String.format("As propriedades não foram correctamente carregadas. Verifique se o ficheiro %s existe e que o seu formato está correcto.", PROPERTIES_FILE_PATH));
        return properties;
    }

    static {
        Properties prop = new Properties();

        FileInputStream stream = null;
        try {
            File f = new File(PROPERTIES_FILE_PATH);
            System.out.println(f.getAbsolutePath());
            stream = new FileInputStream(f);
            prop.load(stream);
            properties = prop;
        } catch (FileNotFoundException e) {
            properties = null;
        } catch (IOException e) {
            properties = null;
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                    properties = null;
                }
            }
        }
    }
}
