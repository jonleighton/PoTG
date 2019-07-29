import java.net.SocketException;

class Output extends LXDatagramOutput {
  // There are 512 channels per universe. The PixLite rounds this down to 510
  // to avoid splitting a single RGB pixel across universes. Therefore 170 max
  // pixels per universe.
  //
  // For simplicity, we are mapping one Art-Net universe to one PixLite output.
  // (But the PixLite can support more than 170 pixels per output in reality.)
  final static int PIXELS_PER_UNIVERSE = 170;

  Output(LX lx) throws SocketException {
    super(lx);

    int[] indices = { 0 };
    addDatagram(new ArtNetDatagram(indices, PIXELS_PER_UNIVERSE, 0));

    int[] indices2 = { 1 };
    addDatagram(new ArtNetDatagram(indices2, PIXELS_PER_UNIVERSE, 1));
  }
}
