import java.net.InetAddress;

public class Packet {
    private InetAddress address;
    private int port;
    private byte[] data;

    public Packet(InetAddress address, int port, byte[] data) {
        this.address = address;
        this.port = port;
        this.data = data;
    }

    int lenght=0;
    public void appendData(byte[] tab,int dl){
        for(int i=lenght,j=0;j<tab.length&&i<dl;i++,j++)
            data[i]=tab[j];
        lenght+=tab.length;
    }

    public InetAddress getAddress() {
        return address;
    }

//    public void setAddress(InetAddress address) {
//        this.address = address;
//    }

    public int getPort() {
        return port;
    }

//    public void setPort(int port) {
//        this.port = port;
//    }

    public byte[] getData() {
        return data;
    }

//    public void setData(byte[] data) {
//        this.data = data;
//    }
}
