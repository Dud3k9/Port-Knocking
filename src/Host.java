import java.net.InetAddress;


public class Host {
    private InetAddress address;
    private int port;
    private StringBuilder message;

    public Host(InetAddress address, int port) {
        this.address = address;
        this.port = port;
        this.message = new StringBuilder();
    }

    public InetAddress getAddress() {
        return address;
    }

//    public void setAddress(InetAddress address) {
//        this.address = address;
//    }
//
//    public int getPort() {
//        return port;
//    }

//    public void setPort(int port) {
//        this.port = port;
//    }

    public void appendMessage(char c){
        message.append(c);
    }

//    public int getMessageLenght(){
//        return message.toString().length();
//    }

    public String getMessage(){
        return message.toString();
    }

}
