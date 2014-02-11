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

import stratex.twitter.provider.auth.oauth.OAuthConnector;
import stratex.twitter.provider.auth.oauth.OAuthConnectorParameters;
import stratex.twitter.provider.exception.ConnectorFactoryException;

public class ConnectorFactory {

    // Returns the default IConnector implementation defined in the application
    public static IConnectorProvider obtainDefaultConnector() throws ConnectorFactoryException {
        return new OAuthConnector();
    }
}
