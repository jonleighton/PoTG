import java.net.SocketException;
import java.net.UnknownHostException;

import heronarts.lx.LX;
import heronarts.lx.output.LXDatagramOutput;
import heronarts.lx.output.ArtNetDatagram;

class Output extends LXDatagramOutput {
  // There are 512 channels per universe. The PixLite rounds this down to 510
  // to avoid splitting a single RGB pixel across universes. Therefore 170 max
  // pixels per universe.
  //
  // For simplicity, we are mapping one Art-Net universe to one PixLite output.
  // (But the PixLite can support more than 170 pixels per output in reality.)
  final static int PIXELS_PER_UNIVERSE = 170;

  final static String CONTROLLER_IP = "192.168.1.100";

  Output(LX lx) throws SocketException, UnknownHostException {
    super(lx);

    int[] indices = new int[50];
    for (int i = 0; i < 50; i++) {
      indices[i] = i;
    }

    addDatagram(new ArtNetDatagram(indices, PIXELS_PER_UNIVERSE, 0));

    this.setAddress(CONTROLLER_IP);
  }
}
