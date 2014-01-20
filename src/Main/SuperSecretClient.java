package Main;

import ConnectionHandling.ConnectionHandler;
import ConnectionHandling.FileHandler;
import InputHandling.CommandHandler;
import InputHandling.RequestHeaderGenerator;
import ResponseHandling.ResponseHandler;

import java.io.*;
import java.net.Socket;
import java.util.HashMap;

/**
 * Created by Rick Veens on 12/9/13.
 */
public class SuperSecretClient {

    private BufferedReader uir;         // Voor het lezen van gebruiker input

    public SuperSecretClient() {
        uir = new BufferedReader(new InputStreamReader(System.in));
    }

    public void StartClient() {

        while (true) {
            try {
                printMessage("Server adres: ");
                String host = getInput();
                printMessage("Server port: ");
                String temp = getInput();
                int port = Integer.parseInt(temp);

                //Create socket
                Socket s = new Socket(host, port);

                //Handle this socket
                handle(s);

                //Print close message and start all over
                printMessage("Connection closed.");
            } catch (IOException e) {
                printMessage("ERROR: Server could not be found!");
            } catch (NumberFormatException e) {
                printMessage("ERROR: Server port must be a number!");
            } catch (ClientException e) {
                printMessage(e.what());
            }
        }
    }

    private void handle(Socket s) {
        while(true) {
            try {
                // Lees gebruiker input
                String userInputLine = getInput();

                // Maak connection handler aan die streams beheerd.
                ConnectionHandler connHandler = new ConnectionHandler(s);
                // Maak filehandler die bestanden lezen/schrijven afhandeld
                FileHandler fh = new FileHandler(connHandler);

                // Maak commandhandler aan die commando's van de input afhandeld.
                CommandHandler commHandler = new CommandHandler(fh);

                // CommandHandler gebruiker om user input af te handelen. Deze stuurt een bericht naar de server
                String userCommand = commHandler.HandleCommand(userInputLine, connHandler);
                // ResponseHandler handeld bericht van de server af.
                ResponseHandler rh = new ResponseHandler(connHandler, fh);
                rh.Handle(userCommand);
            } catch (DisconnectException e) {
                printMessage(e.what());
                break;
            } catch (ClientException e) {
                printMessage(e.what());
            } catch (NumberFormatException nfe) {
                printMessage(nfe.getMessage());
            }
        }
    }

    private String getInput() throws ClientException {
        if (uir != null)
            System.out.print('>');

        try {
            return uir.readLine();
        } catch (IOException e) {
            throw new ClientException("Error reading userinput.");
        }
    }

    private void printMessage(String mes) {
        System.out.println(mes);
    }
}