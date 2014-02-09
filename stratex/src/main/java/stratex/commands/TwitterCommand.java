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

package stratex.commands;

import pt.command.annotation.CommandAnnotation;
import pt.command.base.Command;
import stratex.log.StratexLogger;
import stratex.twitter.extractor.BaseExtractor;
import stratex.twitter.extractor.datahandlers.DataHandlerFactory;
import stratex.twitter.extractor.datahandlers.IDataHandler;
import stratex.twitter.extractor.datahandlers.exception.DataHandlerException;
import stratex.twitter.provider.connection.ConnectorFactory;
import stratex.twitter.provider.connection.IConnectorProvider;
import stratex.twitter.provider.exception.ConnectorFactoryException;
import stratex.utils.InetUtils;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;

@CommandAnnotation
        (
                command = "TwitterCommand",
                usage = "TwitterCommand -networkAddress <xxx.xxx.xxx.xxx> -username <username> -password <password>",
                description = ""
        )
public abstract class TwitterCommand extends Command {

    private static final String networkAddressKey = "networkAddress";
    private static final String usernameKey       = "username";
    private static final String passwordKey       = "password";

    private String networkAddress;
    private String username;
    private String password;

    protected static IConnectorProvider connProvider;
    protected static IDataHandler dataHandler;

    private static final String ERROR_MSG_PROBLEM_LOADING_A_CONNECTOR = "Error initializing base Twitter Extractor base command. Problem loading a connector.";
    private static final String ERROR_MSG_PROBLEM_LOADING_A_DATA_HANDLER = "Error initializing base Twitter Extractor base command. Problem loading a data handler.";

    static
    {
        try {
            connProvider = ConnectorFactory.obtainDefaultConnector();
        } catch (ConnectorFactoryException e) {
            StratexLogger.logError(ERROR_MSG_PROBLEM_LOADING_A_CONNECTOR, e);
        }
        try {
            dataHandler = DataHandlerFactory.getDataHandler();
        } catch (DataHandlerException e) {
            StratexLogger.logError(ERROR_MSG_PROBLEM_LOADING_A_DATA_HANDLER, e);
        }
    }
    protected TwitterCommand(){
    }

    protected void initializeDefaultValues(Map<String,String> values)
    {
        networkAddress = values.get(networkAddressKey);
        username       = values.get(usernameKey);
        password       = values.get(passwordKey);

        StratexLogger.logInfo(String.format(
                "CommandType: %s NetworkAddress: %s Username: %s Password: %s",
                this.getClass().getName(),
                networkAddress,
                username,
                password
        ));
    }

    protected BaseExtractor initializeBaseExtractor() throws UnknownHostException {
        InetAddress address = InetUtils.getInetAddressFromContextualRepresentation(networkAddress);
        return new BaseExtractor(connProvider, dataHandler, username, password, address);
    }

    protected BaseExtractor initializeBaseExtractorWithDefaultIP() throws UnknownHostException {
        return new BaseExtractor(connProvider, dataHandler, username, password);
    }

}
