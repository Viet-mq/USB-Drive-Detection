import client.TCPClient;
import client.UDPClient;
import server.TCPServer;
import server.UDPServer;

public class ClientServerManager {

    private final UDPClient udpClient = new UDPClient();
    private final UDPServer udpServer = new UDPServer();

    public void run() throws InterruptedException{

        Thread A = new Thread(new Runnable() {
            @Override
            public void run() {
                synchronized (this){
                    //tcpServer.start();
                    udpServer.openUDPServer();
                }
            }
        });


        Thread B = new Thread(new Runnable() {
            @Override
            public void run() {
                synchronized (this){
                    //tcpClient.initializeTCPClient();
                    udpClient.initializeUDPClient();
                }
            }
        });

        A.start();
        B.start();
        A.join();
        B.join();
    }

    public static void main(String[] args) throws InterruptedException {
        ClientServerManager clientServerManager = new ClientServerManager();
        clientServerManager.run();
    }
}
