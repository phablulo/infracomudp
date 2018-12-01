import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.FileOutputStream;

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
    byte[] buf = util.intAsBytes(50); // tamanho da janela
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
    ByteArrayOutputStream file = new ByteArrayOutputStream();

    int i = 0;
    int count = 0;
    while (true) {
      byte[] buffer = new byte[256];
      DatagramPacket pkt = new DatagramPacket(buffer, buffer.length, address, port);
      socket.receive(pkt);
      Packet packet = new Packet(util.getDatagramData(pkt));
      if (packet.isValid()) {
        System.out.println("["+(++i)+"] Pacote válido de tamanho "+packet.data.length+" recebido");
        sendAck(packet.seq);
        if (packet.data.length == 1 && packet.data[0] == 0) {
          System.out.println("Transmissão acabou. -- "+count);
          try (OutputStream fileStream = new FileOutputStream("recebido.zip")) {
            file.writeTo(fileStream);
          }
          break;
        }
        else {
          count += packet.data.length;
          file.write(packet.data);
        }
      }
      else {
        System.err.println("Pacote inválido recebido "+packet.checksum);
      }
    }
  }
  public static void sendAck(int seq) {
    try {
      Ack ack = new Ack(seq);
      DatagramPacket pkt = new DatagramPacket(ack.mounted, ack.mounted.length, address, port);
      socket.send(pkt);
      System.out.println("Ack com número de sequência "+seq+" enviado");
    }
    catch (IOException e) {
      System.err.println("Erro ao enviar ack com número de sequência "+seq+": "+e.toString());
      e.printStackTrace();
    }
  }
}
