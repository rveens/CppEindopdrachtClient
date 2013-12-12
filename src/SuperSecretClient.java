import java.io.*;
import java.net.Socket;
import java.util.HashMap;

/**
 * Created by Rick Veens on 12/9/13.
 */
public class SuperSecretClient {

    private Socket s;                   // socket
    private OutputStream sos;           // voor output naar socket
    private BufferedReader sibr;        // voor input van socket
    private RequestHeaderGenerator rhg; // Maakt een header voor ons

    public SuperSecretClient() {
        int[] versionNr = { 1, 0 };
        rhg = new RequestHeaderGenerator("SUPERSECRETPROTOL", versionNr);
    }

    public void StartClient() {

        /* Create a socket */
        try {
            s = new Socket(Constants.HOST, Constants.TCP_PORT);
            sos = s.getOutputStream();
            sibr = new BufferedReader(new InputStreamReader(s.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        ReadUserInput();

        System.out.println("Client closed");
    }

    private void ReadUserInput() {
        BufferedReader ibr;     // voor input van stdin
        ibr = new BufferedReader(new InputStreamReader(System.in));

        String line = null;
        try {
            // read commands from input
            while ((line = getCommand(ibr)) != null) {
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

                // stuur bestand (als die er is)
                if (file != null) {
                    FileInputStream fis = new FileInputStream(file); // voor het lezen uit het bestand
                    byte[] buffer = new byte[1337]; // buffer

                    while (fis.read(buffer) != -1)
                        sos.write(buffer);
                }

                // flush aanroepen?
                // TODO lees response van server

                sos.close();
            }
        } catch (IOException ioe) {
            System.out.println("IO error with stdin");
        }
    }

    private String getCommand(BufferedReader inputReader) throws IOException {
        if (inputReader != null)
            System.out.print('>');

        return inputReader.readLine();
    }
}
