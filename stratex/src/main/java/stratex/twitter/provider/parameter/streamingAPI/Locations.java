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

import java.util.List;

public class Locations extends AbstractParameter {


    private List<LocationArea> locations;


    //TODO: Add check rules
    public Locations(List<LocationArea> locations) {
        super("locations");
        this.locations = locations;
    }

    public String getParameterValue() {
        StringBuilder sb = new StringBuilder();
        for (LocationArea area : locations) {
            if (sb.length() > 0)
                sb.append(String.format(",%s",area));
            else
                sb.append(area);
        }
        return sb.toString();
    }

    protected class LocationArea {

        private float _lat1;
        private float _lon1;
        private float _lat2;
        private float _lon2;

        public LocationArea(float lat1, float lon1, float lat2, float lon2) {
            _lat1 = lat1;
            _lon1 = lon1;
            _lat2 = lat2;
            _lon2 = lon2;
        }

        public String toString() {
            return String.format("%f,%f,%f,%f", _lat1, _lon1, _lat2, _lon2);
        }
    }
}
