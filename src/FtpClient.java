/** Author:  Shannen Stolkovich and Clara Shoemaker
 * Course:  COMP 342 Data Communications and Networking
 * Date:    16 March 2024
 * Description: Handles Client side of the FTP connection
 */



import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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

            String command = "";

            while (true) {
                System.out.println("Command:");

                command = scan.next();
                String filename = "";

                outputStream.writeUTF(command);

                if (command.equals("LS")) {
                    int length = inputStream.readInt();
                    System.out.println(length);
                    for (int i = 0; i < length; i++) {
                        System.out.println(inputStream.readUTF());
                    }
                } else if (command.equals("PWD")) {
                    System.out.println(inputStream.readUTF());
                } else if (command.equals("PUT")) {
                    System.out.println("File has been sent successfully.");
                } else if (command.equals("GET")) {

                } else if (command.equals("QUIT")) {
                    break;
                }
            }
        } catch(IOException e) {
            throw new IOException("Server Connection Error. Please try again later.");
        }
    }
}
