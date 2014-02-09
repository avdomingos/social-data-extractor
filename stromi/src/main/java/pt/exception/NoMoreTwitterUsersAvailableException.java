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

package pt.exception;

public class NoMoreTwitterUsersAvailableException extends Throwable {
    public NoMoreTwitterUsersAvailableException(String message) {
        super(message);
    }
    public NoMoreTwitterUsersAvailableException(Throwable cause, String message) {
        super(message,cause);
    }
    public NoMoreTwitterUsersAvailableException(Throwable cause) {
        super(cause);
    }
}
