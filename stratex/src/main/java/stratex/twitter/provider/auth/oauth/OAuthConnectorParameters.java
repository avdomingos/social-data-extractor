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

/**
 * Created by IntelliJ IDEA.
 * User: Hugo Ferreira
 * Date: 02-04-2012
 * Time: 21:59
 * To change this template use File | Settings | File Templates.
 */
public class OAuthConnectorParameters extends ConnectorParameters {

    //"vfMAFp6nZloisOKrHmwlg";
    private String _oAuthComsumerKey;
    //"BNyItEcKAtdkiFy9xdV19zoI9MA78UIe8KMDt3myk";
    private String _oAuthComsumerSectet;
    //"534744254-svzXTzyoU1ersTi97LHnDDqHoxHmLOQfah3IUIM3";
    private String _oAuthComsumerToken;
    //"FGVpTQ7BiqrL3qYW6tULK4NRvAawUJdLkEH3sDZQtk";
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
