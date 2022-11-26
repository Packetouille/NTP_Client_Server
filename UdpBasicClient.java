import java.io.IOException;
import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Scanner;

public class UdpBasicClient {
    public static void main(String[] args) {
        try{
            Scanner scanner = new Scanner(System.in);
            DatagramSocket clientSocket = new DatagramSocket();

            byte[] buffer = new byte[1024];

            while (true){
                System.out.print("> ");

                String data = scanner.nextLine();
                if("exit".equalsIgnoreCase(data)){
                    break;
                }

                DatagramPacket packet = new DatagramPacket(data.getBytes(), data.getBytes().length, InetAddress.getLocalHost(), 20000);
                clientSocket.send(packet);
                DatagramPacket receivePacket = new DatagramPacket(buffer, buffer.length);

                clientSocket.receive(receivePacket);
                System.out.println("Echo back" + new String(receivePacket.getData()));
            }
        } catch (UnknownHostException e){
            e.printStackTrace();
        } catch (IOException e){
            e.printStackTrace();
        }
    }
}
