import java.io.*;
import java.net.Socket;

/**
 * Created by Rick Veens on 12/8/13.
 */
public class Main {

    static final int TCP_PORT = 8080;
    static final String HOST = "localhost";

    public static void main(String[] args) {
        // voor input van stdin
        BufferedReader ibr = new BufferedReader(new InputStreamReader(System.in));

        // voor output naar socket
        OutputStreamWriter sosw = null;

        // voor input van socket
        BufferedReader sibr = null;


        Socket s = null;
        try {
            s = new Socket(HOST, TCP_PORT);
            sosw = new OutputStreamWriter(s.getOutputStream());
            sibr = new BufferedReader(new InputStreamReader(s.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }


        String line = null;
        try {
            // read welcome message
            System.out.println(sibr.readLine());

            // read user input
            for (System.out.print('>'); (line = ibr.readLine()) != null && !line.equals("null"); System.out.print('>'))
            {
                // stuur bericht naar server
                sosw.write(line + '\n');
                sosw.flush();

                // lees response van server
                System.out.println(sibr.readLine());
            }
            sosw.close();
        } catch (IOException ioe) {
            System.out.println("IO error with stdin");
        }

        System.out.println("Client closed");
    }
}
