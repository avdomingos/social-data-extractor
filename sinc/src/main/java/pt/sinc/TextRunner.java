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

package pt.sinc;

import pt.sinc.data.input.interaction.ISocialDataProvider;
import pt.sinc.data.input.interaction.text.TextDataProvider;
import pt.sinc.data.output.AutoFileOutputDataHandler;
import pt.sinc.data.output.IOutputDataHandler;
import pt.sinc.executor.SincExecutor;
import pt.sinc.pipeline.engine.PipelineObjectType;
import pt.sinc.pipeline.engine.action.AbstractPipelineAction;
import pt.sinc.pipeline.engine.action.text.filter.InputFilterValidateTextHasAuthorDataAction;
import pt.sinc.pipeline.engine.action.text.filter.InputFilterValidateTextHasUsersDataAction;
import pt.sinc.pipeline.engine.action.text.reconstruct.TextDataReconstructAction;
import pt.sinc.pipeline.engine.basic.BasicPipelineEngine;

import java.io.IOException;
import java.util.ArrayList;

public class TextRunner {
    public void runSinc(String fileWithData) throws IOException {

        ISocialDataProvider provider = new TextDataProvider(fileWithData);


        ArrayList<AbstractPipelineAction> actions = new ArrayList<AbstractPipelineAction>();

        String baseOutputPath = PropertyLoader.getProperties().get("OutputPath").toString();

        if (!baseOutputPath.endsWith("/"))
            baseOutputPath += "/";

        // Initialize actions
        actions.add(new InputFilterValidateTextHasAuthorDataAction());
        actions.add(new InputFilterValidateTextHasUsersDataAction());
        actions.add(new TextDataReconstructAction());

        IOutputDataHandler dataHandler;

        BasicPipelineEngine pipeline = new BasicPipelineEngine(actions);
        SincExecutor se = null;
        se = new SincExecutor(provider, pipeline, new AutoFileOutputDataHandler(baseOutputPath + ".graph"), PipelineObjectType.TEXT);
        se.run();
        provider.close();
    }
}


