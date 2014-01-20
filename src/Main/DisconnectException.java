package Main;

/**
 * Created by Reviara on 15-1-14.
 */
public class DisconnectException extends Exception {
    private String message;

    public DisconnectException(String message) {
        this.message = message;
    }

    public String what() {
        return message;
    }
}
