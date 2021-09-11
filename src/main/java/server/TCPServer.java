package server;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.EnumSet;

public class TCPServer {

    private static final ByteOrder DEFAULT_BYTE_ORDER = ByteOrder.LITTLE_ENDIAN;
    private String fileDir;
    private int port;

    public static void main(String[] args) throws IOException {
        TCPServer server = new TCPServer();
        server.run();
    }

    TCPServer() { }

    private void runWithCmd() throws IOException {
        SocketChannel socketChannel = createServerSocketChannel(port);
        if (!checkExistedFile(fileDir))
            readFileFromSocketChannel(socketChannel, fileDir);
        else {
            System.out.println("File existed!!");
        }
    }

    private void run() throws IOException {
        TCPServer server = new TCPServer();
        server.setFileDir("server/Events.log");
        server.setPort(9900);
        SocketChannel socketChannel = server.createServerSocketChannel(server.getPort());
        server.readFileFromSocketChannel(socketChannel, server.getFileDir());
    }

    private void readFileFromSocketChannel(SocketChannel socketChannel, String fileDir) throws IOException {
        Path path = Paths.get(fileDir);
        FileChannel fileChannel = FileChannel.open(path,
                EnumSet.of(StandardOpenOption.CREATE,
                        StandardOpenOption.TRUNCATE_EXISTING,
                        StandardOpenOption.WRITE)
        );

        ByteBuffer buffer = ByteBuffer.allocate(1024);
        while (socketChannel.read(buffer) > 0) {
            buffer.flip();
            byte[] data = buffer.array();
            fileChannel.write(ByteBuffer.wrap(onReceive(data, buffer)));
            buffer.clear();
        }

        fileChannel.close();
        System.out.println("Receiving file successfully!");
        socketChannel.close();
    }

    public byte[] onReceive(byte[] data, ByteBuffer socketBuffer) {
        int offset = 0;
        int len;
        while ((len = Math.min(data.length - offset, socketBuffer.remaining())) > 0) {
            socketBuffer.put(data, offset, len);
            offset += len;

            //System.out.println("Socket position: " + socketBuffer.position());
            //Kiem tra dieu kien
            if (socketBuffer.position() < 8) {
                //return de doc tiep du lieu tu socket
                return null;
            }

            socketBuffer.flip();
            //Doc message type va message length
            int pos = 0;
            System.out.println(socketBuffer.remaining());
            while (socketBuffer.remaining() >= 8) {
                socketBuffer.mark();
                int type = ByteBuffer.wrap(socketBuffer.array(), pos, 4).order(DEFAULT_BYTE_ORDER).getInt();
                pos += 4;
                //System.out.println("type: " + type);
                int length = ByteBuffer.wrap(socketBuffer.array(), pos, 4).order(DEFAULT_BYTE_ORDER).getInt();
                pos += 4;
                //System.out.println("length: " + length);
                if (socketBuffer.remaining() < length) {
                    socketBuffer.reset();
                    //compact de pos = limit,limit = capacity
                    socketBuffer.compact();
                    return null;
                } else {
                    //doc full du lieu tu message
                    byte[] value = new byte[length];
                    System.arraycopy(socketBuffer.array(), pos, value, 0, length);
                    //System.out.println(new String(value));
                    return value;
                }
            }

            socketBuffer.compact();
        }

        return null;
    }

    private SocketChannel createServerSocketChannel(int port) throws IOException {
        ServerSocketChannel serverSocket;
        SocketChannel client;
        serverSocket = ServerSocketChannel.open();
        serverSocket.socket().bind(new InetSocketAddress(port));
        client = serverSocket.accept();

        System.out.println("connection established .." + client.getRemoteAddress());
        return client;
    }

    private boolean checkExistedFile(String fileDir) {
        File tmpDir = new File(fileDir);
        return tmpDir.exists();
    }

    public String getFileDir() {
        return fileDir;
    }

    public void setFileDir(String fileDir) {
        this.fileDir = fileDir;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

}
