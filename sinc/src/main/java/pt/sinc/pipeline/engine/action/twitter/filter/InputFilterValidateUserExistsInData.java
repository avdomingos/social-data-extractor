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

package pt.sinc.pipeline.engine.action.twitter.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import pt.sinc.logger.SincLogger;
import pt.sinc.pipeline.engine.PipelineExecutionAction;
import pt.sinc.pipeline.engine.PipelineObject;
import pt.sinc.pipeline.engine.PipelineObjectType;
import pt.sinc.pipeline.engine.PipelineStage;
import pt.sinc.pipeline.engine.action.AbstractPipelineAction;
import pt.sinc.pipeline.engine.annotation.PipelineAction;
import pt.sinc.pipeline.engine.exception.PipelineActionException;

import java.io.IOException;
import java.util.Map;

@PipelineAction
        (
                value = PipelineStage.FILTER
        )
public class InputFilterValidateUserExistsInData extends AbstractPipelineAction {

    private static final String EXCEPTION_MESSAGE_PIPELINE_DATA_ANALYSIS = "Error filtering PipelineObjectData.";

    protected String EXCEPTION_NODE_NOT_FOUND;
    protected String NODE_NAME_FOR_VALIDATION;

    public InputFilterValidateUserExistsInData() {
        EXCEPTION_NODE_NOT_FOUND = "No user found.";
        NODE_NAME_FOR_VALIDATION = "user";
    }

    ObjectMapper mapper = new ObjectMapper();

    @Override
    public PipelineObject executeAction(PipelineObject item) throws PipelineActionException {
        if (item.getPipelineObjectType() != PipelineObjectType.JSON) {
            throw new PipelineActionException(EXCEPTION_MESSAGE_JSON_PIPELINE_OBJECTS);
        }
        validateObject(item);
        return item;
    }

    protected void validateObject(PipelineObject item) throws PipelineActionException {
        try {
            Map twitterData = mapper.readValue(item.getDataToAnalyze(), Map.class);
            if (!twitterData.containsKey(NODE_NAME_FOR_VALIDATION) || isNodeEmpty(twitterData)) {
                item.removeFromPipeline(EXCEPTION_NODE_NOT_FOUND);
                item.setExecutionStatus(PipelineExecutionAction.CANCEL);
            }

        } catch (IOException e) {
            throw new PipelineActionException(EXCEPTION_MESSAGE_PIPELINE_DATA_ANALYSIS, e);
        }
    }

    private boolean isNodeEmpty(Map twitterData) {
        return ((Map) twitterData.get(NODE_NAME_FOR_VALIDATION)).isEmpty();
    }
}
