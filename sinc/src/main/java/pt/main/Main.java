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

package pt.main;

import pt.sinc.TwitterRunner;

import java.io.IOException;

public class Main {

    public static void main(String[] args) {
        int nRunningThreads = 1;
        if (args.length > 0)
        {
            nRunningThreads = Integer.parseInt(args[0]);
        }
        TwitterRunner r = new TwitterRunner();
        try {
            r.runSinc(nRunningThreads);
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        System.exit(0);
    }
}
