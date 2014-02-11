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

package stratex.twitter.provider.auth.oauth;

import stratex.properties.PropertyLoader;
import stratex.twitter.provider.connection.ConnectorParameters;
import stratex.twitter.provider.parameter.AbstractParameter;
import stratex.twitter.provider.parameter.streamingAPI.StreamingAPIMethod;

import java.io.IOException;
import java.util.Properties;

public class OAuthConnectorParameters extends ConnectorParameters {

    private static final String API_KEY_PROPERTY = "APIkey";
    private static final String API_SECRET_PROPERTY = "APISecret";
    private static final String ACCESS_TOKEN_PROPERTY = "AccessToken";
    private static final String ACCESS_TOKEN_SECRET_PROPERTY = "AccessTokenSecret";

    private String APIkey;
    private String APISecret;
    private String AccessToken;
    private String AccessTokenSecret;

    /**
     * Constructor for ConnectorParameters
     *
     * @param encoding   enconding for the Request
     * @param parameters
     * @param port
     * @param host
     * @param protocol
     * @param apiMethod
     * @param apiLevel
     */
    public OAuthConnectorParameters(
            String encoding,
            int port,
            String host,
            String protocol,
            StreamingAPIMethod apiMethod,
            String apiLevel,
            AbstractParameter... parameters) throws IOException {
        super(encoding, port, host, protocol, apiMethod, apiLevel, parameters);

        // Obtain keys
        Properties oAuthProperties = PropertyLoader.getProperties("OAuth");

        APIkey = oAuthProperties.getProperty(API_KEY_PROPERTY);
        APISecret = oAuthProperties.getProperty(API_SECRET_PROPERTY);
        AccessToken = oAuthProperties.getProperty(ACCESS_TOKEN_PROPERTY);
        AccessTokenSecret = oAuthProperties.getProperty(ACCESS_TOKEN_SECRET_PROPERTY);


    }

    public String getAPIkey() {
        return APIkey;
    }

    public String getAPISecret() {
        return APISecret;
    }

    public String getAccessToken() {
        return AccessToken;
    }

    public String getAccessTokenSecret() {
        return AccessTokenSecret;
    }
}
