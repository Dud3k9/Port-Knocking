
import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.OpenOption;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;

public class Comunicate implements Runnable {

    InetAddress address;
    String port;

    public Comunicate(InetAddress address) {
        //adres klienta
        this.address = address;
        //losowanie portu
        port = String.valueOf((int) ((Math.random() * 64512) + 1024));
    }

    @Override
    public void run() {
        try {
            DatagramSocket socket = new DatagramSocket();
            DatagramPacket packet = new DatagramPacket(port.getBytes(), port.length(), address, 1024);
            socket.send(packet);

            socket = new DatagramSocket(Integer.parseInt(port));
            packet=new DatagramPacket(new byte[1460],1460);
            socket.receive(packet);//odebranie nazwy pliku
            StringBuilder name=new StringBuilder();
            for(int i=0;i<packet.getLength();i++){
                name.append((char)packet.getData()[i]);
            }

            packet = new DatagramPacket(new byte[1460], 1460);
            socket.receive(packet);     //odebranie dlugosci przesylanego pliku
            StringBuilder dlString = new StringBuilder();
            for (int i = 0; i < packet.getLength(); i++)
                dlString.append((char) packet.getData()[i]);
            int dl = Integer.parseInt(dlString.toString()); //dlugosc pliku
            int count = dl / 1460;  //ilosc pakietów kture beda odebrane
            if (dl % 1460 > 0)
                count++;

            Packet file = new Packet(packet.getAddress(), packet.getPort(), new byte[dl]);

            for (int i = 0; i < count; i++) {
                packet = new DatagramPacket(new byte[1460], 1460);
                socket.receive(packet);//odbieranei pakietow
                file.appendData(packet.getData(),dl);//łączenie kawałków pliku
                System.out.println(count-i);
            }
            System.out.println(address.toString().replace('.','_').substring(1)+'_'+port+'_'+ LocalDateTime.now().getYear()+'_'+LocalDateTime.now().getMonthValue()+'_'+LocalDateTime.now().getDayOfMonth()+'_'+LocalDateTime.now().getHour()+'_'+LocalDateTime.now().getMinute()+'_'+LocalDateTime.now().getSecond()+'_'+name);
            FileChannel channel=FileChannel.open(Paths.get("C:\\skj2019dzienne\\odebrane\\"+address.toString().replace('.','_').substring(1)+'_'+port+'_'+ LocalDateTime.now().getYear()+'_'+LocalDateTime.now().getMonthValue()+'_'+LocalDateTime.now().getDayOfMonth()+'_'+LocalDateTime.now().getHour()+'_'+LocalDateTime.now().getMinute()+'_'+LocalDateTime.now().getSecond()+'_'+name), new OpenOption[] {
                StandardOpenOption.CREATE,StandardOpenOption.APPEND});
            ByteBuffer buffer=ByteBuffer.wrap(file.getData());
            channel.write(buffer);//zapis pliku


        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
