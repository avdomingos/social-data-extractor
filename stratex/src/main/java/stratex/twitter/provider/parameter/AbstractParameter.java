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

package stratex.twitter.provider.parameter;

public abstract class AbstractParameter implements IParameter{

    private final String PARAMETERNAME;

    public AbstractParameter(String parameterName){
        PARAMETERNAME = parameterName;
    }

    public String getParameterName() {
        return PARAMETERNAME;
    }

    @Override
    public String toString()
    {
        return getParameterName() + "=" + getParameterValue();
    }
}
