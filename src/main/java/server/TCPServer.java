package server;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import net.TLVPackage;
import net.TLV;

public class TCPServer {
    private static final int port = 9900;
    ServerSocket serverSocket = null;
    List <SocketClient> list = new ArrayList<>();

    public static void main(String[] args) {
        System.out.println("Server opened on port " + port);
        new TCPServer().start();
    }

    public void start() {
        try {
            serverSocket = new ServerSocket(port);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            while (true) {
                Socket s = serverSocket.accept ();
                SocketClient sc = new SocketClient(s);
                new Thread(sc).start();
                list.add(sc);
            }
        } catch (Exception e) {

            e.printStackTrace();
        }

    }

    static class SocketClient implements Runnable {
        Socket s = null;
        TLV tlv = null;

        public SocketClient(Socket s) {
            this.s = s;
            tlv = new TLV(s);
        }

        @Override
        public void run() {
            tlv.writeMsg("Ready to receive file", 200);
            System.out.println("Sent request");
            while (true) {
                TLVPackage tlvPackage = tlv.getLogFile();
                if(tlvPackage == null) break;
                else
                    System.out.println(tlvPackage);
            }
        }

        void close() {
            tlv.close();
            try {
                if (s != null) {
                    s.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    void close() {
        try {

            if (serverSocket != null) {
                serverSocket.close();
            }
            for (SocketClient sc : list) {
                sc.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class shutdownhook implements Runnable {
        @Override
        public void run() {
            close();
            System.out.println("server closed");
        }
    }

}

