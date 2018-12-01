import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.SocketException;

import java.math.BigInteger;

import java.io.IOException;
import java.nio.ByteBuffer;

public class Server {
  public static DatagramSocket socket;
  public static InetAddress address;
  public static int port; // client port

  public static void main(String[] args) {
    try {
      socket = new DatagramSocket(4445);
    }
    catch (SocketException e) {
      System.err.println("Erro ao escutar na porta 4445: "+e.toString());
      System.err.println("Outro programa está ouvindo na mesma porta?");
      System.exit(1);
    }
    byte[] buf = new byte[4];
    while (true) {
      DatagramPacket packet = new DatagramPacket(buf, buf.length);
      try {
        socket.receive(packet);
      }
      catch (IOException e) {
        System.err.println("Erro ao receber pacote: "+e.toString());
        e.printStackTrace();
        System.exit(1);
      }

      address = packet.getAddress();
      port = packet.getPort();
      packet = new DatagramPacket(buf, buf.length, address, port);
      int window_size = (int)(new BigInteger(packet.getData()).intValue());
      try {
        sendFile(window_size);
      }
      catch (IOException e) {
        System.err.println("Erro ao enviar pacote: "+e.toString());
        e.printStackTrace();
        System.exit(1);
      }
      break;
    }
  }
  public static void sendFile(int window_size) throws IOException {
    byte[] response = "Teste testando".getBytes();
    DatagramPacket pkt = new DatagramPacket(response, response.length, address, port);
    socket.send(pkt);
  }
}
