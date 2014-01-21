package ResponseHandling;

import ConnectionHandling.ConnectionHandler;
import ConnectionHandling.FileHandler;
import Main.ClientException;
import Main.DisconnectException;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Rick Veens on 12/12/13.
 */
public class ResponseParser {
    private String statusCode;
    private HashMap<String, String> attributes;
    private boolean receivedErrorResponse = false;

    private ConnectionHandler ch;

    private final String[] possibleAttributes = {
        "error_mesg",
        "info_mesg",
        "file_length",
        "file_location",
        "client_directory",
        "server_directory",
    };

    public ResponseParser(ConnectionHandler ch) {
        this.ch = ch;
        attributes = new HashMap<String, String>();
    }


    public void ReadResponse() throws DisconnectException, ClientException {
        // read first line
        if (!checkFirstLine(ch.ReadLine()))
            throw new ClientException("First line of header is invalid. ");

        // read and put attribute-lines into an arrayList.
        ArrayList<String> tempList = new ArrayList<String>();
        String line = null;
        while ( (line = ch.ReadLine()) != null && (!line.equals("")) )
            tempList.add(line);

        // pass String-array to checkAttributes
        String[] lines = tempList.toArray(new String[tempList.size()]);
        checkAttributes(lines);
    }

    public String GetStatusCode() {
        return statusCode;
    }

    public HashMap<String, String> GetAttributes() {
        return attributes;
    }

    public boolean GetReceivedErrorResponse() {
        return this.receivedErrorResponse;
    }

    private boolean checkFirstLine(String firstLine) {
        /* check for nullpointer */
        if (firstLine == null)
            return false;

        String[] words = firstLine.split(" ");

        if (words.length == 0)
            return false;

        if (!words[0].equals("SUPERSECRETPROTOCOL-1.0"))
            return false;

        if (!words[1].equals("OK") && !words[1].equals("ERROR"))
            return false;

        if (words[1].equals("ERROR"))
            receivedErrorResponse = true;

        this.statusCode = words[1];

        return true;
    }

    private boolean checkAttributes(String[] lines) throws ClientException {
        for (String line : lines) {
            String[] words = line.split(": ");
            if (words.length == 0)
                continue;

            if (checkIfAttributeNameExists(words[0]))
                this.attributes.put(words[0], words[1]);
            else
                throw new ClientException("Unknown Attribute Name");
        }

        return !this.attributes.isEmpty();
    }

    private boolean checkIfAttributeNameExists(String givenAttribute) {
        boolean result = false;

        for (String attribute : possibleAttributes) {
            if (attribute.equals(givenAttribute)) {
                result = true;
                break;
            }
        }

        return result;
    }
}