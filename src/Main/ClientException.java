package Main;

import com.sun.deploy.util.SessionState;

/**
 * Created by Reviara on 15-1-14.
 */
public class ClientException extends Exception {
    private String message;

    public ClientException(String message) {
        this.message = message;
    }

    public String what() {
        return message;
    }
}
