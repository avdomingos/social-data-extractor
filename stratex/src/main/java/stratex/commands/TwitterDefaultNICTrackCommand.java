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

@CommandAnnotation
(
        command = "TwitterDefaultNICTrack",
        usage = "TwitterDefaultNICTrack -username <username> -password <password> -topics <topic1;topic2;...;topicN> ",
        description = "Fetches a list of users from mongoDB and follows users using Twitter's StreamingAPI"
)
public class TwitterDefaultNICTrackCommand extends TwitterTrackCommand {

    public TwitterDefaultNICTrackCommand(){
        super();
    }

    @Override
    protected void commandAction(PrintStream printStream, Map<String, String> stringStringMap) throws ExitWithoutSuccessException {
        initializeDefaultValues(stringStringMap);
        String[] topics = stringStringMap.get(topicsToTrackKey).split(";");

        try {
            BaseExtractor baseExtractor = initializeBaseExtractorWithDefaultIP();
            baseExtractor.extract(StreamingAPIMethod.STATUSES_FILTER, new AbstractParameter[] {new Track(topics)} );
        } catch (UnknownHostException e) {
            StratexLogger.logError(e.getMessage(), e);
        } catch (Exception e) {
            StratexLogger.logError(e.getMessage(), e);
        }
    }
}
