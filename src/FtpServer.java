/** Author:  Shannen Stolkovich and Clara Shoemaker
 * Course:  COMP 342 Data Communications and Networking
 * Date:    16 March 2022
 * Description: Handles Server side of the FTP connection
 */

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Scanner;

public class FtpServer {
    public static final int PORT = 9001;
    public static String currentDirectory;

    public static void main(String[] args) {
        try {

            // initialize socket
            ServerSocket serverSocket = new ServerSocket(PORT);
            Socket socket = serverSocket.accept();
            System.out.println("Welcome to GCC FTP Service!\nWaiting for client commands...");

            // initialize input and output streams
            DataInputStream inputStream = new DataInputStream(socket.getInputStream());
            DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());

            // While the client wants the connection to continue, parse the commands
            String command = "";
            while (command != "QUIT") {
                command = inputStream.readUTF();

                parseCommand(command);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Parses command string to run client's desired command
     *
     * @param command String containing keyword and possibly parameters from Client
     */
    public static void parseCommand(String command) {
        Scanner commandParser = new Scanner(command);

        String keyword = commandParser.next();
        ArrayList<String> params = new ArrayList<String>();
        while (commandParser.hasNext()) {
            params.add(commandParser.next());
        }
        
        if (keyword == "LS") {
            LS();
        } else if (keyword == "PUT") {
            PUT(params.get(0));
        } else if (keyword == "GET") {
            GET(params.get(0));
        } else if (keyword == "PWD") {
            PWD();
        } else {

        }
    }

    /**
     * Outputs the files in the current directory to the Client
     */
    public static void LS() {
        File[] files =

    }

    /**
     * Downloads the file specified by filename to client
     *
     * @param filename String name of file to be downloaded
     */
    public static void GET(String filename) {

    }

    /**
     * Receives the file specified by filename from the client
     */
    public static void PUT(String filename) {

    }

    /**
     * Lists the current directory, using path stored in currentDirectory
     */
    public static void PWD() {

    }

    /**
     * Finds path to current directory and changes currentDirectory member variable
     */
    public static void updateCurrentDirectory() {

    }

}
