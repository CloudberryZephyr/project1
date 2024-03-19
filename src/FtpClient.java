/** Author:  Shannen Stolkovich and Clara Shoemaker
 * Course:  COMP 342 Data Communications and Networking
 * Date:    16 March 2024
 * Description: Handles Client side of the FTP connection
 */



import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Scanner;

public class FtpClient {
   public static final int PORT = 9001;
   public static final String HOST = "127.0.0.1";


   public static Scanner scan = new Scanner(System.in);

    public static void main(String[] args) throws IOException {
        System.out.println("Welcome to the GCC FTP client service!");

        try {
            // initialize socket and io objects
            Socket socket = new Socket(HOST, PORT);
            System.out.println("Succeed: socket: " + socket);
            DataInputStream inputStream = new DataInputStream(socket.getInputStream());
            DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());

            // continues receiving commands until the client quits
            String keyword = "";
            while (!keyword.equals("QUIT")) {

                // prompt and receive, and parse commands
                System.out.println("Command:");
                keyword = scan.nextLine();
                ArrayList<String> params = new ArrayList<String>();
                Scanner scanner = new Scanner(keyword);
                scanner.useDelimiter(" ");
                while (scanner.hasNext()) {
                    params.add(scanner.next());
                }

                // send command to server
                outputStream.writeUTF(keyword);

                String command = params.get(0);

                // Switch block depending on command
                switch (command) {
                    case "LS" :
                        int length = inputStream.readInt();
                        String list = "";
                        for (int i = 0; i < length; i++) {
                            list += inputStream.readUTF() + "\n";
                        }
                        System.out.println(list);

                    case "PWD" :
                        System.out.println(inputStream.readUTF());

                    case "GET" :
                        GET(params.get(1), inputStream);

                    case "PUT" :
                        String filename = params.get(1);
                        outputStream.writeUTF(filename);

                        if (filename.endsWith(".png")) {
                            // transmit .png file
                            transmitPNG(filename, outputStream);
                        } else {
                            // transmit .txt file
                            transmitTXT(filename, outputStream);
                        }

                } // end of switch block

            } // end of while loop

            // close everything
            outputStream.close();
            inputStream.close();
            socket.close();

        } catch(IOException e) {
            throw new IOException("Server Connection Error. Please try again later.");
        }
    }

    /**
     * Sends files of type .png to the server
     *
     * @param filename String name of the file to send
     * @param outputStream DataOutputStream for socket output
     * @throws IOException
     */
    public static void transmitPNG(String filename, DataOutputStream outputStream) throws IOException{

        // find file to transmit in client_folder
        File file = new File(Path.of("client_folder").toAbsolutePath() + File.separator + filename);
        BufferedImage image = ImageIO.read(file);

        // write .png file to byte array
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ImageIO.write(image, "png", byteArrayOutputStream);
        byte[] imgArray = byteArrayOutputStream.toByteArray();

        // output byte array to server
        outputStream.writeInt(imgArray.length);
        outputStream.write(imgArray);
        outputStream.flush();

        byteArrayOutputStream.close();
    }

    /**
     * Sends files of type .txt to the server
     *
     * @param filename String name of file to send
     * @param outputStream DataOutputStream for socket output
     * @throws IOException
     */
    public static void transmitTXT(String filename, DataOutputStream outputStream) throws IOException{

        // find file in client_folder
        File[] files = Path.of("client_folder").toAbsolutePath().toFile().listFiles();

        // make a copy of the .txt file to be sent
        File copy = new File(filename);
        for (int i = 0; i < files.length; i++) {
            if (files[i].getName().equals(filename)) {
                copy = files[i];
            }
        }

        // scan through entire copy, copy data to String array list
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

    /**
     * Downloads a file of the given name into the client_folder
     *
     * @param filename String name of file to download
     * @param inputStream DataInputStream for socket input
     * @throws IOException
     */
    public static void GET(String filename, DataInputStream inputStream) throws IOException {

        // get file name and create new file in client_folder
        filename = inputStream.readUTF();
        File f = new File(Path.of("client_folder").toAbsolutePath() + File.separator + filename);
        FileWriter writer = new FileWriter(f);

        int length = inputStream.readInt();

        // receiving process is different depending on file type
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

}
