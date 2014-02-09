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

package pt.sinc.data.input.interaction.text;

import pt.sinc.data.input.interaction.ISocialDataProvider;
import pt.sinc.data.input.interaction.exception.SocialDataProviderException;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TextDataProvider implements ISocialDataProvider {

    BufferedReader reader;

    public TextDataProvider(String filepath) throws FileNotFoundException {
        reader = new BufferedReader(new FileReader(filepath));
    }

    public String getNextInteraction() throws SocialDataProviderException {
        try {
            return reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<String> getNextInteractions(int numberOfInteractions) throws SocialDataProviderException {
        ArrayList<String> lst = new ArrayList<String>();
        String line;
        try {
            while (lst.size() <= numberOfInteractions && (line = reader.readLine()) != null)
            {
                lst.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lst;
    }

    public boolean hasMoreInteractions() throws SocialDataProviderException {
        try {
            return reader.ready();
        } catch (IOException e) {
            return false;
        }
    }

    public void close() {
        try {
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
