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

import stratex.twitter.provider.connection.ConnectorParameters;
import stratex.twitter.provider.parameter.AbstractParameter;
import stratex.twitter.provider.parameter.streamingAPI.StreamingAPIMethod;

public class OAuthConnectorParameters extends ConnectorParameters {

    private String _oAuthComsumerKey;
    private String _oAuthComsumerSectet;
    private String _oAuthComsumerToken;
    private String _oAuthComsumerTokenSecret;

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
     * @param httpMethod
     */
    public OAuthConnectorParameters(
            String encoding,
            int port,
            String host,
            String protocol,
            StreamingAPIMethod apiMethod,
            String apiLevel,
            String httpMethod,
            String oAuthComsumerKey,
            String oAuthComsumerSecret,
            String oAuthComsumerToken,
            String oAuthComsumerTokenSecret,
            AbstractParameter... parameters) {
        super(encoding, port, host, protocol, apiMethod, apiLevel, parameters);
        _oAuthComsumerKey = oAuthComsumerKey;
        _oAuthComsumerSectet = oAuthComsumerSecret;
        _oAuthComsumerToken = oAuthComsumerToken;
        _oAuthComsumerTokenSecret = oAuthComsumerTokenSecret;
    }

    public String getOAuthComsumerKey() {
        return _oAuthComsumerKey;
    }
    public String getOAuthComsumerSectet() {
        return _oAuthComsumerSectet;
    }
    public String getOAuthComsumerToken() {
        return _oAuthComsumerToken;
    }
    public String getOAuthComsumerTokenSecret() {
        return _oAuthComsumerTokenSecret;
    }
}
