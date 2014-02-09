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
import stratex.twitter.provider.parameter.AbstractParameter;
import stratex.twitter.provider.parameter.streamingAPI.Follow;
import stratex.twitter.provider.parameter.streamingAPI.StreamingAPIMethod;
import stratex.twitter.provider.parameter.streamingAPI.Track;

import java.io.PrintStream;
import java.net.UnknownHostException;
import java.util.Map;

@CommandAnnotation
        (
                command = "TwitterFollowAndTrack",
                usage = "TwitterFollow -networkAddress <xxx.xxx.xxx.xxx> -username <username> -password <password> -users <user1;user2;user3;...;userN> -topics <topic1;topic2;topic3;...;topicN>",
                description = "Command to follow specified users and track topics. Stores tweets via the specified DataHandler in Stratex Properties file"
        )
public class TwitterFollowAndTrackCommand extends TwitterFollowCommand {

    private static final String usersKey = "users";
    private static final String topicsKey = "topics";

    public TwitterFollowAndTrackCommand(){
        super();
    }

    @Override
    protected void commandAction(PrintStream printStream, Map<String, String> stringStringMap) throws ExitWithoutSuccessException {
        String[] users, topics;

        users  = stringStringMap.get(usersKey).split(";");
        topics = stringStringMap.get(topicsKey).split(";");

        try {
            BaseExtractor be = this.initializeBaseExtractor();
            be.extract(
                    StreamingAPIMethod.STATUSES_FILTER,
                    new AbstractParameter[]
                            {
                                    new Track(topics),
                                    new Follow(usersStringToLong(users))
                            }
            );
        } catch (UnknownHostException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }
}
