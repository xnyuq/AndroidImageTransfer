import javax.naming.Context;
import java.net.*;
import java.io.*;
import java.util.Enumeration;

public class SocketServer {

    public static void main(String[] args) throws SocketException {
        try {
            ServerSocket serverSocket = new ServerSocket(8888);
            System.out.println("Expecting client connection...");
            Socket client = serverSocket.accept();
            System.out.println("client connected!");

            final File f = new File("D:\\study\\laptrinhmang\\test.jpg");
            InputStream inputStream = client.getInputStream();
            byte buf[] = new byte[1024];
            int len;
            OutputStream fileOut = new FileOutputStream(f);
            while ((len = inputStream.read(buf)) != -1) {
                fileOut.write(buf, 0, len);
            }
            fileOut.close();
            inputStream.close();
            System.out.println("File transfered succeed!");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
