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

package stratex.twitter.provider.connection;

import stratex.properties.PropertyLoader;
import stratex.twitter.provider.auth.basicauth.BasicAuthenticationConnector;
import stratex.twitter.provider.auth.oauth.OAuthConnector;
import stratex.twitter.provider.exception.ConnectorFactoryException;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.security.InvalidParameterException;

public class ConnectorFactory {

    private static final String CONNECTOR_PROVIDER_DEFAULT_ERROR_MESSAGE = "An error has occurred while instantiating a ConnectorProvider.";

    // Returns the default IConnector implementation defined in the application
    public static IConnectorProvider obtainDefaultConnector() throws ConnectorFactoryException {
        return obtainConnectorDefinedInProperties() != null ? obtainConnectorDefinedInProperties() : new BasicAuthenticationConnector();
    }

    // Returns the IConnector implementation based on the ConnectorType @type
    public static IConnectorProvider obtainConnector(ConnectorType type) throws ConnectorFactoryException {
        if (type == null)
            throw new ConnectorFactoryException("", new InvalidParameterException("ConnectorType cannot be null."));
        if (type == ConnectorType.OAUTH_CONNECTOR)
            return new OAuthConnector();
        else if(type == ConnectorType.BASIC_AUTHENTICATION_CONNECTOR)
            return new BasicAuthenticationConnector();
        return null;
    }

    private static IConnectorProvider obtainConnectorDefinedInProperties() throws ConnectorFactoryException{
        IConnectorProvider retobj = null;
        try {
            String className = PropertyLoader.getProperties().getProperty("ConnectorProvider");
            Class cls = null;
            try {
                cls = Class.forName(className);
            } catch (ClassNotFoundException e) {
                throw new ConnectorFactoryException(CONNECTOR_PROVIDER_DEFAULT_ERROR_MESSAGE, e);
            }
            retobj = (IConnectorProvider) cls.getConstructor().newInstance();
        } catch (IOException e) {
            throw new ConnectorFactoryException(CONNECTOR_PROVIDER_DEFAULT_ERROR_MESSAGE, e);
        } catch (InvocationTargetException e) {
            throw new ConnectorFactoryException(CONNECTOR_PROVIDER_DEFAULT_ERROR_MESSAGE, e);
        } catch (NoSuchMethodException e) {
            throw new ConnectorFactoryException(CONNECTOR_PROVIDER_DEFAULT_ERROR_MESSAGE, e);
        } catch (InstantiationException e) {
            throw new ConnectorFactoryException(CONNECTOR_PROVIDER_DEFAULT_ERROR_MESSAGE, e);
        } catch (IllegalAccessException e) {
            throw new ConnectorFactoryException(CONNECTOR_PROVIDER_DEFAULT_ERROR_MESSAGE, e);
        }
        return retobj;
    }
}
