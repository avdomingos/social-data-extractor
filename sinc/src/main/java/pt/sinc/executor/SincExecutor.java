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

package pt.sinc.executor;

import pt.sinc.PropertyLoader;
import pt.sinc.data.input.interaction.ISocialDataProvider;
import pt.sinc.data.input.interaction.exception.SocialDataProviderException;
import pt.sinc.data.output.IOutputDataHandler;
import pt.sinc.logger.SincLogger;
import pt.sinc.pipeline.engine.*;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Observable;
import java.util.TimeZone;

public class SincExecutor extends Observable implements Runnable {

    private static final String ERROR_MSG_HANDLING_DATA_WITH_SOCIAL_DATA_PROVIDER = "Error handling data with SocialDataProvider.";
    private static final String ERROR_MSG_HANDLING_OUTPUT = "Error handling output.";
    private static final String MSG_TOTAL_NUMBER_OF_PROCESSED_NODES = "Total number of processed nodes: ";
    private static final String MSG_RECORDS_WRITTEN_TO_GRAPH_FILE = " records written to graph file";
    private DateFormat format = null;
    private static final String ERROR_MSG_UNABLE_TO_LOAD_DATE_FORMAT = "Could not load OutputDateFormat from Properties file. Using default value of \"dd-MM-yyyy HH:mm:ss\"";

    private ISocialDataProvider provider;
    private AbstractPipelineEngine pipeline;
    private IOutputDataHandler outputDataHandler;
    PipelineObjectType type = PipelineObjectType.JSON;  // Default type is JSON
    private static int numberOfInteractions = 1000;


    public SincExecutor(ISocialDataProvider provider, AbstractPipelineEngine pipeline, IOutputDataHandler outputDataHandler) {
        this.provider = provider;
        this.pipeline = pipeline;
        this.outputDataHandler = outputDataHandler;
        try {
            format = new SimpleDateFormat((String) PropertyLoader.getProperties().get("OutputDateFormat"));
            numberOfInteractions = Integer.parseInt(PropertyLoader.getProperties().get("numberOfInteractions").toString());
        } catch (IOException e) {
            SincLogger.logError(ERROR_MSG_UNABLE_TO_LOAD_DATE_FORMAT, e);
            format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        }
        format.setTimeZone(TimeZone.getTimeZone("GMT"));
    }

    public SincExecutor(ISocialDataProvider provider, AbstractPipelineEngine pipeline, IOutputDataHandler outputDataHandler, PipelineObjectType type) {
        this.provider = provider;
        this.pipeline = pipeline;
        this.outputDataHandler = outputDataHandler;
        this.type = type;
        try {
            format = new SimpleDateFormat((String) PropertyLoader.getProperties().get("OutputDateFormat"));
            numberOfInteractions = Integer.parseInt(PropertyLoader.getProperties().get("numberOfInteractions").toString());
        } catch (IOException e) {
            SincLogger.logError(ERROR_MSG_UNABLE_TO_LOAD_DATE_FORMAT, e);
            format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        }
        format.setTimeZone(TimeZone.getTimeZone("GMT"));
    }

    private void handleData() throws SincExecutorException {
        int count = 0;
        int prevCount = -1;
        try {
            while (prevCount != count) {

                try {
                    prevCount = count;
                    for (String socialData : provider.getNextInteractions(numberOfInteractions)) {
                        ++count;
                        if ((count % 10000) == 0) {
                            SincLogger.logInfo(count + MSG_RECORDS_WRITTEN_TO_GRAPH_FILE);
                        }
                        PipelineObject pipelineObject = new PipelineObject(type, socialData);
                        pipeline.runTroughPipeline(pipelineObject);
                        handleOutput(pipelineObject);
                    }

                } catch (SocialDataProviderException e) {
                    SincLogger.logError(ERROR_MSG_HANDLING_DATA_WITH_SOCIAL_DATA_PROVIDER, e);
                }
            }
        }
        //catch (SocialDataProviderException e) {
        //  SincLogger.logError(ERROR_MSG_HANDLING_DATA_WITH_SOCIAL_DATA_PROVIDER, e);
        //    }
        finally {
            SincLogger.logInfo(MSG_TOTAL_NUMBER_OF_PROCESSED_NODES + count);
            outputDataHandler.close();
        }
    }

    private void handleOutput(PipelineObject pipelineObject) {
        if (pipelineObject.getExecutionStatus() == PipelineExecutionAction.PROCESSED) {
            if (pipelineObject.getUser() != null) {
                String userID = pipelineObject.getUser().getUserID().toString();
                for (Object user : pipelineObject.getUsersMentioned()) {
                    try {
                        String mentionedUserID = ((User) user).getUserID().toString();
                        Date d = pipelineObject.getInteractionDate() == null ? new Date() : (Date) pipelineObject.getInteractionDate();
                        String dataToHandle = String.format("%s %s %s", userID, mentionedUserID, format.format(d));
                        outputDataHandler.handleData(dataToHandle);
                    } catch (IOException e) {
                        SincLogger.logError(ERROR_MSG_HANDLING_OUTPUT, e);
                    }
                }
            }

        }
    }

    public void run() {
        try {
            handleData();
        } catch (SincExecutorException e) {
            SincLogger.logError("Error executing.", e);
        } finally {
            setChanged();
            notifyObservers();
        }
    }
}
