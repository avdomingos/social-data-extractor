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

package stratex.exceptions;

import stratex.log.StratexLogger;

public class StratexException extends Exception {
    String exceptionMessage;
    Exception innerException;

    static String ctorParametersErr = "constructor parameters cannot be null.";

    public StratexException(String message) {
        if (message == null)
            throw new IllegalArgumentException(ctorParametersErr);
        exceptionMessage = message;
        // TODO: Review if this is correct
        StratexLogger.logError(message);
    }

    public StratexException(String message, Exception e) {
        if (message == null || e == null)
            throw new IllegalArgumentException(ctorParametersErr);
        exceptionMessage = message;
        innerException = e;
        // TODO: Review if this is correct
        StratexLogger.logError(message,e);
    }

    public Exception getInnerException()
    {
        return innerException;
    }

    @Override
    public String getMessage()
    {
        return exceptionMessage;
    }

}
