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
import pt.command.exception.ExitWithoutSuccessException;
import stratex.twitter.extractor.BaseExtractor;
import stratex.twitter.extractor.datahandlers.exception.DataHandlerException;
import stratex.twitter.provider.exception.ConnectorFactoryException;
import stratex.twitter.provider.parameter.AbstractParameter;
import stratex.twitter.provider.parameter.streamingAPI.Follow;
import stratex.twitter.provider.parameter.streamingAPI.StreamingAPIMethod;

import java.io.PrintStream;
import java.net.UnknownHostException;
import java.util.Map;

@CommandAnnotation
(
    command = "TwitterFollow",
    usage = "TwitterFollow -networkAddress <xxx.xxx.xxx.xxx> -username <username> -password <password> -users <user1,user2,user3,...,userN>",
    description = "Command to follow specified users and store tweets via the specified DataHandler in Stratex Properties file"
)
public class TwitterFollowCommand extends TwitterCommand {

    private static final String usersFollowKey = "users";

    public TwitterFollowCommand() {
        super();
    }

    @Override
    protected void commandAction(PrintStream printStream, Map<String, String> stringStringMap) throws ExitWithoutSuccessException {
        // Get users to follow

        initializeDefaultValues(stringStringMap);
        String[] usersToFollow = stringStringMap.get(usersFollowKey).split(";");
        try
        {
            BaseExtractor baseExtractor = this.initializeBaseExtractor();
            baseExtractor.extract(
                    StreamingAPIMethod.STATUSES_FILTER,
                    new AbstractParameter[] {
                            new Follow(usersStringToLong(usersToFollow))
                    }
            );

        } catch (DataHandlerException e) {
            throw new ExitWithoutSuccessException(e.getMessage());
        } catch (ConnectorFactoryException e) {
            throw new ExitWithoutSuccessException(e.getMessage());
        } catch (UnknownHostException e) {
            throw new ExitWithoutSuccessException(e.getMessage());
        } catch (Exception e) {
            throw new ExitWithoutSuccessException(e.getMessage());
        }
    }

    protected long[] usersStringToLong(String[] usersToFollow) {
        long[] usersToFollowIDs = new long[usersToFollow.length];;
        for (int i = 0; i < usersToFollow.length; i++) {
            usersToFollowIDs[i] = Long.parseLong(usersToFollow[i]);
        }
        return usersToFollowIDs;
    }
}
