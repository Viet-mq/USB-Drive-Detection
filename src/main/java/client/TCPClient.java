package client;

import usb.FileInfo;

import java.io.*;
import java.net.Socket;

public class TCPClient {
    private Socket client;
    private final String serverHost = "localhost";
    private final int serverPort = 9900;

    public static void main(String[] args) {
        TCPClient tcpClient = new TCPClient();
        tcpClient.initializeTCPClient();
    }

    public void initializeTCPClient(){
        String sourceFilePath = "Events.log";
        String destinationDir = "server\\";
        connectServer();
        sendFile(sourceFilePath, destinationDir);
        closeSocket(client);
    }

    public void connectServer() {
        try {
            client = new Socket(serverHost, serverPort);
            System.out.println("connected to server");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendFile(String sourceFilePath, String destinationDir) {
        DataOutputStream outToServer = null;
        ObjectOutputStream oos = null;
        ObjectInputStream ois = null;

        try {
            // make greeting
            outToServer = new DataOutputStream(client.getOutputStream());
            outToServer.writeUTF("Hello from " + client.getLocalSocketAddress());

            // get file info
            FileInfo fileInfo = getFileInfo(sourceFilePath, destinationDir);

            // send file
            oos = new ObjectOutputStream(client.getOutputStream());
            oos.writeObject(fileInfo);

            // get confirmation
            ois = new ObjectInputStream(client.getInputStream());
            fileInfo = (FileInfo) ois.readObject();
            if (fileInfo != null) {
                System.out.println(fileInfo.getStatus());
            }
        } catch (IOException | ClassNotFoundException ex) {
            ex.printStackTrace();
        } finally {
            closeStream(oos);
            closeStream(ois);
            closeStream(outToServer);
        }
    }

    private FileInfo getFileInfo(String sourceFilePath, String destinationDir) {
        FileInfo fileInfo = null;
        BufferedInputStream bis = null;
        try {
            File sourceFile = new File(sourceFilePath);
            bis = new BufferedInputStream(new FileInputStream(sourceFile));
            fileInfo = new FileInfo();
            byte[] fileBytes = new byte[(int) sourceFile.length()];
            // get file info
            bis.read(fileBytes, 0, fileBytes.length);
            fileInfo.setFilename(sourceFile.getName());
            fileInfo.setDataBytes(fileBytes);
            fileInfo.setDestinationDirectory(destinationDir);
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            closeStream(bis);
        }
        return fileInfo;
    }

    public void closeSocket(Socket socket) {
        try {
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void closeStream(InputStream inputStream) {
        try {
            if (inputStream != null) {
                inputStream.close();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void closeStream(OutputStream outputStream) {
        try {
            if (outputStream != null) {
                outputStream.close();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
