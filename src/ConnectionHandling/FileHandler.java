package ConnectionHandling;

import Main.ClientException;
import Main.Constants;
import Main.DisconnectException;

import java.io.*;
import java.net.Socket;

/**
 * Created by Reviara on 15-1-14.
 */
public class FileHandler {
    private ConnectionHandler ch;

    public FileHandler(ConnectionHandler connHandler) {
        this.ch = connHandler;
    }

    public long getFileSize(String fileName) throws ClientException {

        File file = new File(Constants.CLIENT_PATH, (fileName));

        if (!file.exists() || !file.isFile())
            throw new ClientException("Cannot find the file specified.");

        return file.length();
    }

    public long getFileLastModified(String fileName) throws ClientException {
        File file = new File(Constants.CLIENT_PATH, (fileName));

        if (!file.exists() || !file.isFile())
            throw new ClientException("Cannot find the file specified.");

        return file.lastModified() / 1000;
    }

    public void sendFile(String fileName) throws ClientException, DisconnectException {
        File file = new File(Constants.CLIENT_PATH, (fileName));

        if (!file.exists() || !file.isFile())
            throw new ClientException("Cannot find the file specified.");

        try {
            int remainingSize = (int) file.length();
            FileInputStream fis = new FileInputStream(file);
            while (remainingSize != 0) {
                int buffSize = Math.min(Constants.BUFFER_SIZE, remainingSize);
                byte[] buffer = new byte[buffSize];
                fis.read(buffer);
                ch.Write(buffer);

                remainingSize -= buffSize;
            }
            fis.close();
        } catch (IOException e) {
            throw new DisconnectException("Error while sending the file.");
        }
    }

    public void saveFile(String fileName, int fileSize, int lastModifiedTime) throws ClientException, DisconnectException {
        File file = new File(Constants.CLIENT_PATH + fileName);
        file.getParentFile().mkdirs();

        try {
            int remainingSize = fileSize;
            FileOutputStream fos = new FileOutputStream(file);
            while(remainingSize != 0)
            {
                int buffSize = Math.min(Constants.BUFFER_SIZE, remainingSize);
                byte[] buffer = new byte[buffSize];

                buffer = ch.Read(buffer.length);
                if (buffer == null)
                    throw new DisconnectException("Socket was closed when trying to write to file.");
                fos.write(buffer);

                remainingSize -= buffSize;
            }
            fos.close();
            file.setLastModified(lastModifiedTime);
        } catch (IOException e) {
            throw new DisconnectException("Error while saving the file.");
        }
    }

    public String readToOutput(int fileSize) throws ClientException, DisconnectException {
        String returnVal = "";
        try {
            int remainingSize = fileSize;
            while(remainingSize != 0) {
                int buffSize = Math.min(Constants.BUFFER_SIZE, remainingSize);
                byte[] buffer = new byte[buffSize];

                buffer = ch.Read(buffer.length);
                if (buffer == null)
                    throw new DisconnectException("Socket was closed when trying to write to output.");
                returnVal += new String(buffer, "UTF-8");
                remainingSize -= buffSize;
            }
        } catch (IOException e) {
            throw new DisconnectException("Error reading from socket.");
        }
        return returnVal;
    }
}
