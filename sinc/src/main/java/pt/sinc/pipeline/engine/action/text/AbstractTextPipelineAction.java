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

package pt.sinc.pipeline.engine.action.text;

import pt.sinc.PropertyLoader;
import pt.sinc.logger.SincLogger;
import pt.sinc.pipeline.engine.PipelineObject;
import pt.sinc.pipeline.engine.PipelineObjectType;
import pt.sinc.pipeline.engine.action.AbstractPipelineAction;
import pt.sinc.pipeline.engine.exception.PipelineActionException;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class AbstractTextPipelineAction extends AbstractPipelineAction {

    private static final String REGEX4USERS_KEY = "Regex4Users";
    protected static String regex4Users = null;

    protected static Pattern ptn = null;
    protected Matcher matcher = null;

    private static final String DEFAULT_REGEX = "(@\\[(.*?)\\])|(@\\{(.*?)\\})|(@[A-Z0-9-].*)";
    protected static final String specialCharsRegex = "[^\\w\\*]";
    static {
        try {
            regex4Users = PropertyLoader.getProperties(AbstractTextPipelineAction.class.getSimpleName()).getProperty(REGEX4USERS_KEY);
        } catch (IOException e) {
            SincLogger.logError("Problem loading a property from properties file. Class: " + AbstractTextPipelineAction.class.getName(), e);
            if (regex4Users != null && regex4Users.isEmpty())
            {
                regex4Users = DEFAULT_REGEX;
            }
        }
        ptn = Pattern.compile(regex4Users);
    }



    public void validatePipelineObjectType(PipelineObject item) throws PipelineActionException {
        if (item.getPipelineObjectType() != PipelineObjectType.TEXT) {
            throw new PipelineActionException(EXCEPTION_MESSAGE_TEXT_PIPELINE_OBJECTS);
        }
    }
}
