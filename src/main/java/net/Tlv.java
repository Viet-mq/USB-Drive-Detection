package net;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.HashMap;
import java.util.Set;

public class Tlv {

    private static final ByteOrder DEFAULT_BYTE_ORDER = ByteOrder.LITTLE_ENDIAN;

    private HashMap<Integer, byte[]> mObjects;
    private int mTotalBytes = 0;

    public Tlv() {
        mObjects = new HashMap<Integer, byte[]>();
    }

    public static Tlv parse(byte[] buffer, int offset, int length) {

        Tlv box = new Tlv();

        int parsed = 0;
        while (parsed < length) {
            int type = ByteBuffer.wrap(buffer,offset + parsed, 4).order(DEFAULT_BYTE_ORDER).getInt();
            parsed += 4;
            int size = ByteBuffer.wrap(buffer,offset + parsed, 4).order(DEFAULT_BYTE_ORDER).getInt();
            parsed += 4;
            byte[] value = new byte[size];
            System.arraycopy(buffer, offset+parsed, value, 0, size);
            box.putBytesValue(type, value);
            parsed += size;
        }
        return box;
    }

    public byte[] serialize() {
        int offset = 0;
        byte[] result = new byte[mTotalBytes];
        Set<Integer> keys = mObjects.keySet();
        for (Integer key : keys) {
            byte[] bytes = mObjects.get(key);
            byte[] type   = ByteBuffer.allocate(4).order(DEFAULT_BYTE_ORDER).putInt(key).array();
            byte[] length = ByteBuffer.allocate(4).order(DEFAULT_BYTE_ORDER).putInt(bytes.length).array();
            System.arraycopy(type, 0, result, offset, type.length);
            offset += 4;
            System.arraycopy(length, 0, result, offset, length.length);
            offset += 4;
            System.arraycopy(bytes, 0, result, offset, bytes.length);
            offset += bytes.length;
        }
        return result;
    }

    public void putByteValue(int type, byte value) {
        byte[] bytes = new byte[1];
        bytes[0] = value;
        putBytesValue(type, bytes);
    }

    public void putStringValue(int type, String value) {
        putBytesValue(type, value.getBytes());
    }

    public void putObjectValue(int type, Tlv value) {
        putBytesValue(type, value.serialize());
    }

    public void putBytesValue(int type, byte[] value) {
        mObjects.put(type, value);
        mTotalBytes += value.length + 8;
    }

    public Byte getByteValue(int type) {
        byte[] bytes = mObjects.get(type);
        if (bytes == null) {
            return null;
        }
        return bytes[0];
    }

    public Tlv getObjectValue(int type) {
        byte[] bytes = mObjects.get(type);
        if (bytes == null) {
            return null;
        }
        return Tlv.parse(bytes, 0, bytes.length);
    }

    public String getStringValue(int type) {
        byte[] bytes = mObjects.get(type);
        if (bytes == null) {
            return null;
        }
        return new String(bytes).trim();
    }

    public byte[] getBytesValue(int type) {
        byte[] bytes = mObjects.get(type);
        return bytes;
    }
}
