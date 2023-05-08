public class ServerMain {

    public static void main(String[] args) {
        TCPServer server = new TCPServer(8888);
        server.start();
    }
}
