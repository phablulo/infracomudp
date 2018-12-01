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
      System.out.println("Tamanho da janela escolhido: "+window_size);
      try {
        sendFile(window_size);
      }
      catch (IOException e) {
        System.err.println("Erro ao enviar pacote: "+e.toString());
        e.printStackTrace();
        System.exit(1);
      }
      System.err.println("Arquivo enviado");
      break;
    }
  }
  public static void sendFile(int window_size) throws IOException {
    byte[][] parts = util.getFileBytes("teste.zip", 256 - 8);
    int veryend = parts.length;
    int startWindow = 0;
    Window window = new Window(Math.min(window_size+1, parts.length));
    System.out.println(parts.length+" partes para enviar.");
    byte[] last = parts[parts.length-1];

    int _seq = 0;
    while (startWindow <= veryend) {
      int wsize = Math.min(veryend - startWindow, window_size);
      for (int i = 0; i < wsize; ++i) {
        if (window.sentAt(_seq)) continue;
        byte[] packet = Packet.mount(parts[startWindow + i], _seq);
        DatagramPacket pkt = new DatagramPacket(packet, packet.length, address, port);
        socket.send(pkt);
        System.out.println("Enviado com sequencia "+(_seq));
        window.setSentAt(_seq++);
      }
      int seq = waitAck();
      if (seq == -1) continue;
      window.setAckAt(seq);
      if (window.ackAt(startWindow)) { // tem ack no início da janela: move
        if (window.isFull()) {
          window.clear();
          startWindow += window_size;
        }
        else {
          int i;
          for (i = 0; i < window_size; ++i) {
            if (!window.ackAt(startWindow + i)) {
              window.clear(startWindow, startWindow + i - 1);
              startWindow += i;
              break;
            }
          }
        }
      }
    }
  }
  public static int waitAck() throws IOException {
    byte[] buffer = new byte[8]; // [...checksum, ...seq]
    DatagramPacket pkt = new DatagramPacket(buffer, buffer.length);
    socket.receive(pkt);
    Ack ack = new Ack(pkt.getData());
    if (ack.isValid()) {
      // System.out.println("[OK] Ack recebido com número de sequência "+ack.seq);
      return ack.seq;
    }
    else {
      System.err.println("[FAIL] Ack inválido recebido com número de sequência "+ack.seq);
    }
    return -1;
  }
}
