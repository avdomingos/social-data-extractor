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

package java.pt.SINC.tests;

import org.junit.Test;
import pt.sinc.pipeline.engine.PipelineObject;
import pt.sinc.pipeline.engine.PipelineObjectType;
import pt.sinc.pipeline.engine.action.text.reconstruct.TextDataReconstructAction;

public class TextDataReconstructActionTest extends junit.framework.TestCase {
    public static String textForParsing = "Aqui vai uma menção sobre #lisboa ou #[noites de lisboa] relativos ao user @Andre e ao @{Hugo Ferreira}!";

    @Test
    public void testExecuteAction() throws Exception {
        TextDataReconstructAction action = new TextDataReconstructAction();
        PipelineObject<String> item = new PipelineObject<String>(PipelineObjectType.TEXT,textForParsing);

    }
}
