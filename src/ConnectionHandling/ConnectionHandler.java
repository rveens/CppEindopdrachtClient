package ConnectionHandling;

import Main.Constants;
import Main.DisconnectException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Created with IntelliJ IDEA.
 * User: rick
 * Date: 20-1-14
 * Time: 12:17
 * To change this template use File | Settings | File Templates.
 */
public class ConnectionHandler {
    private Socket clientSocket;
    private InputStream sis;
    private OutputStream sos;


    public ConnectionHandler(Socket clientSocket) throws DisconnectException {
        this.clientSocket = clientSocket;
        try {
            this.sos = clientSocket.getOutputStream();
            this.sis = clientSocket.getInputStream();
        } catch (IOException e) {
            throw new DisconnectException("Streams konden niet worden opgevraagd: " + e.getMessage());
        }
    }


    public byte[] Read(int bytesToRead) throws IOException, DisconnectException {
        byte[] buffer = new byte[bytesToRead];

        if (sis.read(buffer, 0, bytesToRead) == -1)
            return null;

        return buffer;
    }

    public String ReadLine() throws DisconnectException {
        byte[] singleChar = new byte[1];
        int size = 0;
        String result = "";

        try {
            while ( (size = sis.read(singleChar, 0, 1)) > 0) {
                if (size == 0) break;
                if (singleChar[0] == '\n') break;
                if (singleChar[0] != '\r') result += (char)singleChar[0];
                if (size >= Constants.MAX_READLINE_SIZE) break;
            }
        } catch (IOException e) {
            throw new DisconnectException("Fout bij het lezen van de stream: " + e.getStackTrace());
        }

        return result;
    }

    public void Write(byte[] buffer) throws IOException {
        sos.write(buffer, 0, buffer.length);
        sos.flush();
    }
}
