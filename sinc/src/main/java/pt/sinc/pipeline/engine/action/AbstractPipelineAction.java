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

package pt.sinc.pipeline.engine.action;

import pt.sinc.pipeline.engine.PipelineObject;
import pt.sinc.pipeline.engine.basic.exception.PipelineException;
import pt.sinc.pipeline.engine.exception.PipelineActionException;

public abstract class AbstractPipelineAction {
    protected static final String EXCEPTION_MESSAGE_JSON_PIPELINE_OBJECTS = "This Pipeline Action only supports JSON pipeline objects.";
    protected static final String EXCEPTION_MESSAGE_TEXT_PIPELINE_OBJECTS = "This Pipeline Action only supports plain text pipeline objects.";
    public abstract PipelineObject executeAction(PipelineObject item) throws PipelineException;

    public void validatePipelineObjectType(PipelineObject item) throws PipelineActionException {
        this.validatePipelineObjectType(item);
    }
}
