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

public class Track extends AbstractParameter {

    private String[] topicsToTrack;

    public Track(String[] trackStr) {
        super("track");
        topicsToTrack = trackStr.clone();
    }

    public String getParameterValue() {
        StringBuilder sb = new StringBuilder();
        for (String topic : topicsToTrack) {
            if (sb.length() > 0)
                sb.append(String.format(",%s", topic));
            else
                sb.append(topic);
        }
        return sb.toString();
    }
}
