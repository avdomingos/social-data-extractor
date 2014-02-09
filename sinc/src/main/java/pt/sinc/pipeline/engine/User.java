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

/**
 * Declared as generic because not everyone uses int and long for IDs.
 */
public class User<T> {
    private String username;
    private T userID;

    public User(String username, T userID)
    {
        this.username = username;
        this.userID = userID;
    }

    public String getUsername() {
        return username;
    }

    public T getUserID() {
        return userID;
    }
}
