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

package twitter.connector.tests;

import org.junit.Assert;
import org.junit.Test;
import stratex.twitter.provider.auth.basicauth.BasicAuthenticationConnector;
import stratex.twitter.provider.auth.oauth.OAuthConnector;
import stratex.twitter.provider.connection.ConnectorFactory;
import stratex.twitter.provider.connection.ConnectorType;
import stratex.twitter.provider.exception.ConnectorFactoryException;

public class ConnectorFactoryTest {

    @Test
    public void testGetsAnyConnectorFromTheConnectorsFactory() throws ConnectorFactoryException {
        Assert.assertNotNull(ConnectorFactory.obtainDefaultConnector());
    }

    @Test
    public void testGetsABasicAuthenticationConnectorFromTheConnectorsFactory() throws ConnectorFactoryException {

        Assert.assertTrue(ConnectorFactory.obtainConnector(ConnectorType.BASIC_AUTHENTICATION_CONNECTOR) instanceof BasicAuthenticationConnector);
    }

    @Test
    public void testGetsAnOpenAuthConnectorFromTheConnectorsFactory() throws ConnectorFactoryException {
        Assert.assertTrue(ConnectorFactory.obtainConnector(ConnectorType.OAUTH_CONNECTOR) instanceof OAuthConnector);
    }

    @Test(expected = ConnectorFactoryException.class)
    public void testThrowsExceptionWhenNullIsPassedAsConnectorType() throws ConnectorFactoryException {
        ConnectorFactory.obtainConnector(null);
    }


}
