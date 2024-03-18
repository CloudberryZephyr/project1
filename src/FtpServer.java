/** Author:  Shannen Stolkovich and Clara Shoemaker
 * Course:  COMP 342 Data Communications and Networking
 * Date:    16 March 2022
 * Description: Handles Server side of the FTP connection
 */

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
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
            while (true) {
                command = inputStream.readUTF();
                parseCommand(command, inputStream, outputStream);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Finds path to current directory and changes currentDirectory member variable
     */
    public static Path getCurrentDirectory() {
        Path path = Path.of("server_folder");
        return path;
    }

    /**
     * Parses command string to run client's desired command
     *
     * @param command String containing keyword and possibly parameters from Client
     */
    public static void parseCommand(String command, DataInputStream inputStream, DataOutputStream outputStream) throws IOException {
        Scanner commandParser = new Scanner(command);

        ArrayList<String> params = new ArrayList<String>();
        commandParser.useDelimiter(" ");

        while (commandParser.hasNext()) {
            params.add(commandParser.next());
        }

        String keyword = params.get(0);

        if (keyword.equals("LS")) {
            LS(outputStream);
        } else if (keyword.equals("PUT")) {
            PUT(params.get(1), inputStream);
        } else if (keyword.equals("GET")) {
            GET(params.get(1), outputStream);
        } else if (keyword.equals("PWD")) {
            PWD(outputStream);
        } else if (keyword.equals("QUIT")){
            System.exit(0);
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
    public static void GET(String filename, DataOutputStream outputStream) throws IOException{
       outputStream.writeUTF(filename);
        if (filename.endsWith(".png")) {
            // transmit .png file
            transmitPNG(filename, outputStream);
        } else {
            // transmit .txt file
            transmitTXT(filename, outputStream);
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
        outputStream.writeUTF(currentDirectory.toString());
    }

    /**
     * Transmits a file of type .png to client
     *
     * @param filename the name of the png file to be transmitted
     */
    public static void transmitPNG(String filename, DataOutputStream outputStream) throws IOException{
        File file = new File(filename);
        BufferedImage image = ImageIO.read(file);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ImageIO.write(image, "png", byteArrayOutputStream);
        byte[] imgArray = byteArrayOutputStream.toByteArray();
        outputStream.writeInt(imgArray.length);
        outputStream.write(imgArray);

        byteArrayOutputStream.close();
    }

    /**
     * Transmits a file of type .txt to client
     *
     * @param filename the name of the png file to be transmitted
     */
    public static void transmitTXT(String filename, DataOutputStream outputStream) throws IOException{
        File file = new File(filename);
        Scanner fileSc = new Scanner(file);

        ArrayList<String> fileArray = new ArrayList<String>();
        // add contents of file to ArrayList
        while (fileSc.hasNext()) {
            fileArray.add(fileSc.next());
        }

        // send number of messages to expect
        outputStream.writeInt(fileArray.size());

        // send messages
        for (int i = 0; i < fileArray.size(); i++) {
            outputStream.writeUTF(fileArray.get(i));
        }

        fileSc.close();
    }

}
