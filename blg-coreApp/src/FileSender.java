

import java.io.IOException;
import java.net.*;
import java.io.*;


public class FileSender {

    
    public static void sendFile() throws IOException {
        
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(15123); //server listens on port 15123
            Socket socket = serverSocket.accept(); //blocks until a client connects
            System.out.println("Accepted connection: " + socket);
            File transferFile = new File("MisplacedRecords.txt");
            byte[] bytearray = new byte[(int) transferFile.length()]; //temp array of size equal to filesize
            FileInputStream fin = new FileInputStream(transferFile); //fetch data from a file
            BufferedInputStream bin = new BufferedInputStream(fin); //
            bin.read(bytearray, 0, bytearray.length);  //storing from a bufferedinput stream to a byte array
            OutputStream os = socket.getOutputStream();
            System.out.println("Sending a text file");
            os.write(bytearray, 0, bytearray.length); //byte array is sent to the client with the help of output stream
            os.flush();
            serverSocket.close();
            socket.close();
            System.out.println("File transfer complete");
        } catch (IOException ex) {
            System.out.println("Exception:" + ex);
            System.exit(0);
        } finally {
            serverSocket.close();
        }

    }

}
