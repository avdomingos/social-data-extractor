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
import pt.sinc.pipeline.engine.*;
import pt.sinc.pipeline.engine.action.twitter.AbstractJSONPipelineAction;
import pt.sinc.pipeline.engine.annotation.PipelineAction;
import pt.sinc.pipeline.engine.exception.PipelineActionException;
import pt.sinc.pipeline.engine.exception.PipelineObjectException;

import java.util.ArrayList;
import java.util.Map;

@PipelineAction(value = PipelineStage.RECONSTRUCT)
public class BasicReconstructAction extends AbstractJSONPipelineAction {

    private static final String ERROR_OBTAINING_A_JSON_OBJECT = "error obtaining a JSON object";
    private static final String USERNAME_KEY = "screen_name";
    private static final String USERID_KEY = "id";
    private static final String ERROR_MSG_RECONSTRUCT_ACTION_ERROR = "BasicReconstructAction error. Item flagged for cancellation.";

    @Override
    public PipelineObject executeAction(PipelineObject item) throws PipelineActionException {
        if (item.getPipelineObjectType() != PipelineObjectType.JSON)
        {
            throw new PipelineActionException(EXCEPTION_MESSAGE_JSON_PIPELINE_OBJECTS);
        }
        Map userData = (Map) this.obtainJSONObject(item).get("user");
        Map entities = (Map) this.obtainJSONObject(item).get("entities");
        ArrayList user_mentions = (ArrayList)entities.get("user_mentions");
        for(int i = 0; i < user_mentions.size();++i)
        {
            Map user = (Map)user_mentions.get(i);
            item.addMentionedUser(new User((String) user.get(USERNAME_KEY), user.get(USERID_KEY)));
        }
        try {
            item.setUser(new User((String) userData.get(USERNAME_KEY),userData.get(USERID_KEY)));
        } catch (PipelineObjectException e) {
            item.setExecutionStatus(PipelineExecutionAction.CANCEL);
            SincLogger.logWarn(ERROR_MSG_RECONSTRUCT_ACTION_ERROR, e);
        }
       return item;
    }
}
