import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import java.io.IOException;
import java.nio.ByteBuffer;

public class Client {
  public static DatagramSocket socket;
  public static InetAddress address;
  public static final int port = 4445; // server port

  public static void main(String[] args) {
    try {
      socket = new DatagramSocket();
    }
    catch (SocketException e) {
      System.err.println("Erro ao criar socket: "+e.toString());
      System.exit(1);
    }
    try {
      address = InetAddress.getByName("localhost");
    }
    catch (UnknownHostException e) {
      System.err.println("Erro ao criar escutar em 'localhost': "+e.toString());
      System.exit(1);
    }
    byte[] buf = ByteBuffer.allocate(4).putInt(50).array(); // tamanho da janela
    DatagramPacket pkt = new DatagramPacket(buf, buf.length, address, port);
    try {
      socket.send(pkt);
    }
    catch (IOException e) {
      System.err.println("Erro ao enviar pacote: "+e.toString());
      System.exit(1);
    }
    try {
      receiveFile();
    }
    catch (IOException e) {
      System.err.println("Erro ao receber pacote: "+e.toString());
      System.exit(1);
    }
  }
  public static void receiveFile() throws IOException {
    byte[] buffer = new byte[256];
    DatagramPacket pkt = new DatagramPacket(buffer, buffer.length, address, port);
    socket.receive(pkt);
    String received = new String(pkt.getData(), 0, pkt.getLength());
    System.out.println("Resposta: "+received);
  }
}
