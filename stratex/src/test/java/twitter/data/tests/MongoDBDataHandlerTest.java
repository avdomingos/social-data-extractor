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

package twitter.data.tests;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import stratex.db.MongoDBDataManager;
import stratex.twitter.extractor.datahandlers.exception.DataHandlerException;
import stratex.twitter.extractor.datahandlers.mongodb.MongoDBDataHandler;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Random;

public class MongoDBDataHandlerTest {
    MongoDBDataHandler handler;
    MongoDBDataManager manager;

    static String testDatabase = "testDatabase";
    static String testCollection = "testCollection";

    public MongoDBDataHandlerTest() throws IOException {
        handler = new MongoDBDataHandler();
    }

    @Before
    public void setUp() throws Exception {
        manager = new MongoDBDataManager("localhost", testDatabase, testCollection);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test(expected = DataHandlerException.class)
    public void testHandleDataMustThrowExceptionIfStringIsNotJSON() throws DataHandlerException {
        handler.handleData("error, this isn't JSON!!");
    }


    @Test( expected = DataHandlerException.class)
    public void testHandleDataMustThrowExceptionIfStringIsNull() throws DataHandlerException, UnknownHostException {
        Random rnd = new Random();
        long id = rnd.nextLong();
        handler.handleData("");
    }

}
