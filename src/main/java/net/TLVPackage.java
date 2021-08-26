package net;

public class TLVPackage {

    int type = 0;
    int length = 0;
    byte[] value = null;

    public TLVPackage(){

    }

    public TLVPackage(int type, int length, byte[] value){
        this.type = type;
        this.length = length;
        this.value = value;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public byte[] getValue() {
        return value;
    }

    public void setValue(byte[] value) {
        this.value = value;
    }

    @Override
    public String toString(){
        return "type = [" + this.type + "]," + "length = [" + this.length + "]," + "value = [" + new String(this.value) + "]";
    }
}

