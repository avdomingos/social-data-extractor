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

package pt.sinc.pipeline.engine.action.text.reconstruct;

import pt.sinc.pipeline.engine.PipelineObject;
import pt.sinc.pipeline.engine.PipelineStage;
import pt.sinc.pipeline.engine.User;
import pt.sinc.pipeline.engine.action.text.AbstractTextPipelineAction;
import pt.sinc.pipeline.engine.annotation.PipelineAction;
import pt.sinc.pipeline.engine.basic.exception.PipelineException;

@PipelineAction
        (
                value = PipelineStage.RECONSTRUCT
        )
public class TextDataReconstructAction extends AbstractTextPipelineAction {


/*
    static final String regex = "\\@\\{(.*?)\\}";
    static final String regex4simpleTopic = "((#(.*?)\\s)|(@(.*)))";
    static final String regex4CompositeTopic = "\\#\\[(.*?)\\]";

    static final String regex4SimpleUser = "((@(.*?)\\s)|(@(.*)))";
    static final String regex4CompositeUser = "\\@\\[(.*?)\\]";
 */

    @Override
    public PipelineObject executeAction(PipelineObject item) throws PipelineException {
        String data = item.getDataToAnalyze();
        compareWithRegex(item, regex4Users);
        return item;
    }

    private void compareWithRegex(PipelineObject item, String regex) {

        String username = "";
        matcher = ptn.matcher(item.getDataToAnalyze());
        while (matcher.find()) {
            username = matcher.group();
            username = username.replaceAll(specialCharsRegex,"");
            item.addMentionedUser(new User(username, username));
        }
    }
}
