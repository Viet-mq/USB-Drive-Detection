package server;

import usb.FileInfo;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Objects;

public class UDPServer {
    private static final int PIECES_OF_FILE_SIZE = 1024 * 32;
    private DatagramSocket serverSocket;
    private final int port = 8888;

    public static void main(String[] args) {
        UDPServer udpServer = new UDPServer();
        udpServer.openUDPServer();
    }

    public void openUDPServer() {
        try {
            serverSocket = new DatagramSocket(port);
            System.out.println("UDP Server is opened on port " + port);
            listening();
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    private void listening() {
        while (true) {
            receiveFile();
        }
    }

    public void receiveFile() {
        byte[] receiveData = new byte[PIECES_OF_FILE_SIZE];
        DatagramPacket receivePacket;

        try {
            // get file info
            receivePacket = new DatagramPacket(receiveData, receiveData.length);
            serverSocket.receive(receivePacket);
            InetAddress inetAddress = receivePacket.getAddress();
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(receivePacket.getData());
            ObjectInputStream ois = new ObjectInputStream(byteArrayInputStream);
            FileInfo fileInfo = (FileInfo) ois.readObject();

            // get file content
            //System.out.println("Receiving file...");
            File fileReceive = new File(Objects.requireNonNull(fileInfo).getDestinationDirectory()
                    + fileInfo.getFilename());
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(fileReceive));

            // write pieces of file
            for (int i = 0; i < (fileInfo.getPiecesOfFile() - 1); i++) {
                receivePacket = new DatagramPacket(receiveData, receiveData.length,
                        inetAddress, port);
                serverSocket.receive(receivePacket);
                bos.write(receiveData, 0, PIECES_OF_FILE_SIZE);
            }
            // write last bytes of file
            receivePacket = new DatagramPacket(receiveData, receiveData.length,
                    inetAddress, port);
            serverSocket.receive(receivePacket);
            bos.write(receiveData, 0, fileInfo.getLastByteLength());
            bos.flush();
            System.out.println("file transfer done!");

            // close stream
            bos.close();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
