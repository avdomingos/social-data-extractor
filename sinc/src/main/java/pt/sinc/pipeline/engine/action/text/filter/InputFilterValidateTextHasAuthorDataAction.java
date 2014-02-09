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

package pt.sinc.pipeline.engine.action.text.filter;

import pt.sinc.pipeline.engine.PipelineObject;
import pt.sinc.pipeline.engine.PipelineStage;
import pt.sinc.pipeline.engine.User;
import pt.sinc.pipeline.engine.action.text.AbstractTextPipelineAction;
import pt.sinc.pipeline.engine.annotation.PipelineAction;
import pt.sinc.pipeline.engine.basic.exception.PipelineException;
import pt.sinc.pipeline.engine.exception.PipelineObjectException;


@PipelineAction
        (
                value = PipelineStage.FILTER
        )
public class InputFilterValidateTextHasAuthorDataAction extends AbstractTextPipelineAction {

    private static final String NO_DATA_TO_ANALYZE = "No data to analyze!";
    private static final String ERROR_OBTAINING_USER_INFORMATION = "Error obtaining user information.";

    @Override
    public PipelineObject executeAction(PipelineObject item) throws PipelineException {
        String data = item.getDataToAnalyze();
        validatePipelineObjectType(item);
        if (data.isEmpty()) {
            item.removeFromPipeline(NO_DATA_TO_ANALYZE);
        }
        // Obtain username or user ID
        int idx = data.indexOf("_");
        if (idx > 0) {
            try {
                String username = data.substring(0, idx);
                username = username.replaceAll(specialCharsRegex,"");
                item.setUser(new User(username, username));
            } catch (PipelineObjectException e) {
                throw new PipelineException(ERROR_OBTAINING_USER_INFORMATION);
            }
        }
        // If no author is found then we are not interested in the post
        else
        {
            item.removeFromPipeline("No author found!");
        }
        return item;
    }
}
