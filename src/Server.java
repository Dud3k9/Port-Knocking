import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.*;

public class Server {

//porty w argumentach metody main
    public static void main(String[] args) throws IOException, InterruptedException {
        ArrayList<Integer> ports = new ArrayList<Integer>();
        //zczytanie portów
        for (int i = 0; i < args.length; i++) {
            ports.add(Integer.parseInt(args[i]));
        }
        start(ports);
    }

    private static void start(List<Integer> ports2) throws IOException, InterruptedException {

        HashSet<Integer> a = new HashSet<Integer>(ports2);        //usuniecie powtarzajacych sie portów
        final ArrayList<Integer> ports = new ArrayList<Integer>(a);

        @SuppressWarnings("unchecked")
        ArrayList<Packet>[] packets = new ArrayList[ports.size()];  //lista pakietów na każdy otwarty port
        for (int i = 0; i < packets.length; i++)
            packets[i] = new ArrayList<Packet>();

        for (int i = 0; i < ports.size(); i++) {
            final int[] finalI = {i};

            new Thread(() -> {
                try {
                    DatagramSocket socket = new DatagramSocket((Integer) ports.get(finalI[0]));
                    DatagramPacket packet = new DatagramPacket(new byte[1460], 1460);
                    while (true) {
                        socket.receive(packet);   //odbieranie pakietów
                        synchronized (packets) {    //zapisywanie pakietów do list
                            packets[finalI[0]].add(new Packet(packet.getAddress(), packet.getPort(), packet.getData()));
                        }
                        packet = new DatagramPacket(new byte[1460], 1460);

                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
        }

    int j = 0;
    int i = 0;
    HashMap<String, Host> hostHashMap = new HashMap<String, Host>();  //mapa hostów

    while (packets[0].isEmpty()) {    //czekanie na skończenie wątków odbierajacych pakiety
        Thread.sleep(100);
    }

    while (packets[0].size() > j) {
        for (i = 0; i < ports.size(); i++) {
            if (!packets[i].isEmpty() && packets[i].size() > j) {
                log(i + ":" + String.valueOf(((char) packets[i].get(j).getData()[0])));
                //jezeli adres nadawcy pakietu sie juz pojawia dopisuje wiadomosc do mapy hostu
                if (hostHashMap.containsKey(packets[i].get(j).getAddress().toString() + packets[i].get(j).getPort())) {
                    hostHashMap.get(packets[i].get(j).getAddress().toString() + packets[i].get(j).getPort())
                            .appendMessage((char) packets[i].get(j).getData()[0]);
                    //jesli nie, tworz nowego hasta w mapie
                } else {
                    Host host = new Host(packets[i].get(j).getAddress(), packets[i].get(j).getPort());
                    host.appendMessage((char) packets[i].get(j).getData()[0]);
                    hostHashMap.put(packets[i].get(j).getAddress().toString() + packets[i].get(j).getPort(), host);
                }

                hostHashMap.forEach(((s, host) -> {     //jesli kturys host w mapie ma widomosc "CONNECT" otwiera comunicate w nowym watku
                    if (host.getMessage().equals("CONNECT")) {
                        log(host.getAddress().toString());
                        new Thread(new Comunicate(host.getAddress())).start();
                    }
                }));

            }
        }
        j++;
    }
}


    public static void log(String msg) {
        System.out.println("[SERVER]: " + msg);
    }
}