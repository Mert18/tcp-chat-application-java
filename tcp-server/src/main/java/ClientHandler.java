import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

public class ClientHandler implements Runnable{

    private static final Logger LOGGER = Logger.getLogger(ClientHandler.class.getName());

    private ArrayList<ClientHandler> clientList = new ArrayList<>();

    private Socket clientSocket;
    private BufferedReader in;
    private BufferedWriter out;
    private String username;

    public ClientHandler(Socket socket) throws IOException {
        try {
            System.out.println("Inside client handler constructor");
            this.clientSocket = socket;
            System.out.println("Created the socket");
            this.in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            this.out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
            System.out.println("Created the streams");
            System.out.println("Waiting for username");
            this.username = in.readLine();
            System.out.println("Username is read");
            System.out.println("Username: " + username);
            clientList.add(this);
            System.out.println("Client map is updated");
        } catch (IOException e) {
            shutdown(socket, in, out);
            LOGGER.warning("Error occurred while creating a client handler: " + e.getMessage());
        } catch (Exception e) {
            LOGGER.warning("Error occurred while connecting a client: " + e.getMessage());
        }
    }

    public Message convertToMessage(String message) {
        String[] messageParts = message.split(",");
        String sender = messageParts[0];
        String recipient = messageParts[1];
        String content = messageParts[2];
        return new Message(sender, recipient, content);
    }

    @Override
    public void run() {
        System.out.println("Inside client handler run method");
        String message;
        while(clientSocket.isConnected()) {
            try {
                System.out.println("waiting for message");
                message = in.readLine();
                System.out.println("Message received from client: " + message);
                String recipient = message.split("_")[1];
                String content = message.split("_")[0];
                System.out.println("Recipient: " + recipient);
                System.out.println("Content: " + content);
                sendServerMessage(recipient, message);
                } catch (IOException e) {
                try {
                    shutdown(clientSocket, in, out);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
                throw new RuntimeException(e);
            }
        }
    }

    public void sendServerMessage(String recipient, String message) throws IOException {
        System.out.println("Inside sendServerMessage method: " + message);
        for(ClientHandler clientHandler : clientList) {
            System.out.println("One Client: " + clientHandler.username);
            if (clientHandler.username.equals(recipient)) {
                clientHandler.out.write(message);
                clientHandler.out.newLine();
                clientHandler.out.flush();
            }
        }
    }

    public void removeClient() throws IOException {
        clientList.remove(this);
    }

    public void shutdown(Socket socket, BufferedReader in, BufferedWriter out) throws IOException {
        removeClient();
        try {
            if (socket != null) {
                socket.close();
            }
            if (in != null) {
                in.close();
            }
            if (out != null) {
                out.close();
            }

        } catch (IOException e) {
            LOGGER.warning("Error occurred while shutting down client: " + e.getMessage());
        }
    }


}
