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
    private BufferedReader uir;         // Voor het lezen van gebruiker input

    public SuperSecretClient() {
        int[] versionNr = { 1, 0 };
        rhg = new RequestHeaderGenerator("SUPERSECRETPROTOL", versionNr);

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
            }
        }
    }

    private void Handle() {
        while(true) {
            try {
                String line = getInput();
                String[] words = line.split(" ");

                HashMap<String, String> attributeMap = new HashMap<String, String>();

                File file = null;

                // voeg attributes toe over het bestand
                if (words.length > 1) { // Hebben we een bestand als tweede argument?
                    file = new File(words[1]);
                    attributeMap.put("file_location", file.getPath());
                    attributeMap.put("length", Long.toString(file.length()));
                } else
                    attributeMap.put("length", Integer.toString(0));

                // genereer de header.
                String header = rhg.GenerateRequestHeader(words[0], attributeMap);

                // stuur de header
                sos.write(header.getBytes());

                if(words[0].equals("QUIT"))
                    break;

                // stuur bestand (als die er is)
                if (file != null) {
                    FileInputStream fis = new FileInputStream(file); // voor het lezen uit het bestand
                    byte[] buffer = new byte[Constants.BUFFER_SIZE]; // buffer

                    while (fis.read(buffer) != -1)
                        sos.write(buffer);
                }
                // flush aanroepen?

                // TODO lees response van server
                ResponseParser rs = new ResponseParser();
                rs.ReadResponse(sis);
                if (rs.GetInErrorState())
                    printMessage("ERROR: Received invallid response from server."); // response is niet goed.

                HashMap<String, String> response = rs.GetAttributes();
                printMessage(response.get("info_mesg"));

            } catch (IOException ioe) {
                printMessage("ERROR: Input error");
            }
        }
    }

    private String getInput() throws IOException {
        if (uir != null)
            System.out.print('>');

        return uir.readLine();
    }

    private void printMessage(String mes) {
        System.out.println(mes);
    }
}
