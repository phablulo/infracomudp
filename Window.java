class Window {
  public int maxSize;
  public boolean[] acks;
  public boolean[] sent;
  public int pointer;

  public Window (int maxSize) {
    this.maxSize = maxSize;
    this.acks = new boolean[maxSize];
    this.sent = new boolean[maxSize];
    this.pointer = 0;
  }

  public static void main(String[] args) {
    Window w = new Window(10);
    w.setSentAt(2);
    System.out.println(w.sentAt(2));
    w.movePointerBy(2);
    System.out.println(w.sentAt(0));
  }


  public int calculateNumSeq(int i) {
    return (i + this.pointer) % this.maxSize;
  }
  public boolean ackAt(int index) {
    return this.acks[ index % this.maxSize ];
  }
  public boolean sentAt(int index) {
    return this.sent[ index % this.maxSize ];
  }
  public void setAckAt(int index) {
    this.acks[ index % this.maxSize ] = true;
  }
  public void setSentAt(int index) {
    this.sent[ index % this.maxSize ] = true;
  }

  public void movePointerBy(int quantity) {
    for (int i = 0; i < quantity; ++i) {
      this.acks[ (this.pointer + i) % this.maxSize ] = false;
      this.sent[ (this.pointer + i) % this.maxSize ] = false;
    }
    this.pointer = (this.pointer + quantity) % this.maxSize;
  }
  public boolean isFull() {
    for (int i = 0; i < this.maxSize; ++i) {
      if (!this.acks[i]) return false;
    }
    return true;
  }
  public void clear() {
    for (int i = 0; i < this.maxSize; ++i) {
      this.acks[ i ] = false;
      this.sent[ i ] = false;
    }
    this.pointer = 0;
  }
  public void clear(int i, int j) {
    j = j % this.maxSize;
    int k = i;
    while (true) {
      int km = k % this.maxSize;
      this.sent[km] = false;
      this.acks[km] = false;
      if (km == j) {
        break;
      }
    }
  }
}
