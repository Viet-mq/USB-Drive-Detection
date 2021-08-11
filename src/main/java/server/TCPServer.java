package server;

import usb.FileInfo;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPServer extends Thread {

    private ServerSocket serverSocket;
    private final int port = 9900;

    public static void main(String[] args) {
        TCPServer tcpServer = new TCPServer();
        tcpServer.openTCPServer();
    }

    public void openTCPServer(){
        open();
        start();
    }

    public void open() {
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("TCP server is opened on port " + port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        while (true) {
            Socket server = null;
            DataInputStream inFromClient = null;
            ObjectInputStream ois = null;
            ObjectOutputStream oos = null;

            try {
                // accept connect from client and create Socket object
                server = serverSocket.accept();
                System.out.println("connected to " + server.getRemoteSocketAddress());

                // get greeting from client
                inFromClient = new DataInputStream(server.getInputStream());
                System.out.println(inFromClient.readUTF());

                // receive file info
                ois = new ObjectInputStream(server.getInputStream());
                FileInfo fileInfo = (FileInfo) ois.readObject();
                if (fileInfo != null) {
                    createFile(fileInfo);
                }

                // confirm that file is received
                oos = new ObjectOutputStream(server.getOutputStream());
                fileInfo.setStatus("success");
                fileInfo.setDataBytes(null);
                oos.writeObject(fileInfo);

            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            } finally {
                closeStream(ois);
                closeStream(oos);
                closeStream(inFromClient);
                closeSocket(server);
            }
        }
    }

    private void createFile(FileInfo fileInfo) {
        BufferedOutputStream bos = null;

        try {
            if (fileInfo != null) {
                File fileReceive = new File(fileInfo.getDestinationDirectory() + fileInfo.getFilename());
                bos = new BufferedOutputStream(new FileOutputStream(fileReceive));
                // write file content
                bos.write(fileInfo.getDataBytes());
                bos.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeStream(bos);
        }
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