import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;

public class CommandHandler {
    private RequestHeaderGenerator rhg;
    private FileHandler fh;

    public CommandHandler() {
        rhg = new RequestHeaderGenerator();
        fh = new FileHandler();
    }

    public String HandleCommand(String inputLine, OutputStream sos) throws DisconnectException, ClientException {
        HashMap<String, String> requestArgs = new HashMap<String, String>();

        String[] args = inputLine.split(" ");

        requestArgs.put("command", args[0]);

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
        }

        try {
            sos.write(rhg.GenerateRequestHeader(requestArgs).getBytes());
        } catch (IOException e) {
            throw new DisconnectException("Socket closed unexpectedly.");
        }

        if(args[0].equals("PUT")) {
            fh.sendFile(sos, args[1]);
        }
        if(args[0].equals("QUIT")) {
            throw new DisconnectException("Disconnected from server.");
        }

        return args[0];
    }
}
