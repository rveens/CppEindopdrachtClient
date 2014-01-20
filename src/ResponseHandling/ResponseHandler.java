package ResponseHandling;

import ConnectionHandling.ConnectionHandler;
import ConnectionHandling.FileHandler;
import Main.ClientException;
import Main.DisconnectException;

import java.io.IOException;
import java.util.HashMap;

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
    }
}
