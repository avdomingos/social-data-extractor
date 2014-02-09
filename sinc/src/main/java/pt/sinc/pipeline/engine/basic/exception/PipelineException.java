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

package pt.sinc.pipeline.engine.basic.exception;

public class PipelineException extends Throwable{
    private String    message;
    private Exception innerException;
    private String    methodName;

    public PipelineException(String message, Exception innerException, String methodName)
    {
        this.innerException = innerException;
        this.methodName     = methodName;
        this.message        = message;
    }

    public PipelineException(String message, Exception innerException)
    {
        this.innerException = innerException;
        this.message        = message;
    }

    public PipelineException(String message)
    {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public Exception getInnerException() {
        return innerException;
    }

    public String getMethodName() {
        return methodName;
    }
}
