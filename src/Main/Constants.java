package Main;

public class Constants {
    public static final int BUFFER_SIZE = 1337;
    public static final int MAX_READLINE_SIZE = 1337;

    public static final String CLIENT_PATH;
    public static final String PROTOCOL = "SUPERSECRETPROTOCOL-1.0";

    static
    {
        String OS = System.getProperty("os.name", "generic").toLowerCase();
        if(OS.equals("windows 8"))
            CLIENT_PATH = "C:/Temp/Client/";
        else
            CLIENT_PATH = "/tmp/testclient/";
    }
}
