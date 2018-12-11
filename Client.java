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
import java.util.Random;

public class Client {
  public static DatagramSocket socket;
  public static InetAddress address;
  public static final int port = 4445; // server port
  public static int lossRate = 4;
  public static int window_size = 50;
  public static String serverIP = "localhost";

  public static void main(String[] args) {
    // configura a partir dos argumentos
    if (args.length > 0) {
      window_size = Integer.parseInt(args[0]);
      if (args.length > 1) {
        lossRate = Integer.parseInt(args[1]);
        if (args.length > 2) {
          serverIP = args[2];
        }
      }
    }
    System.out.println("---- Iniciando cliente -----");
    System.out.println("Certifique-se de que o servidor está aberto ANTES de abrir o cliente.");
    System.out.println("Uso: java Client (int)tamanho_janela (int)taxaPerda");
    System.out.println("Os argumentos são opcionais\n\n");

    System.out.println("Tamanho da janela: "+window_size);
    System.out.println("Taxa de perda: "+lossRate+'%');
    System.out.println("IP do servidor: "+serverIP);
    System.out.println("---- BEGIN -----\n\n\n");

    try {
      socket = new DatagramSocket();
    }
    catch (SocketException e) {
      System.err.println("Erro ao criar socket: "+e.toString());
      System.exit(1);
    }
    try {
      address = InetAddress.getByName(serverIP);
    }
    catch (UnknownHostException e) {
      System.err.println("Erro ao criar escutar em '"+serverIP+"': "+e.toString());
      System.exit(1);
    }
    byte[] buf = util.intAsBytes(window_size);
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
    Random generator = new Random();
    byte[][] window = new byte[window_size][];
    while (true) {
      byte[] buffer = new byte[256];
      DatagramPacket pkt = new DatagramPacket(buffer, buffer.length, address, port);
      socket.receive(pkt);
      if (generator.nextInt(99) < lossRate) continue;
      Packet packet = new Packet(util.getDatagramData(pkt));
      if (packet.isValid()) {
        clearPrint("["+(++i)+"] Pacote válido de tamanho "+packet.data.length+" e sequência "+packet.seq+" recebido");
        sendAck(packet.seq);
        boolean isLast = packet.data.length == 1 && packet.data[0] == 0;
        if (!isLast && window[ packet.seq % window_size ] == null) {
          window[ packet.seq % window_size ] = packet.data;
          count += packet.data.length;
        }
        if (isFull(window) || isLast) {
          for (int j = 0; j < window_size; ++j) {
            if (window[j] == null) continue;
            file.write(window[j]);
            window[j] = null;
          }
          if (isLast) {
            clearPrint("Transmissão acabou. Recebi "+count+" bits.");
            try (OutputStream fileStream = new FileOutputStream("recebido.zip")) {
              file.writeTo(fileStream);
            }
            break;
          }
        }
      }
      else {
        clearPrint("Pacote inválido recebido "+packet.checksum);
      }
    }
  }
  public static boolean isFull(byte[][] window) {
    int length = window.length;
    for (int i = 0; i < length; ++i) {
      if (window[i] == null) {
        return false;
      }
    }
    return true;
  }
  public static void clearPrint(String msg) {
    System.out.print(String.format("\033[%dA",1)); // Move up
    System.out.print("\033[2K"); // Erase line content
    System.out.println(msg);
  }
  public static void sendAck(int seq) {
    try {
      Ack ack = new Ack(seq);
      DatagramPacket pkt = new DatagramPacket(ack.mounted, ack.mounted.length, address, port);
      socket.send(pkt);
      clearPrint("Ack com número de sequência "+seq+" enviado");
    }
    catch (IOException e) {
      System.err.println("Erro ao enviar ack com número de sequência "+seq+": "+e.toString());
      e.printStackTrace();
    }
  }
}
