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

import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import stratex.twitter.provider.auth.exception.ConnectionProviderException;
import stratex.twitter.provider.connection.IConnectorProvider;
import stratex.twitter.provider.parameter.IParameter;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class OAuthConnector implements IConnectorProvider<OAuthConnectorParameters> {

    public static final String ERROR_MESSAGE = "Unable to create OAuthConnector.";

    public HttpRequestBase getConnector(OAuthConnectorParameters connectorParameters) throws ConnectionProviderException {
        HttpPost methodToReturn = null;
        try {
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            for (IParameter param : connectorParameters.getParameters()) {
                params.add(new BasicNameValuePair(param.getParameterName(), param.getParameterValue()));
            }

            methodToReturn = new HttpPost(connectorParameters.getApiMethodURI().toString());

            UrlEncodedFormEntity postEntity = new UrlEncodedFormEntity(params,
                    HTTP.UTF_8);
            methodToReturn.setEntity(postEntity);

            CommonsHttpOAuthConsumer consumer =
                    new CommonsHttpOAuthConsumer(
                            connectorParameters.getAPIkey(),
                            connectorParameters.getAPISecret());

            consumer.setTokenWithSecret(
                    connectorParameters.getAccessToken(),
                    connectorParameters.getAccessTokenSecret());

            consumer.sign(methodToReturn);

        } catch (URISyntaxException e) {
            throw new ConnectionProviderException(ERROR_MESSAGE , e);
        } catch (OAuthExpectationFailedException e) {
            throw new ConnectionProviderException(ERROR_MESSAGE , e);
        } catch (OAuthCommunicationException e) {
            throw new ConnectionProviderException(ERROR_MESSAGE , e);
        } catch (OAuthMessageSignerException e) {
            throw new ConnectionProviderException(ERROR_MESSAGE , e);
        } catch (UnsupportedEncodingException e) {
            throw new ConnectionProviderException(ERROR_MESSAGE , e);
        }
        return methodToReturn;
    }
}
