import java.io.File;
import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;


public class Client {
    static String msg;

    //Argumenty:wiadomość,ip servera,porty...
    public static void main(String[] args) throws IOException, InterruptedException {
        msg = args[0];
        int[] ports = new int[args.length - 2];
        for (int i = 2; i < args.length; i++) {
            ports[i - 2] = Integer.parseInt(args[i]);
        }

        start(InetAddress.getByName(args[1]), ports);
    }


    public static void start(InetAddress adres, int... ports) throws IOException, InterruptedException {
///////////////////////////////////////////////////////////////Wysylanie sekwencji
        DatagramSocket socket = new DatagramSocket();

        DatagramPacket packet;
        int p = 0;
        for (int i = 0; i < msg.length(); i++) {
            String tosend = msg.substring(i, i + 1);
            packet = new DatagramPacket(tosend.getBytes(), tosend.length(), adres, ports[p]);
            socket.send(packet);
            if (p < ports.length - 2)
                p++;
            else {
                p = 0;
            }
        }
//////////////////////////////////////////////////////////////Odbieranie wiadomosci z nr. portu
        socket = new DatagramSocket(1024);
        packet = new DatagramPacket(new byte[1460], 1460);
        socket.setSoTimeout(10000);
        socket.receive(packet);
//            System.out.println(packet.getAddress());
        StringBuilder portString = new StringBuilder();
        for (int i = 0; i < packet.getLength(); i++)
            portString.append((char) packet.getData()[i]);
        int port = Integer.valueOf(portString.toString());
        System.out.println(port);
//////////////////////////////////////////////////////////////Wczytanie pliku do wyslania
        File f = new File("C:\\skj2019dzienne\\film.mpg");
        FileChannel channel = FileChannel.open(f.toPath());
        ByteBuffer buffer = ByteBuffer.allocate((int) f.length());
        channel.read(buffer);
        ArrayList<byte[]> tabs = divBuf(buffer.array());
//////////////////////////////////////////////////////////////Wysłanie pliku na odebrany port
        socket = new DatagramSocket();
        packet = new DatagramPacket(f.getName().getBytes(), f.getName().getBytes().length, adres, port);
        socket.send(packet);
        //dlugosc
        packet = new DatagramPacket((String.valueOf(buffer.array().length)).getBytes(), String.valueOf(buffer.array().length).getBytes().length, adres, port);
        socket.send(packet);

        for (int i = 0; i < tabs.size(); i++) {//wysyłanie pliku
            packet = new DatagramPacket(tabs.get(i), tabs.get(i).length, adres, port);
            socket.send(packet);
            Thread.sleep(1);
        }
    }

    static public ArrayList<byte[]> divBuf(byte[] tab) { //Dzielenie tablicy bitów na mniejsze
        int a = 0, b = 0;
        byte[] tmp;
        ArrayList<byte[]> tabs = new ArrayList<>();

        while (true) {
            if (tab.length > a + 1460)
                b = a + 1460;
            else {
                b = tab.length;
                tmp = new byte[b - a];
                for (int i = a, j = 0; i < b; i++, j++)
                    tmp[j] = tab[i];
                tabs.add(tmp);
                return tabs;
            }

            tmp = new byte[b - a];
            for (int i = a, j = 0; i < b; i++, j++)
                tmp[j] = tab[i];
            tabs.add(tmp);

            a = b;
        }
    }

    public static void log(String msg) {
        System.out.println("[CLIENT]: " + msg);
    }
}
