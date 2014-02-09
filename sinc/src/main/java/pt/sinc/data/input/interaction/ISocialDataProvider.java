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

package pt.sinc.data.input.interaction;

import pt.sinc.data.input.interaction.exception.SocialDataProviderException;

import java.util.List;

/**
 *
 */
public interface ISocialDataProvider {
    /**
     * Gets the next interactionNode
     * @return
     */
    abstract String getNextInteraction() throws SocialDataProviderException;
    abstract List<String> getNextInteractions(int numberOfInteractions) throws SocialDataProviderException;
    abstract boolean hasMoreInteractions() throws SocialDataProviderException;
    abstract void close();
}
