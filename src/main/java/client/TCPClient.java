package client;

import java.net.Socket;
import java.util.concurrent.TimeUnit;

import net.TLVPackage;
import net.TLV;

public class TCPClient implements Runnable {
    private TLV tlv = null;
    Socket s = null;

    public static void main(String[] args) {
        try {
            TCPClient c = new TCPClient();
            Thread t = new Thread(c.new ShutdownHook(), "ShutdownHook-Thread");
            Runtime.getRuntime().addShutdownHook(t);

            Socket s = new Socket("127.0.0.1", 9900);

            c.tlv = new TLV(s);
            new Thread(c).start();
            //c.tlv.sendImagesFile("images\\5.png");
            c.tlv.sendLogFile("Events.log");
//            for (int i = 0; i < 10; i++) {
//                String str = i + "_" + "Test!!!";
//                c.tlv.writeMsg(str, 200);
//                Thread.sleep(1000);
//            }
//
//            c.tlv.writeMsg("Done", 201);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void initializeTCPClient(){
        try {
            TCPClient c = new TCPClient();
            Thread t = new Thread(c.new ShutdownHook(), "ShutdownHook-Thread");
            Runtime.getRuntime().addShutdownHook(t);

            Socket s = new Socket("127.0.0.1", 9900);

            c.tlv = new TLV(s);
            new Thread(c).start();
            //c.tlv.sendImagesFile("images\\5.png");
            c.tlv.sendLogFile("Events.log");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        TLVPackage msg = null;
        while (true) {
            msg = tlv.readMsg();
            System.out.println("Read a msg:" + (new String(msg.getValue())));
        }

    }

    class ShutdownHook implements Runnable {// Safe exit method
        @Override
        public void run() {
            System.out.println("ShutdownHook execute start...");
            try {
                tlv.close();
                TimeUnit.SECONDS.sleep (10); // Simulate the processing operation before the application process exits
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("ShutdownHook execute end...");
        }
    }
}
