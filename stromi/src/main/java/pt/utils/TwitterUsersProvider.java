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

package pt.utils;

import pt.exception.NoMoreTwitterUsersAvailableException;
import pt.properties.PropertyFileLoader;
import pt.exception.InvalidPropertiesException;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class TwitterUsersProvider {

    private Hashtable<String, String> _usersInProcess;
    private Hashtable<String, String> _availableUsers;
    private static volatile TwitterUsersProvider _singleton;

    private static final Object syncObj = new Object();

    private TwitterUsersProvider() {
        _usersInProcess = new Hashtable<String, String>();
        _availableUsers = new Hashtable<String, String>();
    }

    public static TwitterUsersProvider getInstance() {
        synchronized (syncObj) {
            if (_singleton == null) {
                _singleton = new TwitterUsersProvider();
            }
            return _singleton;
        }
    }

    public synchronized boolean hasMoreUsers() {
        return _availableUsers.size() > 0;
    }

    public synchronized Map.Entry<String, String> acquireUserAndPassword() throws NoMoreTwitterUsersAvailableException {
        if (hasMoreUsers()) {
            Iterator<Map.Entry<String, String>> iter = _availableUsers.entrySet().iterator();
            Map.Entry<String, String> element = iter.next();
            Map.Entry<String, String> toReturn =  new AbstractMap.SimpleEntry<String, String>(element);
            _usersInProcess.put(element.getKey(), element.getValue());
            _availableUsers.remove(element.getKey());
            return toReturn;
        } else {
            throw new NoMoreTwitterUsersAvailableException("There no exists more available users. Please call hasMoreUsers method before acquire a new user");
        }
    }

    public synchronized void releaseUser(String username) {
        if (_usersInProcess.containsKey(username)) {
            _usersInProcess.remove(username);
        }
    }

    public synchronized void refreshList() throws IOException, InvalidPropertiesException {
        BufferedReader br = null;
        try {
            String defaultPassword = PropertyFileLoader.getProperties().getProperty("defaultPassword");
            br = new BufferedReader(new FileReader(PropertyFileLoader.getProperties().getProperty("usersFile")));
            String userLine;
            while ((userLine = br.readLine()) != null) {
                userLine = userLine.trim();
                if (!_usersInProcess.contains(userLine) && !_availableUsers.contains(userLine)) {
                    _availableUsers.put(userLine, defaultPassword);
                }
            }
        } finally {
            if (br != null) {
                br.close();
            }
        }
    }


}
