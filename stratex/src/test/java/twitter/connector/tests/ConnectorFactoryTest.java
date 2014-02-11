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
import stratex.twitter.provider.connection.ConnectorFactory;
import stratex.twitter.provider.exception.ConnectorFactoryException;

public class ConnectorFactoryTest {

    @Test
    public void testGetsAnyConnectorFromTheConnectorsFactory() throws ConnectorFactoryException {
        Assert.assertNotNull(ConnectorFactory.obtainDefaultConnector());
    }
}
