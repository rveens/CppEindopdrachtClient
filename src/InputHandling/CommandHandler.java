package InputHandling;

import ConnectionHandling.ConnectionHandler;
import ConnectionHandling.FileHandler;
import Main.ClientException;
import Main.Constants;
import Main.DisconnectException;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;

public class CommandHandler {
    private RequestHeaderGenerator rhg;
    private FileHandler fh;
    private String dirList;

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
            if (args.length != 2)
                throw new ClientException("DIR <path>");
            requestArgs.put("server_directory", args[1]);
        }
        if(args[0].equals("DEL")) {
            if (args.length != 2)
                throw new ClientException("DEL <path>");
            requestArgs.put("file_location", args[1]);
        }
        if(args[0].equals("REN")) {
            if (args.length != 3)
                throw new ClientException("REN <path> <path>");
            requestArgs.put("file_location", args[1]);
            requestArgs.put("new_file_location", args[2]);
        }
        if(args[0].equals("GET")) {
            if (args.length != 2)
                throw new ClientException("GET <path>");
            requestArgs.put("file_location", args[1]);
        }
        if(args[0].equals("PUT")) {
            if (args.length != 2)
                throw new ClientException("PUT <path>");
            requestArgs.put("file_location", args[1]);
            requestArgs.put("file_length", "" + fh.getFileSize(args[1]));
        }
        if(args[0].equals("SYNC")) {
            if (args.length != 3)
                throw new ClientException("SYNC <path> <path>");

            dirList = "D/F\tlocation\ttime";
            File dir;
            if(args[1].equals("/")) {
                dir = new File(Constants.CLIENT_PATH);
            } else {
                dir = new File(Constants.CLIENT_PATH + args[1]);
            }
            File[] files = dir.listFiles();
            for (File file : files) {
                dirList += "\n";
                if(file.isDirectory())
                    dirList +=  "D";
                else
                    dirList += "F";
                dirList += "\t";
                dirList += new File(Constants.CLIENT_PATH).toURI().relativize(file.toURI()).getPath();
                dirList += "\t";
                dirList += file.lastModified();
            }
            requestArgs.put("client_directory", args[1]);
            requestArgs.put("server_directory", args[2]);
            requestArgs.put("file_length", "" + dirList.length());
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
        if(args[0].equals("SYNC")) {
            try {
                ch.Write(dirList.getBytes());
            } catch (IOException e) {
                throw new DisconnectException("Socket closed unexpectedly.");
            }
        }

        // TODO sync file
        // stuur sync directory naar de server. Als file.

        return args[0];
    }
}
