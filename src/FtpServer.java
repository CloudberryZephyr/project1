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

            // store the current directory
            currentDirectory = getCurrentDirectory();

            // while the client wants the connection to continue, parse the commands
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
        Path path = Path.of("server_folder").toAbsolutePath();
        return path;
    }

    /**
     * Parses command string to run client's desired command
     *
     * @param command String command from client
     * @param inputStream DataInputStream for socket input
     * @param outputStream DataInputStream for socket output
     * @throws IOException
     */
    public static void parseCommand(String command, DataInputStream inputStream, DataOutputStream outputStream) throws IOException {

        // scan through and parse the client's command
        Scanner commandParser = new Scanner(command);
        ArrayList<String> params = new ArrayList<String>();
        commandParser.useDelimiter(" ");
        while (commandParser.hasNext()) {
            params.add(commandParser.next());
        }

        // switch block depending on command
        String keyword = params.get(0);

        switch (keyword) {
            case "LS" :
                LS(outputStream);

            case "PUT" :
                PUT(params.get(1), inputStream);

            case "GET" :
                GET(params.get(1), outputStream);

            case "PWD" :
                PWD(outputStream);

            case "QUIT" :
                System.exit(0);
        }
    }

    /**
     * Outputs the files in the current directory to client
     *
     * @param outputStream DataOutputStream for socket output
     * @throws IOException
     */
    public static void LS(DataOutputStream outputStream) throws IOException{
        // find all files in current directory
        File[] files = currentDirectory.toFile().listFiles();

        // output number of files in directory
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
     * @throws IOException
     */
    public static void GET(String filename, DataOutputStream outputStream) throws IOException{
        // output file name
        outputStream.writeUTF(filename);

        // transmission process depends on file type
        if (filename.endsWith(".png")) {
            // transmit .png file
            transmitPNG(filename, outputStream);
        } else {
            // transmit .txt file
            transmitTXT(filename, outputStream);
        }
    }

    /**
     * Receives the file specified by filename from client
     *
     * @param filename String name of file being uploaded from the client
     * @param inputStream DataInputStream for socket input
     * @throws IOException
     */
    public static void PUT(String filename, DataInputStream inputStream) throws IOException {

        // create new file in server_folder
        filename = inputStream.readUTF();
        File f = new File(Path.of("server_folder").toAbsolutePath() + File.separator + filename);
        FileWriter writer= new FileWriter(f);

        int length = inputStream.readInt();

        // process is different depending on file type
        if (filename.endsWith(".png")) {
            // receive .png file
            byte[] imageAr = new byte[length];
            inputStream.read(imageAr);
            BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageAr));
            ImageIO.write(image, "png", f);

        } else {
            // receive .txt file
            for(int i = 0; i < length; i++) {
                writer.write(inputStream.readUTF());
            }
        }
        writer.flush();

        writer.close();
    }

    /**
     * Outputs the current directory, using path stored in currentDirectory
     *
     * @param outputStream DataOutputStream for socket output
     * @throws IOException
     */
    public static void PWD(DataOutputStream outputStream) throws IOException {
        outputStream.writeUTF(currentDirectory.toString());
    }

    /**
     * Transmits a file of type .png to client
     *
     * @param filename the name of the png file to be transmitted
     * @param outputStream DataOutputStream for socket output]
     * @throws IOException
     */
    public static void transmitPNG(String filename, DataOutputStream outputStream) throws IOException{

        // finds file to be sent in server_folder
        File file = new File(currentDirectory + File.separator + filename);
        BufferedImage image = ImageIO.read(file);

        // write .png file to byte array
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ImageIO.write(image, "png", byteArrayOutputStream);
        byte[] imgArray = byteArrayOutputStream.toByteArray();

        // output byte array with image to client
        outputStream.writeInt(imgArray.length);
        outputStream.write(imgArray);
        outputStream.flush();

        byteArrayOutputStream.close();
    }

    /**
     * Transmits a file of type .txt to client
     *
     * @param filename the name of the png file to be transmitted
     * @param outputStream DataOutputStream for socket output
     * @throws IOException
     */
    public static void transmitTXT(String filename, DataOutputStream outputStream) throws IOException{
        // find file to transmit from server_folder
        File[] files = currentDirectory.toFile().listFiles();

        // copy the .txt file to be sent
        File copy = new File(filename);
        for (int i = 0; i < files.length; i++) {
            if (files[i].getName().equals(filename)) {
                 copy = files[i];
            }
        }

        // scan through entire copy, copy data to ArrayList of Strings
        FileInputStream input = new FileInputStream(copy);
        Scanner scan = new Scanner(input);
        ArrayList<String> fileArray = new ArrayList<String>();
        while (scan.hasNext()) {
            fileArray.add(scan.nextLine());
        }

        // send number of messages to expect
        outputStream.writeInt(fileArray.size());

        // send messages
        for (int i = 0; i < fileArray.size(); i++) {
            outputStream.writeUTF(fileArray.get(i));
        }
        outputStream.flush();

        scan.close();
    }

}
