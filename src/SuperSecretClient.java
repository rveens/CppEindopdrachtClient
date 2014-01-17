import java.io.*;
import java.net.Socket;
import java.util.HashMap;

/**
 * Created by Rick Veens on 12/9/13.
 */
public class SuperSecretClient {

    private Socket s;                   // socket
    private OutputStream sos;           // voor output naar socket
    private InputStream sis;            // voor input van socket

    private RequestHeaderGenerator rhg; // Maakt een header voor ons
    private CommandHandler ch;
    private ResponseHandler rh;

    private BufferedReader uir;         // Voor het lezen van gebruiker input

    public SuperSecretClient() {
        ch = new CommandHandler();
        rh = new ResponseHandler();

        uir = new BufferedReader(new InputStreamReader(System.in));
    }

    public void StartClient() {

        while (true) {
            try {
                printMessage("Server adres: ");
                String host = getInput();
                printMessage("Server port: ");
                String temp = getInput();
                int port = Integer.parseInt(temp);

                //Create socket
                s = new Socket(host, port);
                sos = s.getOutputStream();
                sis = s.getInputStream();

                //Handle this socket
                Handle();

                //Print close message and start all over
                printMessage("Connection closed.");
            } catch (IOException e) {
                printMessage("ERROR: Server could not be found!");
            } catch (NumberFormatException e) {
                printMessage("ERROR: Server port must be a number!");
            } catch (ClientException e) {
                printMessage(e.what());
            }
        }
    }

    private void Handle() {
        while(true) {
            try {
                String inputLine = getInput();

                String command = ch.HandleCommand(inputLine, sos);
                rh.Handle(sis, command);

            } catch (DisconnectException e) {
                printMessage(e.what());
                break;
            } catch (ClientException e) {
                printMessage(e.what());
            }
        }
    }

    private String getInput() throws ClientException {
        if (uir != null)
            System.out.print('>');

        try {
            return uir.readLine();
        } catch (IOException e) {
            throw new ClientException("Error reading userinput.");
        }
    }

    private void printMessage(String mes) {
        System.out.println(mes);
    }
}
