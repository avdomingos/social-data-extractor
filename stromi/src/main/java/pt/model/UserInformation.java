/*
*	This file is part of STRoMI.
*
*    STRoMI is free software: you can redistribute it and/or modify
*    it under the terms of the GNU General Public License as published by
*    the Free Software Foundation, either version 3 of the License, or
*    (at your option) any later version.
*
*    STRoMI is distributed in the hope that it will be useful,
*    but WITHOUT ANY WARRANTY; without even the implied warranty of
*    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
*    GNU General Public License for more details.
*
*    You should have received a copy of the GNU General Public License
*    along with STRoMI.  If not, see <http://www.gnu.org/licenses/>.
*/

package pt.model;

/**
 * Created with IntelliJ IDEA.
 * User: Crack
 * Date: 29-07-2012
 * Time: 18:16
 * To change this template use File | Settings | File Templates.
 */
public class UserInformation {

    public enum CountType {
        POST, MENTION
    }

    private long userID;
    private int postsCount;
    private int mentionsCount;
    private int followCount;

    public UserInformation(long userID) {
        this.userID = userID;
        this.postsCount = 0;
        this.mentionsCount = 0;
        this.followCount = 0;
    }

    public UserInformation(long userID, int count, CountType countType) {
        this.userID = userID;
        if (countType == CountType.POST) {
            this.postsCount = count;
            this.mentionsCount = 0;
        } else {
            this.postsCount = 0;
            this.mentionsCount = count;
        }
    }

    public UserInformation(long userID, int postsCount, int mentionsCount, int followCount) {
        this.userID = userID;
        this.postsCount = postsCount;
        this.mentionsCount = mentionsCount;
        this.followCount = followCount;
    }

    public void incrementPostCount() {
        ++postsCount;
    }

    public void incrementMentionCount() {
        ++mentionsCount;
    }

    public void incrementFollowCount() {
        ++followCount;
    }

    public long getUserID() {
        return this.userID;
    }

    public int getPostsCount() {
        return this.postsCount;
    }

    public int getMentionsCount() {
        return this.mentionsCount;
    }

    public int getFollowCount() {
        return this.followCount;
    }
}
