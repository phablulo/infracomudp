import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.nio.file.Files;
import java.io.IOException;
import java.lang.Math;
import java.util.Arrays;
import java.net.DatagramPacket;

public class util {
  public static int checksum(byte[] data) {
    int checksum = 0;
    for (int i = 0, j = data.length; i < j; ++i) {
      checksum += (int)data[i];
    }
    return checksum;
  }
  public static byte[] intAsBytes(int number) {
    return ByteBuffer.allocate(4).putInt(number).array();
  }
  public static int bytesAsInt(byte[] bytes) {
    return (int)(new BigInteger(bytes).intValue());
  }
  public static byte[][] getFileBytes(String filename, int dataPerPacket) throws IOException {
    Path location = Paths.get(filename);
    byte[] bytes = Files.readAllBytes(location);
    int nPartes = (int)Math.ceil(bytes.length / (double)dataPerPacket);
    byte[][] partes = new byte[nPartes+1][];

    for (int i = 0; i < nPartes; ++i) {
      int start = i*dataPerPacket;
      int end = Math.min(start + dataPerPacket, bytes.length);
      partes[i] = Arrays.copyOfRange(bytes, start, end);
    }
    partes[nPartes] = new byte[1];
    partes[nPartes][0] = 0; // null pra indicar que acabou
    return partes;
  }
  public static byte[] getDatagramData(DatagramPacket packet) {
    return Arrays.copyOfRange(packet.getData(), 0, packet.getLength());
  }
}
