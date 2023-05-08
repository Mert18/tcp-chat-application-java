import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Logger;

public class TCPServer {

    // Logger for logging events in the server.
    private static final Logger LOGGER = Logger.getLogger(TCPServer.class.getName());

    private ServerSocket serverSocket;
    private boolean isRunning;
    // Defines what port the server listens on.
    private int portNumber;

    public TCPServer(int portNumber) {
        this.portNumber = portNumber;
    }

    public void start() {
        isRunning = true;
        try {
            serverSocket = new ServerSocket(portNumber);
            LOGGER.info("Server started and listening on " + portNumber);
            while (isRunning) {
                Socket clientSocket = serverSocket.accept();

                // Create a new thread to handle     this client connection
                ClientHandler clientHandler = new ClientHandler(clientSocket);

                // Fire up a new thread for each connection.
                Thread clientThread = new Thread(clientHandler);
                clientThread.start();
            }
        } catch (IOException e) {
            LOGGER.warning("Error occurred while connecting a client: " + e.getMessage());
        }
    }

    public void stop() {
        isRunning = false;
        try {
            serverSocket.close();
        } catch (IOException e) {
            LOGGER.warning("Error occurred while stopping the server: " + e.getMessage());
        }
    }


}
