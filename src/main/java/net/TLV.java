// tcp data writing and reading processing
package net;

import java.io.*;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;

public class TLV {

    private final String LOGFILE_DESTINATION = "server/Events.log";

    ImageEncryption imageEncryption = new ImageEncryption();
    DataInputStream dis = null;
    DataOutputStream dos = null;
    FileOutputStream outputStream;
    Socket s;

    {
        try {
            outputStream = new FileOutputStream(LOGFILE_DESTINATION);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public TLV(Socket s) {
        this.s = s;
        try {
            dos = new DataOutputStream(s.getOutputStream());
            dis = new DataInputStream(s.getInputStream());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void sendLogFile(String filePath)  {
        BufferedReader bufferedReader;
        try {
            bufferedReader = new BufferedReader(new FileReader(filePath));
            String line = bufferedReader.readLine();
            while (line != null) {
                writeMsg(line + "\n", 200);
                line = bufferedReader.readLine();
            }

            writeMsg("", 201);
            bufferedReader.close();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public TLVPackage getLogFile(){
        byte[] type = new byte[4];
        byte[] length = new byte[4];

        try{
            TLVPackage tlvPackage = new TLVPackage();
            dis.readFully(type);
            tlvPackage.setType(LITTLEENDIAN(type));

            if(tlvPackage.getType() == 200 || tlvPackage.getType() == 201){
                dis.readFully(length);
                tlvPackage.setLength(LITTLEENDIAN(length));

                byte[] value = new byte[tlvPackage.getLength()];
                dis.readFully(value);

                tlvPackage.setValue(value);
                outputStream.write(value);

                if(tlvPackage.getType() == 201){
                    System.out.println("Read file successfully");
                }

                return tlvPackage;
            } else {
                return null;
            }

        } catch (IOException e){
            e.printStackTrace();
        }

        return null;
    }

    public void sendImagesFile(String filePath){
        String imageString = imageEncryption.encoder(filePath);
        System.out.println(imageString.length());
        writeMsg(imageString, 200);
        writeMsg("", 201);
    }

    public TLVPackage getImageFile(String fileDestination){

        byte[] type = new byte[4];
        byte[] length = new byte[4];

        try{
            TLVPackage tlvPackage = new TLVPackage();
            dis.readFully(type);
            tlvPackage.setType(LITTLEENDIAN(type));

            if(tlvPackage.getType() == 200 || tlvPackage.getType() == 201){
                dis.readFully(length);
                tlvPackage.setLength(LITTLEENDIAN(length));

                byte[] value = new byte[tlvPackage.getLength()];
                dis.readFully(value);

                tlvPackage.setValue(value);
                if(tlvPackage.getType() == 201){
                    System.out.println("Get file successfully");
                } else
                    imageEncryption.decoder(new String(tlvPackage.getValue()), fileDestination);

                return tlvPackage;
            } else {
                return null;
            }

        } catch (IOException e){
            e.printStackTrace();
        }

        return null;
    }

    public void writeMsg(String msg, int type) {

        byte[] MsgBuf = msg.getBytes(StandardCharsets.UTF_8);
        int len = MsgBuf.length;

        byte[] by = new byte[4];
        ByteBuffer byteBuffer = ByteBuffer.wrap(by);
        byteBuffer.order (ByteOrder.LITTLE_ENDIAN);

        byte[] SendMsg = new byte[4 + 4 + len];
        int i = 0;

        // Write type
        byteBuffer.asIntBuffer().put(type);
        for (i = 0; i < by.length; i++) {
            SendMsg[i] = by[i];
        }
        byteBuffer.asIntBuffer (). put(0);

        // write length
        byteBuffer.asIntBuffer().put(len);
        int j = i;
        for (int k = 0; k < by.length; j++, k++) {
            SendMsg[j] = by[k];
        }

        // write value
        int n = j;
        for (int k = 0; k < MsgBuf.length; k++, n++) {
            SendMsg[n] = MsgBuf[k];
        }
        try {
            dos.write(SendMsg);
            dos.flush();
        } catch (Exception e) {
            System.out.println ("Error writing message");
            e.printStackTrace();
        }

    }

    public TLVPackage readMsg() {

        byte[] type = new byte[4];
        byte[] length = new byte[4];

        try {
            TLVPackage msg = new TLVPackage();
            dis.readFully(type);
            msg.setType(LITTLEENDIAN(type));
            //System.out.println("Type: " + msg.getType());

            dis.readFully(length);
            msg.setLength(LITTLEENDIAN(length));
            //System.out.println("Len: " + msg.len());

            byte[] value = new byte[msg.getLength()];
            dis.readFully(value);
            msg.setValue(value);
            //System.out.println ("Value:" + (new String (value)));
            return msg;

        } catch (IOException e) {
            System.out.println ("Error reading message");
            e.printStackTrace();
        }

        return null;
    }

    public static int LITTLEENDIAN(byte[] b) {
        int t = 0;
        if (b.length != 4) {
            return 0;
        }
        t = 0xff & b[0];
        t = t | (0xff & b[1]) << 8;
        t = t | (0xff & b[2]) << 16;
        t = t | (0xff & b[3]) << 24;
        return t;
    }

    public static int BIGENDIAN(byte[] b) {
        int t = 0;

        if (b.length != 4) {
            return 0;
        }
        t = 0xff & b[0] << 24;
        t = t | (0xff & b[1]) << 16;
        t = t | (0xff & b[2]) << 8;
        t = t | (0xff & b[3]);
        return t;
    }

    public void close() {
        try {
            if (dis != null) {
                dis.close();
            }
            if (dos != null) {
                dos.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
