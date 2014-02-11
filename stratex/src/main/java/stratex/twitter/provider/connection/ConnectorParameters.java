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

import stratex.twitter.provider.parameter.AbstractParameter;
import stratex.twitter.provider.parameter.streamingAPI.StreamingAPIMethod;

import java.net.URI;
import java.net.URISyntaxException;

public abstract class ConnectorParameters {
    // typically "stream.twitter.com"
    private String host;
    // http or https
    private String protocol;
    // 80 for http, 443 for https
    private int port;
    // for example, "/1/statuses/filter.json"
    private StreamingAPIMethod apiMethod;
    // parameters to pass to the streamingAPI method. e.g.  "track=portugal"
    private AbstractParameter[] parameters;
    // default "UTF-8"
    private String encoding;
    // for example, api 1 -  "1"
    private String apiLevel;

    /**
     * Constructor for ConnectorParameters
     * @param encoding encoding for the Request
     * @param parameters parameters list
     * @param port Connector port (typically 443, as StreamingAPI uses SSL)
     * @param host StreamingAPI host (typically stream.twitter.com)
     * @param protocol HTTP or HTTPS
     * @param apiMethod StreamingAPI Method to invoke, most common is STATUSES_FILTER
     * @param apiLevel API Level (0,1,2,...) Current level is 1
     */
    public ConnectorParameters(
            String encoding,
            int port,
            String host,
            String protocol,
            StreamingAPIMethod apiMethod,
            String apiLevel,
            AbstractParameter... parameters) {
        if (encoding == null){ throw new IllegalArgumentException("encoding must have a valid value."); }
        this.encoding = encoding;

        if (host == null) {throw new IllegalArgumentException("host must have a valid value."); }
        this.host = host;

        if (protocol == null){ throw new IllegalArgumentException("protocol must have a valid value."); }
        this.protocol = protocol;
        if (apiMethod == null){ throw new IllegalArgumentException("apiMethod must have a valid value."); }
        this.apiMethod = apiMethod;
        if (apiLevel == null){ throw new IllegalArgumentException("apiLevel must have a valid value."); }
        this.apiLevel = apiLevel;
        this.port = port;
        this.parameters = parameters;
    }




    public String getHost() { return host; }
    public String getProtocol() { return protocol; }
    public int getPort() { return port; }
    public StreamingAPIMethod getMethod() { return apiMethod; }
    public AbstractParameter[] getParameters() { return parameters; }
    public String getEncoding() { return encoding; }
    public URI getApiMethodURI() throws URISyntaxException{
            return new URI(String.format("%s://%s/%s/%s", protocol,host,apiLevel ,getApiMethodName(apiMethod)));
    }

    private String getApiMethodName(StreamingAPIMethod apiMethod) {
        switch(apiMethod)
        {
            case STATUSES_FILTER:
                return "statuses/filter.json";
            case STATUSES_LINKS:
                return "statuses/links.json";
            case STATUSES_RETWEET:
                return "statuses/retweet.json";
            default:
                return "statuses/filter.json";
        }
    }

}
