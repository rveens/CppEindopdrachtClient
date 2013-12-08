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

        Socket s;
        try {
            s = new Socket(HOST, TCP_PORT);
            sosw = new OutputStreamWriter(s.getOutputStream());
            sibr = new BufferedReader(new InputStreamReader(s.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        String name = null;
        try {
            // read welcome message
            System.out.println(sibr.readLine());

            // read user input
            while((name = ibr.readLine()) != null) {
                sosw.write(name + '\n');
                sosw.flush();
            }
            sosw.close();
        } catch (IOException ioe) {
            System.out.println("IO error with stdin");
        }
    }
}
