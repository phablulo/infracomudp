import java.util.Arrays;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class Ack {
  public int seq;
  public int checksum;
  public byte[] data;
  public byte[] mounted;

  // public static void main(String[] args) throws IOException {
  //   Ack a = new Ack(2);
  //   System.out.println(a.isValid());
  //   byte[] data = a.mounted;
  //
  //   Ack b = new Ack(data);
  //   System.out.println(b.isValid());
  // }
  public Ack(byte[] data) {
    this.checksum = util.bytesAsInt(Arrays.copyOfRange(data, 0, 4));
    this.data = Arrays.copyOfRange(data, 4, 8);
    this.seq = util.bytesAsInt(this.data);
    this.mounted = data;
  }
  public Ack(int seq) throws IOException {
    this.seq = seq;
    this.data = util.intAsBytes(seq);
    this.checksum = util.checksum(this.data);
    this.mounted = mount(this.data, util.intAsBytes(this.checksum));
  }

  public boolean isValid() {
    return util.checksum(this.data) == this.seq;
  }


  public static byte[] mount(int seq) throws IOException {
    return mount(util.intAsBytes(seq));
  }
  public static byte[] mount(byte[] seq) throws IOException {
    return mount(seq, util.intAsBytes(util.checksum(seq)));
  }
  public static byte[] mount(byte[] seq, byte[] checksum) throws IOException {
    ByteArrayOutputStream mounted = new ByteArrayOutputStream();
    mounted.write(checksum); // 4 bytes
    mounted.write(seq); // 4 bytes
    return mounted.toByteArray();
  }
}
