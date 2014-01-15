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
        File file = new File(Constants.CLIENT_PATH, (fileName));

        if (!file.exists() || !file.isFile())
            throw new ClientException("Cannot find the file specified.");

        try {
            FileOutputStream fos = new FileOutputStream(file);
            int buffSize = Math.min(Constants.BUFFER_SIZE, fileSize);
            byte[] buffer = new byte[buffSize];

            while (sis.read(buffer) != -1)
                fos.write(buffer);
        } catch (IOException e) {
            throw new ClientException("Error while reading the file.");
        }
    }
}
