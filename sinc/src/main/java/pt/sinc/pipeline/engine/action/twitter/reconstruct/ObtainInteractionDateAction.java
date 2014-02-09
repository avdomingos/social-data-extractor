/*
*	This file is part of SINC.
*
*    SINC is free software: you can redistribute it and/or modify
*    it under the terms of the GNU General Public License as published by
*    the Free Software Foundation, either version 3 of the License, or
*    (at your option) any later version.
*
*    SINC is distributed in the hope that it will be useful,
*    but WITHOUT ANY WARRANTY; without even the implied warranty of
*    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
*    GNU General Public License for more details.
*
*    You should have received a copy of the GNU General Public License
*    along with SINC.  If not, see <http://www.gnu.org/licenses/>.
*/

package pt.sinc.pipeline.engine.action.twitter.reconstruct;

import pt.sinc.logger.SincLogger;
import pt.sinc.pipeline.engine.PipelineExecutionAction;
import pt.sinc.pipeline.engine.PipelineObject;
import pt.sinc.pipeline.engine.PipelineObjectType;
import pt.sinc.pipeline.engine.PipelineStage;
import pt.sinc.pipeline.engine.action.twitter.AbstractJSONPipelineAction;
import pt.sinc.pipeline.engine.annotation.PipelineAction;
import pt.sinc.pipeline.engine.basic.exception.PipelineException;
import pt.sinc.pipeline.engine.exception.PipelineActionException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

@PipelineAction(value = PipelineStage.RECONSTRUCT)
public class ObtainInteractionDateAction extends AbstractJSONPipelineAction {
    private static final String TWITTER_DATE_PATTERN = "EEE MMM dd HH:mm:ss Z yyyy";
    private static final String ERROR_MSG_UNPARSEABLE_DATE = "Could not parse tweet date to a valid format. Discarding this tweet.";

    @Override
    public PipelineObject executeAction(PipelineObject item) throws PipelineException {
        if (item.getPipelineObjectType() != PipelineObjectType.JSON)
        {
            throw new PipelineActionException(EXCEPTION_MESSAGE_JSON_PIPELINE_OBJECTS);
        }
        Date interactionDate = null;
        String date = this.obtainJSONObject(item).get("created_at").toString();
        try {
            SimpleDateFormat format = new SimpleDateFormat(TWITTER_DATE_PATTERN, Locale.ENGLISH);
            format.setTimeZone(TimeZone.getTimeZone("GMT"));
            interactionDate = format.parse(date);
        } catch (ParseException e) {
            item.setExecutionStatus(PipelineExecutionAction.CANCEL);
            SincLogger.logWarn(ERROR_MSG_UNPARSEABLE_DATE, e);
        }
        item.setInteractionDate(interactionDate);
        return item;
    }
}
