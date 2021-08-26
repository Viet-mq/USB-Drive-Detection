import usb.UsbManager;

public class Main {
    public static void main(String[] args) {
        UsbManager usbManager = new UsbManager();
        ClientServerManager clientServerManager = new ClientServerManager();
        usbManager.runDetect();
//        try {
//            clientServerManager.run();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
    }
}
