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
            // read user input
            while ((line = getCommand(ibr)) != null) {
                String[] words = line.split(" ");

                /* stuur bericht naar server */
                // maak een header
                HashMap<String, String> hm = new HashMap<String, String>();

                // voeg attributes toe over het bestand, als we een tweede argument hebben.
                if (words.length > 1) { // Hebben we een bestand als tweede argument?
                    File file = new File(words[1]);
                    hm.put("file_location", file.getPath());
                    hm.put("length", Long.toString(file.length()));
                } else
                    hm.put("length", Integer.toString(0));

                String header = rhg.GenerateRequestHeader(words[0], hm);

                // stuur de header + het bestand
                byte[] buffer = new byte[1337]; // buffer

                // TODO header maken en sturen + bestand
//                sos.write(line + '\n');
//                sos.flush();

                // lees response van server
                System.out.println(sibr.readLine());
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
