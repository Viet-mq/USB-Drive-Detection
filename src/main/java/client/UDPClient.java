package client;

import net.ImageEncryption;
import net.TLVPackage;
import usb.FileInfo;

import java.io.*;
import java.net.*;
import java.util.Objects;

public class UDPClient {
    private static final int PIECES_OF_FILE_SIZE = 1024 * 32;
    private DatagramSocket clientSocket;
    private final String serverHost = "localhost";
    private final int serverPort = 8888;

    public static void main(String[] args) {
        UDPClient udpClient = new UDPClient();
        udpClient.initializeUDPClient();
    }

    public void initializeUDPClient(){
        UDPClient udpClient = new UDPClient();
        udpClient.sendFolder(new File("images"));
    }

    private void connectServer() {
        try {
            clientSocket = new DatagramSocket();
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    private void sendFolder(File filePath){
        String destinationDir = "server\\";
        UDPClient udpClient = new UDPClient();
        udpClient.connectServer();
        if(filePath.isDirectory()){
            for (File f : Objects.requireNonNull(filePath.listFiles())){
                String sourcePath = "images\\" + f.getName();
                udpClient.sendImagesFile(sourcePath, destinationDir);
            }
            System.out.println("=== Complete ===");
        }
    }

    private void sendImagesFile(String sourcePath, String destinationDir){
        InetAddress inetAddress;
        DatagramPacket sendPacket;
        ImageEncryption imageEncryption = new ImageEncryption();
        TLVPackage tlvPackage = new TLVPackage();

        String imageText = imageEncryption.encoder(sourcePath);

        try {
            File fileSend = new File(sourcePath);
            InputStream inputStream = new FileInputStream(fileSend);
            BufferedInputStream bis = new BufferedInputStream(inputStream);
            inetAddress = InetAddress.getByName(serverHost);
            byte[] bytePart = new byte[PIECES_OF_FILE_SIZE];

            // get file size
            long fileLength = fileSend.length();
            int piecesOfFile = (int) (fileLength / PIECES_OF_FILE_SIZE);
            int lastByteLength = (int) (fileLength % PIECES_OF_FILE_SIZE);

            // check last bytes of file
            if (lastByteLength > 0) {
                piecesOfFile++;
            }

            // split file into pieces and assign to fileBytess
            byte[][] fileBytess = new byte[piecesOfFile][PIECES_OF_FILE_SIZE];
            int count = 0;
            while (bis.read(bytePart, 0, PIECES_OF_FILE_SIZE) > 0) {
                fileBytess[count++] = bytePart;
                bytePart = new byte[PIECES_OF_FILE_SIZE];
            }

            // read file info
            FileInfo fileInfo = new FileInfo();
            fileInfo.setFilename(fileSend.getName());
            fileInfo.setFileSize(fileSend.length());
            fileInfo.setPiecesOfFile(piecesOfFile);
            fileInfo.setLastByteLength(lastByteLength);
            fileInfo.setDestinationDirectory(destinationDir);

            // send file info
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(fileInfo);
            sendPacket = new DatagramPacket(baos.toByteArray(), baos.toByteArray().length,
                    inetAddress, serverPort);
            clientSocket.send(sendPacket);

            // send file content
            System.out.println("Sending file... " + fileInfo.getFilename());
            // send pieces of file
            for (int i = 0; i < (count - 1); i++) {
                tlvPackage.setType(200);
                tlvPackage.setLength(PIECES_OF_FILE_SIZE);
                tlvPackage.setValue(fileBytess[i]);
                sendPacket = new DatagramPacket(tlvPackage.getValue(), PIECES_OF_FILE_SIZE, inetAddress, serverPort);
                clientSocket.send(sendPacket);
                waitMillisecond(20);
            }
            // send last bytes of file
            sendPacket = new DatagramPacket(fileBytess[count - 1], PIECES_OF_FILE_SIZE, inetAddress, serverPort);
            clientSocket.send(sendPacket);
            waitMillisecond(20);

            // close stream
            bis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void waitMillisecond(long millisecond) {
        try {
            Thread.sleep(millisecond);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
