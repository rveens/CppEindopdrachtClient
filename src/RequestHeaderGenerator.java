import java.io.File;
import java.util.HashMap;
import java.util.Set;

/**
 * Created by Rick Veens on 12/9/13.
 */
public class RequestHeaderGenerator {
    private String protocolString;
    private int[] version;

    public RequestHeaderGenerator(String protocolString, int version[]) {
        this.protocolString = protocolString;
        this.version = version;
    }

    public String GenerateRequestHeader(String command, HashMap<String, String> attributesMap) {
        String firstLine, attributeLines;
        firstLine = attributeLines = "";

        // create first line of the SUPERSECRETPROTOCOL
        firstLine        =      protocolString +
                                '-'            +
                                version[0]     +
                                '.'            +
                                version[1]     +
                                ' '            +
                                command        +
                                '\n';

        // create a string with attribute fields
        if (attributesMap != null) {
            Set<String> keys = attributesMap.keySet();
            for( String s : keys)
                attributeLines += s + ": " + attributesMap.get(s) + '\n';
        }

        return firstLine + attributeLines + '\n';
    }
}
