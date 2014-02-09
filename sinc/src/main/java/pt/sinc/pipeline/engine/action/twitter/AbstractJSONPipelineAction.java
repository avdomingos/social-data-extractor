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

package pt.sinc.pipeline.engine.action.twitter;

import com.fasterxml.jackson.databind.ObjectMapper;
import pt.sinc.pipeline.engine.PipelineObject;
import pt.sinc.pipeline.engine.action.AbstractPipelineAction;
import pt.sinc.pipeline.engine.exception.PipelineActionException;

import java.io.IOException;
import java.util.Map;

public abstract class AbstractJSONPipelineAction extends AbstractPipelineAction {
    protected ObjectMapper mapper = new ObjectMapper();
    private static final String ERROR_OBTAINING_A_JSON_OBJECT = "error obtaining a JSON object";

    protected Map obtainJSONObject(PipelineObject item) throws PipelineActionException {
        try {
            return mapper.readValue(item.getDataToAnalyze(), Map.class);
        } catch (IOException e) {
            throw new PipelineActionException(ERROR_OBTAINING_A_JSON_OBJECT, e);
        }
    }
}
