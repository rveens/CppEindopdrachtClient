package InputHandling;

import ConnectionHandling.ConnectionHandler;
import ConnectionHandling.FileHandler;
import Main.ClientException;
import Main.DisconnectException;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;

public class CommandHandler {
    private RequestHeaderGenerator rhg;
    private FileHandler fh;

    public CommandHandler(FileHandler fh) throws DisconnectException {
        rhg = new RequestHeaderGenerator();
        this.fh = fh;
    }

    public String HandleCommand(String inputLine, ConnectionHandler ch) throws DisconnectException, ClientException {
        HashMap<String, String> requestArgs = new HashMap<String, String>();

        String[] args = inputLine.split(" ");

        requestArgs.put("command", args[0]);

        // Maak en stuur een header, op basis van het usercommand. Dit command wordt ook gereturned.
        if(args[0].equals("DIR")) {
            requestArgs.put("directory", args[1]);
        }
        if(args[0].equals("DEL")) {
            requestArgs.put("file_location", args[1]);
        }
        if(args[0].equals("REN")) {
            requestArgs.put("file_location", args[1]);
            requestArgs.put("new_file_location", args[2]);
        }
        if(args[0].equals("GET")) {
            requestArgs.put("file_location", args[1]);
        }
        if(args[0].equals("PUT")) {
            requestArgs.put("file_location", args[1]);
            requestArgs.put("file_length", "" + fh.getFileSize(args[1]));
        }
        if(args[0].equals("SYNC")) {
            //TODO: Handle sync command
            // Stuur sync header naar de server
        }

        try {
            ch.Write(rhg.GenerateRequestHeader(requestArgs).getBytes());
        } catch (IOException e) {
            throw new DisconnectException("Socket closed unexpectedly.");
        }


        // Moet de client een bestand sturen?
        if(args[0].equals("PUT")) {
            fh.sendFile(args[1]);
        }
        if(args[0].equals("QUIT")) {
            throw new DisconnectException("Disconnected from server.");
        }

        // TODO sync file
        // stuur sync directory naar de server. Als file.

        return args[0];
    }
}
