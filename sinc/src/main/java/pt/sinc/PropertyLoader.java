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

package pt.sinc;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class PropertyLoader {
    private static HashMap<String, Properties> propertiesCollection;
    private static final String PROPERTIES_FILE_EXTENSION = ".properties";

    static {
        propertiesCollection = new HashMap<String, Properties>();
    }

    /**
     * Gets a Properties collection based on propertiesCollectionName
     *
     * @param propertiesCollectionName Properties collection name to search for
     * @return Properties collection
     * @throws IOException
     */
    public static Properties getProperties(String propertiesCollectionName) throws IOException {
        if (!propertiesCollection.containsKey(propertiesCollectionName))
            propertiesCollection.put(propertiesCollectionName, getPropertiesFromFile(propertiesCollectionName + PROPERTIES_FILE_EXTENSION));
        return propertiesCollection.get(propertiesCollectionName);
    }

    public static Properties getProperties() throws IOException {
        return getProperties("sinc");
    }

    /**
     * Loads a properties collection from the specified filename
     *
     * @param absolutePathFileName path and filename for the properties to load
     * @return Properties collection
     * @throws IOException
     */
    private static Properties getPropertiesFromFile(String absolutePathFileName) throws IOException {
        Properties prop = new Properties();
        FileInputStream stream = null;
        try {
            File file = new File(absolutePathFileName);
            if (!file.exists())
                return null;
            stream = new FileInputStream(absolutePathFileName);
            prop.load(stream);
        } finally {
            if (stream != null)
                stream.close();
        }
        return prop;
    }

    /**
     * Creates a properties file based on a filename and a map structure
     *
     * @param simpleName file name appended with ".properties" extension
     * @param properties the key-value collection to write to the file
     */
    public static void generatePropertiesFile(String simpleName, Map<String, String> properties) throws IOException {
        String filename = simpleName + PROPERTIES_FILE_EXTENSION;
        OutputStreamWriter writer = null;
        try {
            writer = new FileWriter(filename);
            for (String s : properties.keySet()) {
                writer.write(s + "=" + properties.get(s));
                writer.write('\n');
            }
        }finally {
            if(writer!=null)
                writer.close();
        }
    }
}
