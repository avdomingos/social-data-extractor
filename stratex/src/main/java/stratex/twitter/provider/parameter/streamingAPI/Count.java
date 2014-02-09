/*
*	This file is part of STRATEX.
*
*    STRATEX is free software: you can redistribute it and/or modify
*    it under the terms of the GNU General Public License as published by
*    the Free Software Foundation, either version 3 of the License, or
*    (at your option) any later version.
*
*    STRATEX is distributed in the hope that it will be useful,
*    but WITHOUT ANY WARRANTY; without even the implied warranty of
*    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
*    GNU General Public License for more details.
*
*    You should have received a copy of the GNU General Public License
*    along with STRATEX.  If not, see <http://www.gnu.org/licenses/>.
*/

package stratex.twitter.provider.parameter.streamingAPI;

import stratex.twitter.provider.parameter.AbstractParameter;

/**
 * Firehose, Links, Birddog and Shadow clients interested in capturing all statuses should maintain a current estimate of the number of statuses received per second and note the time that the last status was received. Upon a reconnect, the client can then estimate the appropriate backlog to request. Note that the count parameter is not allowed elsewhere, including track, sample and on the default access role.
 */
public class Count extends AbstractParameter {

    private int count;

    public Count(int count) {
        super("count");
        this.count = count;
    }

    public String getParameterValue() {
        return String.valueOf(this.count);
    }
}