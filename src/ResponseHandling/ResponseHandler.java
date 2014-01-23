package ResponseHandling;

import ConnectionHandling.ConnectionHandler;
import ConnectionHandling.FileHandler;
import InputHandling.CommandHandler;
import Main.ClientException;
import Main.Constants;
import Main.DisconnectException;

import java.io.File;
import java.util.HashMap;
import java.util.Scanner;

/**
 * Created by Reviara on 15-1-14.
 */
public class ResponseHandler {
    private ResponseParser rp;
    private FileHandler fh;
    private ConnectionHandler ch; // voor sync

    public ResponseHandler(ConnectionHandler ch, FileHandler fileHandler) {
        this.fh = fileHandler;
        this.rp = new ResponseParser(ch);
        this.ch = ch;
    }

    public void Handle(String command) throws DisconnectException, ClientException {
        // read the response first
        rp.ReadResponse();
        HashMap<String, String> response = rp.GetAttributes();
        if (rp.GetReceivedErrorResponse())
            throw new ClientException("Received ERROR from server: " + response.get("error_mesg"));


        // action after a server response (which might be reading more bytes):
        if(command.equals("INFO")) {
            System.out.println(response.get("info_mesg"));
        }
        if(command.equals("GET")) {
            fh.saveFile(response.get("file_location"), Integer.parseInt(response.get("file_length")), Integer.parseInt(response.get("modified_time")));
        }
        if(command.equals("DIR")) {
            String dirList = fh.readToOutput(Integer.parseInt(response.get("file_length")));
            System.out.println(dirList);
        }
        // TODO SYNC afhandelen.
        /* Huidige staat van de server lezen:
        * Hiermee:
        *   Client bestanden vergelijken met de server bestanden
        *       - Nieuwe bestanden sturen
        *       - Nieuwere bestanden sturen
        *   Gebruik PUT
        */
        if (command.equals("SYNC")) {
            String dirList = fh.readToOutput(Integer.parseInt(response.get("file_length")));

            Scanner s = new Scanner(dirList);
            s.nextLine(); // skip de eerste regel bij DIR.

            HashMap<String, ServerFileDataItem>
                    tempServerFiles = new HashMap<String, ServerFileDataItem>();

            // voor elk bestand op de server...
            while (s.hasNext()) {
                String currentLine = s.nextLine();
                String[] bestandAttributen = currentLine.split("\t");

                // sla wat dingen op en stop ze in de map.
                ServerFileDataItem item = new ServerFileDataItem();

                if (bestandAttributen[0] == "F")
                    item.isDirectory = false;
                else if (bestandAttributen[0] == "D")
                    item.isDirectory = true;

                item.unixTimeStamp = Long.parseLong(bestandAttributen[2]);
                item.fileName = bestandAttributen[1];

                tempServerFiles.put(bestandAttributen[1], item);
            }

            // voor elk bestand op de client, test of deze op de server staat of nieuwer is, zo ja, stuur zo over met PUT.
            iterateClientIfNewSendToServer(response.get("client_folder"), response.get("server_folder"), tempServerFiles);
        }
    }

    public void iterateClientIfNewSendToServer(String clientFolder, String serverFolder,
                                                HashMap<String, ServerFileDataItem> tempServerFiles)
                                                throws DisconnectException, ClientException
    {
        File currentFolder = new File(combine(Constants.CLIENT_PATH, clientFolder));

        if (currentFolder == null)
            return;

        for (File file : currentFolder.listFiles()) {
            if (file == null)
                continue;

            // display test
            //System.out.println((file.isDirectory() ? "D " : "F ") + file.getName() + " " + file.lastModified());

            if (file.isDirectory()) {
                iterateClientIfNewSendToServer(clientFolder, serverFolder, tempServerFiles);
            } else if (file.isFile()) {
                String relative = new File(combine(Constants.CLIENT_PATH, clientFolder)).toURI().relativize(file.toURI()).getPath();
                ServerFileDataItem item = null;

                String finalClientPath, finalServerPath;
                if(clientFolder.equals("/"))
                    finalClientPath = relative;
                else
                    finalClientPath = clientFolder + relative;

                if(serverFolder.equals("/"))
                    finalServerPath = relative;
                else
                    finalServerPath = serverFolder + relative;
                if ( (item = tempServerFiles.get(relative)) != null) {
                    // bestand bestaat zowel op de server als op de client, is de client versie nieuwer?

                    long lastModified = file.lastModified();
                    lastModified /= 1000;
                    if ( lastModified != item.unixTimeStamp) {
                        CommandHandler tcm = new CommandHandler(fh);
                        String usercommand = tcm.HandleCommand("PUT " + finalClientPath + " " + finalServerPath, ch);
                        this.Handle(usercommand);
                    }
                } else {
                    // bestand bestaat niet op de server, altijd sturen
                    CommandHandler tcm = new CommandHandler(fh);
                    String usercommand = tcm.HandleCommand("PUT " + finalClientPath + " " + finalServerPath, ch);
                    this.Handle(usercommand);
                }
            }
        }
    }
    public static String combine (String path1, String path2)
    {
        File file1 = new File(path1);
        File file2 = new File(file1, path2);
        return file2.getPath();
    }

    private class ServerFileDataItem {
        public boolean isDirectory = false;
        public String fileName = "";
        public long unixTimeStamp = -1;
    }
}
