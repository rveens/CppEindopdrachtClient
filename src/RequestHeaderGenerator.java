import java.io.File;
import java.util.HashMap;
import java.util.Set;

/**
 * Created by Rick Veens on 12/9/13.
 */
public class RequestHeaderGenerator {

    public RequestHeaderGenerator() {
    }

    public String GenerateRequestHeader(HashMap<String, String> requestArgs) {
        String response;

        // create first line of the SUPERSECRETPROTOCOL
        response = Constants.PROTOCOL + ' ' + requestArgs.get("command") + '\n';

        // create a string with attribute fields
        if (requestArgs != null) {
            Set<String> keys = requestArgs.keySet();
            for( String s : keys)
                if(!s.equals("command"))
                    response += s + ": " + requestArgs.get(s) + '\n';
        }
        response += "\n";

        return response;
    }
}
