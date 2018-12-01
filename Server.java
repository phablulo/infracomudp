import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.SocketException;

import java.io.IOException;

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
      int window_size = util.bytesAsInt(packet.getData());
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
    byte[] response;
    for (int i = 0; i < window_size; ++i) {
      response = Packet.mount(("Teste testando "+i).getBytes(), i);
      DatagramPacket pkt = new DatagramPacket(response, response.length, address, port);
      socket.send(pkt);
      Packet p = new Packet(response);
    }
    response = new byte[1];
    response[0] = 0;
    DatagramPacket pkt = new DatagramPacket(response, response.length, address, port);
    socket.send(pkt);

    while (true) {
      int seq = -1;
      try {
        seq = waitAck();
      }
      catch (IOException e) {
        System.err.println("Erro ao receber pacote: "+e.toString());
        e.printStackTrace();
        System.exit(1);
      }
      if (seq == -1) continue;
    }
  }
  public static int waitAck() throws IOException {
    byte[] buffer = new byte[8]; // [...checksum, ...seq]
    DatagramPacket pkt = new DatagramPacket(buffer, buffer.length);
    socket.receive(pkt);
    Ack ack = new Ack(pkt.getData());
    if (ack.isValid()) {
      System.out.println("[OK] Ack recebido com número de sequência "+ack.seq);
      return ack.seq;
    }
    else {
      System.err.println("[FAIL] Ack inválido recebido com número de sequência "+ack.seq);
    }
    return -1;
  }
}
