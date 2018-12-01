import java.util.Arrays;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class Packet {
  public int seq;
  public int checksum;
  public byte[] data;
  public byte[] mounted;

  // public static void main(String args[]) throws IOException {
  //   byte[] data = "String de teste aqui!!".getBytes();
  //   byte[] teste = mount(data, 1);
  //   Packet p = new Packet(teste);
  //
  //   System.out.println("É válido? "+p.isValid());
  //   System.out.println("Checksums: "+p.checksum+" "+util.checksum(p.data));
  // }

  public Packet(byte[] data) {
    this.seq = util.bytesAsInt(Arrays.copyOfRange(data, 0, 4));
    this.checksum = util.bytesAsInt(Arrays.copyOfRange(data, 4, 8));
    this.data = Arrays.copyOfRange(data, 8, data.length);

    byte[] c = Arrays.copyOfRange(data, 4, 8);

    this.mounted = data;
  }
  public Packet(byte[] data, int seq) throws IOException {
    byte[] mounted = mount(data, seq);

    this.seq = seq;
    this.data = data;
    this.checksum = checksum;
    this.mounted = mounted;
  }

  public boolean isValid() {
    return util.checksum(this.data) == this.checksum;
  }

  public static byte[] mount(byte[] data, int seq) throws IOException {
    return mount(data, seq, util.checksum(data));
  }
  public static byte[] mount(byte[] data, int seq, int checksum) throws IOException {
    ByteArrayOutputStream mounted = new ByteArrayOutputStream();
    byte[] seqbyte = util.intAsBytes(seq); // 4 bytes
    byte[] checkbyte = util.intAsBytes(checksum); // 4 bytes

    mounted.write(seqbyte);
    mounted.write(checkbyte);
    mounted.write(data);

    return mounted.toByteArray();
  }
}
