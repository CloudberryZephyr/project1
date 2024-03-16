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

public class FtpServer {
    public static final int PORT = 9001;

    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(PORT);
            Socket socket = serverSocket.accept();
            System.out.println("Welcome to GCC FTP Service!\nWaiting for client commands...");


            DataInputStream inputStream = new DataInputStream(socket.getInputStream());
            DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());

            String command = "";


            // While the client wants the connection to continue, parse the commands
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

    }

}
