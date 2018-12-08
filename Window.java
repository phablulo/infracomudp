class Window {
  public int maxSize;
  public boolean[] acks;
  public boolean[] sent;

  public Window (int maxSize) {
    this.maxSize = maxSize;
    this.acks = new boolean[maxSize];
    this.sent = new boolean[maxSize];
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

  public void unsetSentAt(int index) {
    this.sent[ index % this.maxSize ] = false;
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
  }
  public void clear(int i, int j) {
    j = j % this.maxSize;
    int k = i;
    while (true) {
      int km = k++ % this.maxSize;
      this.sent[km] = false;
      this.acks[km] = false;
      if (km == j) {
        break;
      }
    }
  }
}
