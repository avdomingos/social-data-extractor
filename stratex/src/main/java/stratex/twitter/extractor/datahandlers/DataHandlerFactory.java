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

package stratex.twitter.extractor.datahandlers;

import stratex.properties.PropertyLoader;
import stratex.twitter.extractor.datahandlers.exception.DataHandlerException;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public class DataHandlerFactory {

    private static final String DEFAULT_DATA_HANDLER = "stratex.twitter.extractor.datahandlers.db.MongoDBDataHandler";
    private static final String DATA_HANDLER_INSTANTIATION_ERROR = "Could not instantiate a DataHandler.";
    public static final String STRATEX = "Stratex";
    public static final String DATA_HANDLER = "DataHandler";

    public static IDataHandler getDataHandler() throws DataHandlerException {
        IDataHandler retobj = null;
        Class cls;
        try {
            cls = getClassBasedOnClassName(STRATEX, DATA_HANDLER);
        } catch (ClassNotFoundException e) {
            throw new DataHandlerException(DATA_HANDLER_INSTANTIATION_ERROR, e);
        } catch (IOException e) {
            throw new DataHandlerException(DATA_HANDLER_INSTANTIATION_ERROR, e);
        }
        if (cls != null) {
            try {
                retobj = (IDataHandler) cls.getConstructor().newInstance();
            } catch (InstantiationException e) {
                throw new DataHandlerException(DATA_HANDLER_INSTANTIATION_ERROR, e);
            } catch (IllegalAccessException e) {
                throw new DataHandlerException(DATA_HANDLER_INSTANTIATION_ERROR, e);
            } catch (InvocationTargetException e) {
                throw new DataHandlerException(DATA_HANDLER_INSTANTIATION_ERROR, e);
            } catch (NoSuchMethodException e) {
                throw new DataHandlerException(DATA_HANDLER_INSTANTIATION_ERROR, e);
            }
        }
        return retobj;
    }

    private static Class<?> getClassBasedOnClassName(String propertiesName, String property) throws ClassNotFoundException, IOException {
        String className = DEFAULT_DATA_HANDLER;
        if (PropertyLoader.getProperties(propertiesName) != null)
        {
            className = PropertyLoader.getProperties(propertiesName).getProperty(property);
        }
        return Class.forName(className);
    }
}
