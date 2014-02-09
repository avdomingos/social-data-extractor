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

package stratex.twitter.provider.auth.basicauth;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;
import stratex.twitter.provider.auth.exception.ConnectionProviderException;
import stratex.twitter.provider.connection.ConnectorParameters;
import stratex.twitter.provider.connection.IConnectorProvider;
import stratex.twitter.provider.parameter.AbstractParameter;
import stratex.twitter.provider.parameter.streamingAPI.StreamingAPIMethod;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;

public class BasicAuthenticationConnector implements IConnectorProvider {

    public HttpRequestBase getConnector(ConnectorParameters connectorParameters) throws ConnectionProviderException {

        // Get the baseRequest based on the streamingAPI method
        if (connectorParameters.getMethod() == StreamingAPIMethod.STATUSES_FILTER)
            return prepareHttpPostConnector(connectorParameters);
        else
            return prepareHttpGetConnector(connectorParameters);
    }

    /**
     * Returns a HttpGet (extends HttpRequestBase) configured for the specified ConnectorParameters
     *
     * @param connectorParameters ConnectorParameters for configuration
     * @return The configured HttpGet object
     */
    private HttpRequestBase prepareHttpGetConnector(ConnectorParameters connectorParameters) {
        return new HttpGet();
    }

    /**
     * Returns a HttpPost (extends HttpRequestBase) configured for the specified ConnectorParameters
     *
     * @param connectorParameters ConnectorParameters for configuration
     * @return The configured HttpPost object
     * @throws stratex.twitter.provider.auth.exception.ConnectionProviderException Thrown when an error occurs while trying to
     * create a new HttpPost object.
     */
    private HttpRequestBase prepareHttpPostConnector(ConnectorParameters connectorParameters) throws ConnectionProviderException {
        HttpPost post = null;
        try {
            post = new HttpPost(connectorParameters.getApiMethodURI());
        } catch (URISyntaxException e) {
            throw new ConnectionProviderException("Unable to establish a proper HttpPost Request.", e);
        }
        StringEntity postEntity = null;
        try {
            String entityValue = "";
            for(AbstractParameter p : connectorParameters.getParameters())
            {
                // TODO: Verificar HttpEntity e ver como passar mais que um parametro (e se é possivel)
                if (!entityValue.equals(""))
                    entityValue += "&" + p.toString();
                else
                    entityValue += p.toString();
            }
            postEntity = new StringEntity(entityValue, HTTP.UTF_8);
        } catch (UnsupportedEncodingException e) {
            throw new ConnectionProviderException("Error establishing a connection.", e);
        }
        postEntity.setContentType("application/x-www-form-urlencoded");
        post.setEntity(postEntity);
        return post;
    }


}
