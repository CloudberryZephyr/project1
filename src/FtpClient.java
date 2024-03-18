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
            Socket socket = new Socket(HOST, PORT);
            System.out.println("Succeed: socket: " + socket);
            DataInputStream inputStream = new DataInputStream(socket.getInputStream());
            DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());

            String keyword = "";

            while (!keyword.equals("QUIT")) {
                System.out.println("Command:");

                keyword = scan.nextLine();

                ArrayList<String> params = new ArrayList<String>();
                Scanner scanner = new Scanner(keyword);
                scanner.useDelimiter(" ");

                while (scanner.hasNext()) {
                    params.add(scanner.next());
                }
                outputStream.writeUTF(keyword);

                String command = params.get(0);


                if (command.equals("LS")) {
                    int length = inputStream.readInt();
                    String list = "";
                    for (int i = 0; i < length; i++) {
                        list += inputStream.readUTF() + "\n";
                    }
                    System.out.println(list);

                } else if (command.equals("PWD")) {
                    System.out.println(inputStream.readUTF());

                } else if (command.equals("GET")) {

                    String filename = inputStream.readUTF();
                    File f = new File(filename);
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

                } else if (command.equals("PUT")) {
                    String filename = params.get(1);
                    outputStream.writeUTF(filename);
                    if (filename.endsWith(".png")) {
                        // transmit .png file
                        transmitPNG(filename, outputStream);
                    } else {
                        // transmit .txt file
                        transmitTXT(filename, outputStream);
                    }
                }
                // end of if block

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
     * Transmits a file of type .png to client
     *
     * @param filename the name of the png file to be transmitted
     */
    public static void transmitPNG(String filename, DataOutputStream outputStream) throws IOException{
        File file = new File(Path.of("client_folder").toAbsolutePath() + File.separator + filename);
        BufferedImage image = ImageIO.read(file);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ImageIO.write(image, "png", byteArrayOutputStream);
        byte[] imgArray = byteArrayOutputStream.toByteArray();
        outputStream.writeInt(imgArray.length);
        outputStream.write(imgArray);
        outputStream.flush();

        byteArrayOutputStream.close();
    }

    /**
     * Transmits a file of type .txt to client
     *
     * @param filename the name of the png file to be transmitted
     */
    public static void transmitTXT(String filename, DataOutputStream outputStream) throws IOException{

        File[] files = Path.of("client_folder").toAbsolutePath().toFile().listFiles();

        File copy = new File(filename);

        for (int i = 0; i < files.length; i++) {
            if (files[i].getName().equals(filename)) {
                copy = files[i];
            }
        }

        FileInputStream input = new FileInputStream(copy);

        Scanner scan = new Scanner(input);

        ArrayList<String> fileArray = new ArrayList<String>();
        // add contents of file to ArrayList
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
