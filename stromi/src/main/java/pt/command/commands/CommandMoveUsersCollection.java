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

package pt.command.commands;

import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import pt.command.annotation.CommandAnnotation;
import pt.command.base.Command;
import pt.command.exception.ExitWithoutSuccessException;
import pt.db.MongoDBDataHandler;
import pt.exception.*;
import pt.intance.provider.MongoConnectionInstanceProvider;

import java.io.PrintStream;
import java.util.Map;

@CommandAnnotation(command = "move", description = "Starts the application fabric", usage = "move -from sourceCollection -to destinationCollection")
public class CommandMoveUsersCollection extends Command {

    @Override
    protected void commandAction(PrintStream ps, Map<String, String> parameters) throws ExitWithoutSuccessException {
        if(!parameters.containsKey("from") || !parameters.containsKey("to")){
            throw new ExitWithoutSuccessException(getUsage());
        }
        MongoDBDataHandler handler = null;
        try {
            handler = MongoConnectionInstanceProvider.getInstance();
            DBCollection from = handler.getCollection(parameters.get("from"));
            DBCollection to = handler.getCollection(parameters.get("to"));

            handler.beingTransaction();
            DBCursor cursor = from.find();
            while (cursor.hasNext()) {
                to.insert(cursor.next());
            }
            from.drop();
        } catch (NotInitializedException e) {
            throw new ExitWithoutSuccessException(e.getMessage());
        } catch (InstanceInitializeException e) {
            throw new ExitWithoutSuccessException(e.getMessage());
        } finally {
            if (handler != null)
                handler.commitTransaction();
        }


    }
}
