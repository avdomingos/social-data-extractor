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

package pt.sinc.pipeline.engine.basic;

import pt.sinc.logger.SincLogger;
import pt.sinc.pipeline.engine.AbstractPipelineEngine;
import pt.sinc.pipeline.engine.PipelineExecutionAction;
import pt.sinc.pipeline.engine.PipelineObject;
import pt.sinc.pipeline.engine.PipelineStage;
import pt.sinc.pipeline.engine.action.AbstractPipelineAction;
import pt.sinc.pipeline.engine.annotation.PipelineAction;
import pt.sinc.pipeline.engine.basic.exception.PipelineException;

import java.util.ArrayList;
import java.util.List;

/**
 * A basic pipeline engine;
 */
public class BasicPipelineEngine<T> extends AbstractPipelineEngine<T> {
    /**
     * TODO: Implement a method for cancellation... e.g. I had a tweet that had no mentions to other users. This should blocked at the filter part...
     *
     * @param actions
     */
    public BasicPipelineEngine(List<AbstractPipelineAction> actions) {
        if (actions.isEmpty()) {
            throw new IllegalArgumentException("Pipeline needs actions to execute.");
        }

        this.inputTransformationActions = new ArrayList<AbstractPipelineAction>();
        this.filterActions = new ArrayList<AbstractPipelineAction>();
        this.reconstructActions = new ArrayList<AbstractPipelineAction>();
        this.outputTransformationActions = new ArrayList<AbstractPipelineAction>();

        for (AbstractPipelineAction action : actions) {
            PipelineAction ann = action.getClass().getAnnotation(PipelineAction.class);
            PipelineStage stage = ann.value();

            addActionToCorrespondentContainer(action, stage);
        }
    }

    private void addActionToCorrespondentContainer(AbstractPipelineAction action, PipelineStage stage) {
        switch (stage) {
            case INPUT_TRANSFORM: {
                this.inputTransformationActions.add(action);
                break;
            }
            case FILTER: {
                this.filterActions.add(action);
                break;
            }
            case RECONSTRUCT: {
                this.reconstructActions.add(action);
                break;
            }
            case OUTPUT_TRANSFORM: {
                this.outputTransformationActions.add(action);
                break;
            }
        }
    }

    @Override
    public PipelineObject<T> runTroughPipeline(PipelineObject<T> item) {

        try {
            // Set execution for CONTINUE status
            item.setExecutionStatus(PipelineExecutionAction.CONTINUE);

            runTroughPipelineStage(item, inputTransformationActions);
            if (item.getExecutionStatus() == PipelineExecutionAction.CONTINUE)
                runTroughPipelineStage(item, filterActions);
            if (item.getExecutionStatus() == PipelineExecutionAction.CONTINUE)
                runTroughPipelineStage(item, reconstructActions);
            if (item.getExecutionStatus() == PipelineExecutionAction.CONTINUE)
                runTroughPipelineStage(item, outputTransformationActions);

            // Set execution for PROCESSED signaling whoever needs to know that this processing is done
            // No more stages. If it was in a cancel state, confirm cancellation. Else, mark as processed.
            if (item.getExecutionStatus() == PipelineExecutionAction.CANCEL) {
                item.setExecutionStatus(PipelineExecutionAction.CANCELLED);
            } else if (item.getExecutionStatus() == PipelineExecutionAction.CONTINUE) {
                item.setExecutionStatus(PipelineExecutionAction.PROCESSED);
            }
        } catch (PipelineException e) {
            // TODO: Treat exception
            SincLogger.logWarn(String.format("Pipeline item error. Item was cancelled. Item data: %s", item.toString()));
            return item;
        }
        return item;
    }

    private void runTroughPipelineStage(PipelineObject<T> item, List<AbstractPipelineAction> actions) throws PipelineException {
        for (Object inputAction : actions) {
            if (item.getExecutionStatus() == PipelineExecutionAction.CONTINUE) {
                ((AbstractPipelineAction) inputAction).executeAction(item);
            } else if (item.getExecutionStatus() == PipelineExecutionAction.CANCEL) {
                break;
            }
        }
    }
}
