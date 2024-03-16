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
    public static Path currentDirectory;

    public static void main(String[] args) {
        try {

            // initialize socket
            ServerSocket serverSocket = new ServerSocket(PORT);
            Socket socket = serverSocket.accept();
            System.out.println("Welcome to GCC FTP Service!\nWaiting for client commands...");

            // initialize input and output streams
            DataInputStream inputStream = new DataInputStream(socket.getInputStream());
            DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());

            currentDirectory = getCurrentDirectory();

            // While the client wants the connection to continue, parse the commands
            String command = "";
            while (command != "QUIT") {
                command = inputStream.readUTF();

                parseCommand(command, inputStream, outputStream);
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
    public static void parseCommand(String command, DataInputStream inputStream, DataOutputStream outputStream) throws IOException {
        Scanner commandParser = new Scanner(command);

        String keyword = commandParser.next();
        ArrayList<String> params = new ArrayList<String>();
        while (commandParser.hasNext()) {
            params.add(commandParser.next());
        }
        
        if (keyword == "LS") {
            LS(outputStream);
        } else if (keyword == "PUT") {
            PUT(params.get(0), inputStream);
        } else if (keyword == "GET") {
            GET(params.get(0), outputStream);
        } else if (keyword == "PWD") {
            PWD(outputStream);
        } else {

        }
    }

    /**
     * Outputs the files in the current directory to the Client
     */
    public static void LS(DataOutputStream outputStream) throws IOException{
        File[] files = currentDirectory.toFile().listFiles();
        outputStream.writeInt(files.length);
        // output every file name in the array of files under the current directory
        for (int i = 0; i < files.length; i++) {
            outputStream.writeUTF(files[i].getName());
        }
    }

    /**
     * Downloads the file specified by filename to client
     *
     * @param filename String name of file to be downloaded
     * @param outputStream OutputStream
     */
    public static void GET(String filename, DataOutputStream outputStream) {
        try {
            File file = new File(filename);
            outputStream



        } catch (IOException e) {
            System.out.println("Error...");
        }

    }

    /**
     * Receives the file specified by filename from the client
     */
    public static void PUT(String filename, DataInputStream inputStream) {

    }

    /**
     * Lists the current directory, using path stored in currentDirectory
     */
    public static void PWD(DataOutputStream outputStream) throws IOException {
        outputStream.writeChars(currentDirectory.toString());
    }

    /**
     * Finds path to current directory and changes currentDirectory member variable
     */
    public static Path getCurrentDirectory() {
        File filename = new File("FtpServer.java");
        Path path = filename.toPath().getParent().getParent();
        return path;
    }

}
