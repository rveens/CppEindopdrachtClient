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
        OutputStreamWriter sos = null;

        Socket s;
        try {
            s = new Socket(HOST, TCP_PORT);
            sos = new OutputStreamWriter(s.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

        String name = null;
        try {
            while((name = ibr.readLine()) != null) {
                sos.write(name + '\n');
                sos.flush();
            }
            sos.close();
        } catch (IOException ioe) {
            System.err.println("IO error with stdin");
        }
    }
}
