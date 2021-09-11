package client;

import net.Tlv;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;
import java.nio.file.Path;
import java.nio.file.Paths;

public class TCPClient {

    private String filePath;
    private String host;
    private int port;

    public static void main(String[] args) throws IOException {
        TCPClient client = new TCPClient();
        client.run();
    }

    TCPClient(){}

    private void run() throws IOException {
        TCPClient client = new TCPClient();
        client.setHost("127.0.0.1");
        client.setPort(9900);
        client.setFilePath("Events.log");
        SocketChannel socketChannel = client.CreateChannel(client.getHost(), client.getPort());
        client.sendFile(socketChannel, client.getFilePath());
    }

    private void sendFile(SocketChannel socketChannel, String filePath) throws IOException {

        Path path = Paths.get(filePath);
        FileChannel inChannel = FileChannel.open(path);
        Tlv tlv = new Tlv();

        ByteBuffer buffer = ByteBuffer.allocate(1016);
        while (inChannel.read(buffer) > 0) {
            buffer.flip();
            tlv.putBytesValue(200, buffer.array());

            byte[] serialized = tlv.serialize();
            System.out.println(serialized.length);
            socketChannel.write(ByteBuffer.wrap(serialized));

            buffer.clear();
        }
        socketChannel.close();
    }

    private SocketChannel CreateChannel(String host, int port) throws IOException {

        SocketChannel socketChannel = SocketChannel.open();
        socketChannel.configureBlocking(true);

        SocketAddress sockAddr = new InetSocketAddress(host, port);
        socketChannel.connect(sockAddr);
        System.out.println("Connected.. Now sending the file");
        return socketChannel;
    }

    public String getFilePath(){
        return filePath;
    }

    public void setFilePath(String filePath){
        this.filePath = filePath;
    }

    public int getPort(){
        return port;
    }

    public void setPort(int port){
        this.port = port;
    }

    public String getHost(){
        return host;
    }

    public void setHost(String host){
        this.host = host;
    }
}
