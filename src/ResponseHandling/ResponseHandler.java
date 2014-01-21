package ResponseHandling;

import ConnectionHandling.ConnectionHandler;
import ConnectionHandling.FileHandler;
import Main.ClientException;
import Main.Constants;
import Main.DisconnectException;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Scanner;

/**
 * Created by Reviara on 15-1-14.
 */
public class ResponseHandler {
    private ResponseParser rp;
    private FileHandler fh;

    public ResponseHandler(ConnectionHandler ch, FileHandler fileHandler) {
        this.fh = fileHandler;
        this.rp = new ResponseParser(ch);
    }

    public void Handle(String command) throws DisconnectException, ClientException {
        rp.ReadResponse();
        if (rp.GetInErrorState())
            throw new ClientException("Received invalid response from server.");

        HashMap<String, String> response = rp.GetAttributes();

        if(command.equals("INFO")) {
            System.out.println(response.get("info_mesg"));
        }
        if(command.equals("GET")) {
            fh.saveFile(response.get("file_location"), Integer.parseInt(response.get("file_length")));
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

            HashMap<String, tempClientParseShittyJavaHasNoStruct>
                    tempServerFiles = new HashMap<String, tempClientParseShittyJavaHasNoStruct>();

            // voor elk bestand op de server...
            while (s.hasNext()) {
                String currentLine = s.nextLine();
                String[] bestandAttributen = currentLine.split("\t");

                // sla wat dingen op en stop ze in de map.
                tempClientParseShittyJavaHasNoStruct item = new tempClientParseShittyJavaHasNoStruct();

                if (bestandAttributen[0] == "F")
                    item.isDirectory = false;
                else if (bestandAttributen[0] == "D")
                    item.isDirectory = true;

                item.unixTimeStamp = Long.parseLong(bestandAttributen[2]);
                item.fileName = bestandAttributen[1];

                tempServerFiles.put(bestandAttributen[1], item);
            }

            // voor elk bestand op de client, test of deze op de server staat of nieuwer is, zo ja, stuur zo over met PUT.
            iterateClientIfNewSendToServer(new File(Constants.CLIENT_PATH), tempServerFiles);
        }
    }

    public void iterateClientIfNewSendToServer(File currentFolder,
                                               HashMap<String, tempClientParseShittyJavaHasNoStruct> tempServerFiles)
    {
        if (currentFolder == null)
            return;

        for (File file : currentFolder.listFiles()) {
            if (file == null)
                continue;

            // display test
            //System.out.println((file.isDirectory() ? "D " : "F ") + file.getName() + " " + file.lastModified());

            if (file.isDirectory()) {
                iterateClientIfNewSendToServer(file, tempServerFiles);
            } else if (file.isFile()) {
            }
        }
    }

    private class tempClientParseShittyJavaHasNoStruct {
        public boolean isDirectory = false;
        public String fileName = "";
        public long unixTimeStamp = -1;
    }
}