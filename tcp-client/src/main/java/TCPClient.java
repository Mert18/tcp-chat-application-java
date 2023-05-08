import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Logger;

public class TCPClient {
    private static final Logger LOGGER = Logger.getLogger(TCPClient.class.getName());
    private String username;
    private Socket socket;
    private BufferedReader in;
    private BufferedWriter out;

    private List<Contact> contactList = new ArrayList<>();

    public TCPClient(Socket socket, String username) {
        try {
            this.socket = socket;
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.username = username;
            Scanner scanner = new Scanner(System.in);
            createContactList(scanner);

            out.write(username);
            out.newLine();
            out.flush();
        } catch (IOException e) {
            shutdown(socket, in, out);
        }
    }

    public void sendMessage() {
        try {
            Scanner scanner = new Scanner(System.in);
            while(socket.isConnected()) {
                System.out.println("Enter your message: ");
                String message = scanner.nextLine();

                // Prompt the user to select a contact or group to send the message to
                System.out.println("Select a contact or group to send the message to:");
                int index = 1;
                for (Contact contact : contactList) {
                    System.out.println(index + ". " + contact.getLabel() + " (" + contact.getUsername() + ")");
                    index++;
                }
                System.out.println(index + ". Everyone");
                int selection = scanner.nextInt();
                scanner.nextLine(); // consume the newline character
                // Construct the message with the selected recipient(s)
                Contact contact = contactList.get(selection - 1);
                message += "_" + contact.getUsername();

                out.write(message);
                out.newLine();
                out.flush();
            }
        } catch (IOException e) {
            shutdown(socket, in, out);
            LOGGER.warning("Error occurred while sending message to server: " + e.getMessage());
        }
    }

    public void listen() {
        System.out.println("will listen now");
        new Thread(new Runnable() {
            @Override
            public void run() {
                String message;
                System.out.println("listening");
                while(socket.isConnected()) {
                    try {
                        System.out.println("will try to read the message.");
                        message =  in.readLine();
                        System.out.println("what is this");
                        System.out.println("Message received from server: " + message);
                        System.out.println(message);
                    } catch (IOException e) {
                        shutdown(socket, in, out);
                        LOGGER.warning("Error occurred while reading from server: " + e.getMessage());
                        break;
                    }
                }
            }
        }).start();
    }

    public void shutdown(Socket socket, BufferedReader in, BufferedWriter out) {
        try {
            if(socket != null) {
                socket.close();
            }
            if(in != null) {
                in.close();
            }
            if(out != null) {
                out.close();
            }
        } catch (IOException e) {
            LOGGER.warning("Error occurred while shutting down client: " + e.getMessage());
        }
    }

    private void createContactList(Scanner scanner) {
        System.out.println("Create your contact list:");
        while (true) {
            System.out.print("Enter a username (or enter \"done\" to finish): ");
            String input = scanner.nextLine();
            if (input.equals("done")) {
                break;
            }
            String username = input;
            System.out.print("Enter a label for this contact (e.g. \"friend\"): ");
            String label = scanner.nextLine();
            contactList.add(new Contact(username, label));
            System.out.println("Contact added!");
        }
    }

}
