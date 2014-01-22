package Main;

import java.io.File;

/**
 * Created by Rick Veens on 12/8/13.
 */
public class Main {

    public static void main(String[] args) {
        File clientDir = new File(Constants.CLIENT_PATH);

        // if the directory does not exist, create it
        if (!clientDir.exists()) {
            if(clientDir.mkdir())
                System.out.println("Created client directory: " + clientDir.getAbsolutePath());
            else {
                System.err.println("Unable to create client directory: " + clientDir.getAbsolutePath());
                System.exit(-1);
            }
        }


        SuperSecretClient ssc = new SuperSecretClient();
        ssc.StartClient();
    }
}
