import java.io.*;

/**
 * Created by Reviara on 15-1-14.
 */
public class FileHandler {
    public FileHandler() {

    }

    public long getFileSize(String fileName) throws ClientException {
        File file = new File(Constants.CLIENT_PATH, (fileName));

        if (!file.exists() || !file.isFile())
            throw new ClientException("Cannot find the file specified.");

        return file.length();
    }

    public void sendFile(OutputStream sos, String fileName) throws ClientException {
        File file = new File(Constants.CLIENT_PATH, (fileName));

        if (!file.exists() || !file.isFile())
            throw new ClientException("Cannot find the file specified.");

        try {
            FileInputStream fis = new FileInputStream(file);
            byte[] buffer = new byte[Constants.BUFFER_SIZE];

            while (fis.read(buffer) != -1)
                sos.write(buffer);
        } catch (IOException e) {
            throw new ClientException("Error while reading the file.");
        }
    }

    public void saveFile(InputStream sis, String fileName, int fileSize) throws ClientException {
        File file = new File(Constants.CLIENT_PATH + fileName);
        file.getParentFile().mkdirs();

        try {
            int remainingSize = fileSize;
            while(remainingSize != 0)
            {
                FileOutputStream fos = new FileOutputStream(file);
                int buffSize = Math.min(Constants.BUFFER_SIZE, remainingSize);
                byte[] buffer = new byte[buffSize];

                sis.read(buffer);
                fos.write(buffer);

                remainingSize -= buffSize;
            }
        } catch (IOException e) {
            throw new ClientException("Error while saving the file.");
        }
    }

    public String readToOutput(InputStream sis, int fileSize) throws ClientException {
        String returnVal = "";
        try {
            int remainingSize = fileSize;
            while(remainingSize != 0) {
                int buffSize = Math.min(Constants.BUFFER_SIZE, remainingSize);
                byte[] buffer = new byte[buffSize];

                sis.read(buffer, 0, buffSize);
                returnVal += new String(buffer, "UTF-8");
                remainingSize -= buffSize;
            }
        } catch (IOException e) {
            throw new ClientException("Error reading from socket.");
        }
        return returnVal;
    }
}
