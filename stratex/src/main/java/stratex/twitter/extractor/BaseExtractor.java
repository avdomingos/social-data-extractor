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

package stratex.twitter.extractor;


import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import stratex.exceptions.StratexException;
import stratex.log.StratexLogger;
import stratex.twitter.extractor.datahandlers.IDataHandler;
import stratex.twitter.extractor.datahandlers.exception.DataHandlerException;
import stratex.twitter.provider.auth.basicauth.BasicAuthenticationConnector;
import stratex.twitter.provider.connection.ConnectorParameters;
import stratex.twitter.provider.connection.IConnectorProvider;
import stratex.twitter.provider.parameter.AbstractParameter;
import stratex.twitter.provider.parameter.streamingAPI.StreamingAPIMethod;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;

/**
 * Extracts data from Twitter's StreamingAPI
 */
public final class BaseExtractor {

    /**
     * Username for twitter's authentication
     */
    private String username;
    /**
     * Password for twitter's authentication
     */
    private String password;

    private IConnectorProvider connector;
    private IDataHandler dataHandler;
    private InetAddress addressToUse = null;


    /**
     * TwitterConnector constructor
     *
     * @param connector   an IConnectorProvider implementation to establish the connection to Twitter's streamingAPI.
     * @param dataHandler an IDataHandler implementation to handle the data retrieved from the streamingAPI.
     * @param user        Twitter username for streamingAPI's authentication.
     * @param pwd         Password associated to Twitter username for streamingAPI's authentication.
     */
    public BaseExtractor(IConnectorProvider connector, IDataHandler dataHandler, String user, String pwd) {
        username = user;
        password = pwd;

        this.connector = connector;
        this.dataHandler = dataHandler;
    }

    /**
     * TwitterConnector constructor
     *
     * @param connector    an IConnectorProvider implementation to establish the connection to Twitter's streamingAPI.
     * @param dataHandler  an IDataHandler implementation to handle the data retrieved from the streamingAPI.
     * @param user         Twitter username for streamingAPI's authentication.
     * @param pwd          Password associated to Twitter username for streamingAPI's authentication.
     * @param addressToUse InetAddress to use for this connector.
     */
    public BaseExtractor(IConnectorProvider connector, IDataHandler dataHandler, String user, String pwd, InetAddress addressToUse) {
        username = user;
        password = pwd;

        this.connector = connector;
        this.dataHandler = dataHandler;
        this.addressToUse = addressToUse;
    }

    /**
     * Extracts data from StreamingAPI
     *
     * @param method                 StreamingAPI method to use for extraction
     * @param streamingApiParameters StreamingAPI parameters. Check {https://dev.twitter.com/docs/streaming-apis} for what method/parameter combinations are allowed
     * @throws Exception
     */
    public void extract(StreamingAPIMethod method, AbstractParameter[] streamingApiParameters) throws Exception {
        ConnectorParameters params = new ConnectorParameters(
                "utf-8",
                443,
                "stream.twitter.com",
                "https",
                method,
                "1",
                streamingApiParameters
        );

        doHttpRequest(connector, params, dataHandler);
    }

    /**
     * Starts an HTTP Request to the Streaming API using the passed parameters
     *
     * @param connector The <code>IConnectorProvider</code> implementation to connect to the streamingAPI (BasicAuth, OAuth, ...)
     * @param params    An instance of <code>ConnectorParameter</code> containing all the necessary data to establish a connection
     * @param handler   <code>DataHandler</code> to use
     * @throws StratexException
     * @throws IOException
     */
    private void doHttpRequest(IConnectorProvider connector, ConnectorParameters params, IDataHandler handler) throws StratexException, IOException {

        StratexLogger.logInfo("Initiating httpRequest.");
        HttpRequestBase baseRequest = connector.getConnector(params);
        HttpHost host = new HttpHost(params.getHost(), params.getPort(), params.getProtocol());
        DefaultHttpClient httpclient = new DefaultHttpClient();

        // Use a specified inetAddress
        if (addressToUse != null) {
            httpclient.getParams().setParameter("http.route.local-address", addressToUse);
        }

        // Prepares basic authentication the basic authentication connector
        BasicHttpContext context = null;
        if (connector.getClass() == BasicAuthenticationConnector.class) {
            context = HttpBasicAuthenticationHelper.prepareHTTPBasicAuthentication
                    (
                            host,
                            httpclient,
                            username, //"pfc2012",
                            password //"pfciselandrehugo2012"
                    );
        }
        HttpResponse response;
        try {
            response = httpclient.execute(host, baseRequest, context);
        } catch (Exception e) {
            throw new StratexException("Error executing httpClient.", e);
        }
        try {
            if (response.getStatusLine().getStatusCode() != 200)
            {
                StratexException ex =  new StratexException("Request error: " + response.getStatusLine());
                StratexLogger.logWarn("HttpResponse NOK. Status code: " + response.getStatusLine(), ex);
                throw ex;
            }
            else {
                handleTwitterResponse(response, handler);
            }
        } catch (Exception e) {
            throw new StratexException("Error handling an httpResponse.", e);
        }
    }

    /**
     * Handles responses from StreamingAPI
     *
     * @param response HttpResponse to handle
     * @param handler  DataHandler that handles the response (typically will write something to a dabatase)
     * @throws StratexException
     */
    private void handleTwitterResponse(HttpResponse response, IDataHandler handler) throws StratexException {
        HttpEntity entity = response.getEntity();
        BufferedReader br = null;
        try {
            assert entity != null;
            br = new BufferedReader(new InputStreamReader(
                    entity.getContent(), "UTF-8"));

            boolean shouldRun = true;
            while (shouldRun) {
                String line;
                line = br.readLine();
                try {
                    if (line != null && !line.equals("")) {
                        handler.handleData(line);
                    }
                    else {
                        StratexLogger.logWarn("StreamingAPI stream is not returning data anymore. Closing the connection.");
                        shouldRun = false;
                    }
                } catch (DataHandlerException e) {
                    closeStream(br);
                    throw new StratexException("DataHandler error: Error extracting data in TwitterConnector!", e);
                }
            }
        } catch (IOException e) {
            throw new StratexException("IO error: Error extracting data in TwitterConnector!", e);
        } finally {
            closeStream(br);
        }
    }

    /**
     * Closes the http stream in use
     *
     * @param br Buffered Reader stream to close
     * @throws StratexException
     */
    private void closeStream(BufferedReader br) throws StratexException {
        if (br != null)
            try {
                br.close();
            } catch (IOException e) {
                throw new StratexException("Error closing HTTP stream", e);
            }

    }
}
