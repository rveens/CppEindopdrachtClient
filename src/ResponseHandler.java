import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

/**
 * Created by Reviara on 15-1-14.
 */
public class ResponseHandler {
    private ResponseParser rp;
    private FileHandler fh;

    public ResponseHandler() {
        fh = new FileHandler();
    }

    public void Handle(InputStream sis, String command) throws DisconnectException, ClientException {
        try {
            rp = new ResponseParser();

            rp.ReadResponse(sis);
            if (rp.GetInErrorState())
                throw new ClientException("Received invalid response from server.");

            HashMap<String, String> response = rp.GetAttributes();

            if(command.equals("INFO")) {
                System.out.println(response.get("info_mesg"));
            }
            if(command.equals("GET")) {
                fh.saveFile(sis, response.get("file_location"), Integer.parseInt(response.get("file_length")));
            }
            if(command.equals("DIR")) {
                String dirList = fh.readToOutput(sis, Integer.parseInt(response.get("file_length")));
                System.out.println(dirList);
            }

        } catch (IOException e) {
            throw new DisconnectException("Connection closed unexpectedly.");
        }
    }
}
