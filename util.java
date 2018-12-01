import java.nio.ByteBuffer;
import java.math.BigInteger;

public class util {
  public static int checksum(byte[] data) {
    int checksum = 0;
    for (int i = 0, j = data.length; i < j; ++i) {
      checksum += data[i];
    }
    return checksum;
  }
  public static byte[] intAsBytes(int number) {
    return ByteBuffer.allocate(4).putInt(number).array();
  }
  public static int bytesAsInt(byte[] bytes) {
    return (int)(new BigInteger(bytes).intValue());
  }
}
