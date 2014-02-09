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

package pt.sinc.pipeline.engine;

import pt.sinc.pipeline.engine.exception.PipelineObjectException;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Object to run trough the pipeline and modify with all the defined actions.
 * Since this pipeline is used to analyse data interactions between users and users and topics from a social network we'll start with a simple structure
 */
public class PipelineObject<T> {

     // Control variables
    private PipelineExecutionAction executionStatus;
    private PipelineObjectType pipelineObjectType;
    private String cancellationReason;
    private Date interactionDate;
    private long tweetID;

    // Data variables
    private String dataToAnalyze;
    private User<T> user;
    private List<User> mentionedUsers;

    public PipelineObject(PipelineObjectType type, String data)
    {
        this.pipelineObjectType = type;
        this.dataToAnalyze      = data;
        mentionedUsers          = new ArrayList<User>();

        // Default value
        executionStatus              = PipelineExecutionAction.PENDING;
    }

    public PipelineObjectType getPipelineObjectType() {
        return pipelineObjectType;
    }

    public String getDataToAnalyze() {
        return dataToAnalyze;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) throws PipelineObjectException {
        if (user != null)
            this.user = user;
        else
         throw new PipelineObjectException("Tweet author already defined.");
    }

    public List<User> getUsersMentioned() {
        return mentionedUsers;
    }

    public void addMentionedUser(User user)
    {
        this.mentionedUsers.add(user);
    }

    public PipelineExecutionAction getExecutionStatus() {
        return executionStatus;
    }

    public void setExecutionStatus(PipelineExecutionAction executionStatus) {
        this.executionStatus = executionStatus;
    }

    public void removeFromPipeline(String removalReason) {
        this.executionStatus = PipelineExecutionAction.CANCEL;
        this.cancellationReason = removalReason;
    }

    public Object getInteractionDate() {
        return interactionDate;
    }

    public long getTweetID() {
        return tweetID;
    }

    public void setInteractionDate(Date interactionDate) {
        this.interactionDate = interactionDate;
    }

    public void setTweetID(long tweetID) {
        this.tweetID = tweetID;
    }
}
