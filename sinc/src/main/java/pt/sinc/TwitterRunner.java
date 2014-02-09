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

import pt.sinc.data.input.interaction.text.InputDataProvider;
//import pt.sinc.data.input.interaction.mongo.MongoDBInteractionProvider;
import pt.sinc.data.output.AutoFileOutputDataHandler;
import pt.sinc.data.output.IOutputDataHandler;
import pt.sinc.executor.SincExecutor;
import pt.sinc.logger.SincLogger;
import pt.sinc.pipeline.engine.action.AbstractPipelineAction;
import pt.sinc.pipeline.engine.action.twitter.filter.InputFilterValidatePostHasReferencesToEntities;
import pt.sinc.pipeline.engine.action.twitter.filter.InputFilterValidateUserExistsInData;
import pt.sinc.pipeline.engine.action.twitter.reconstruct.BasicReconstructAction;
import pt.sinc.pipeline.engine.action.twitter.reconstruct.ObtainInteractionDateAction;
import pt.sinc.pipeline.engine.basic.BasicPipelineEngine;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ScheduledThreadPoolExecutor;

public class TwitterRunner implements Observer {
    private static final String MSG_STARTING_SINC_EXECUTION = "Starting SINC execution.";
    private static final String MSG_ENDING_SINC_EXECUTION = "Ending SINC execution.";
    private static CountDownLatch latch;
    private static final String ERROR_MSG_SINC_EXECUTOR = "Error creating the SincExecutor.";
    private static final String AN_ERROR_HAS_OCCURRED_WHILE_WAITING_FOR_THREAD_COMPLETION = "An error has occurred while waiting for thread completion.";
    private static String baseOutputPath = null;

    public void runSinc(int numberRunningThreads) throws IOException {

        latch = new CountDownLatch(numberRunningThreads);
        //MongoDBInteractionProvider provider = new MongoDBInteractionProvider();
        InputDataProvider provider = new InputDataProvider();

        baseOutputPath = PropertyLoader.getProperties().get("OutputPath").toString();
        if (!baseOutputPath.endsWith("/"))
            baseOutputPath += "/";

        ScheduledThreadPoolExecutor threadPoolExecutor = new ScheduledThreadPoolExecutor(numberRunningThreads);
        IOutputDataHandler dataHandler;

        SincLogger.logInfo(MSG_STARTING_SINC_EXECUTION);

        for (int i = 0; i < numberRunningThreads; ++i) {

            // Initialize actions
            ArrayList<AbstractPipelineAction> actions = new ArrayList<AbstractPipelineAction>();
            actions.add(new InputFilterValidateUserExistsInData());
            actions.add(new InputFilterValidatePostHasReferencesToEntities());
            actions.add(new BasicReconstructAction());
            actions.add(new ObtainInteractionDateAction());

            BasicPipelineEngine pipeline = new BasicPipelineEngine(actions);
            SincExecutor se = null;
            try {
                se = new SincExecutor(provider, pipeline, new AutoFileOutputDataHandler(baseOutputPath + i + ".graph"));
            } catch (IOException e) {
                SincLogger.logError(ERROR_MSG_SINC_EXECUTOR, e);
            }
            se.addObserver(this);
            threadPoolExecutor.execute(se);
        }

        boolean isRunning = true;

        while (isRunning) {
            try {
                latch.await();
                isRunning = false;
            } catch (InterruptedException e) {
                SincLogger.logError(AN_ERROR_HAS_OCCURRED_WHILE_WAITING_FOR_THREAD_COMPLETION, e);
            }
        }
        provider.close();
        SincLogger.logInfo(MSG_ENDING_SINC_EXECUTION);
    }

    public synchronized void update(Observable o, Object arg) {
        latch.countDown();
    }
}
