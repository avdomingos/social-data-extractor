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
import stratex.log.StratexLogger;
import stratex.twitter.extractor.BaseExtractor;
import stratex.twitter.provider.parameter.AbstractParameter;
import stratex.twitter.provider.parameter.streamingAPI.StreamingAPIMethod;
import stratex.twitter.provider.parameter.streamingAPI.Track;

import java.io.PrintStream;
import java.net.UnknownHostException;
import java.util.Map;

@CommandAnnotation(
        command     = "TwitterTrack",
        usage       = "TwitterTrack -networkAddress <xxx.xxx.xxx.xxx> -username <username> -password <password> -topics <topic1,topic2,topic3,...,topicN>",
        description = "Command to follow specified topics and store tweets via the specified DataHandler in Stratex Properties file"
)
public class TwitterTrackCommand extends TwitterCommand {

    protected static final String topicsToTrackKey = "topics";

    public TwitterTrackCommand(){
        super();
    }

    @Override
    protected void commandAction(PrintStream printStream, Map<String, String> stringStringMap) throws ExitWithoutSuccessException {
        initializeDefaultValues(stringStringMap);
        String[] topics = stringStringMap.get(topicsToTrackKey).split(";");

        try {
            BaseExtractor baseExtractor = initializeBaseExtractor();
            baseExtractor.extract(StreamingAPIMethod.STATUSES_FILTER, new AbstractParameter[] {new Track(topics)} );
        } catch (UnknownHostException e) {
            StratexLogger.logError("Problem finding host.",e);
        } catch (Exception e) {
            StratexLogger.logError("Generic error.",e);
        }
    }
}
