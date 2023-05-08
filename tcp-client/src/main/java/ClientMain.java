import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;
import java.util.logging.Logger;

public class ClientMain {
    private static final Logger LOGGER = Logger.getLogger(ClientMain.class.getName());

    public static void main(String[] args) throws IOException {
        String serverAddress = "localhost";
        int serverPort = 8888;

        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter your username: ");
        String username = scanner.nextLine();
        System.out.println("username is " + username);
        Socket socket = new Socket(serverAddress, serverPort);
        TCPClient client = new TCPClient(socket, username);
        client.listen();
        client.sendMessage();

    }
}
