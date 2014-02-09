package pt.sinc.data.input.interaction.text;

import pt.sinc.logger.SincLogger;
import pt.sinc.data.input.interaction.ISocialDataProvider;
import pt.sinc.data.input.interaction.exception.SocialDataProviderException;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class InputDataProvider implements ISocialDataProvider {

    BufferedReader reader;

    public InputDataProvider() throws FileNotFoundException {
        reader = new BufferedReader(new InputStreamReader(System.in));
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
