import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.List;

import heronarts.lx.LX;
import heronarts.lx.output.LXDatagramOutput;
import heronarts.lx.output.ArtNetDatagram;
import heronarts.lx.model.LXPoint;
import heronarts.lx.model.LXFixture;

class Output extends LXDatagramOutput {
  // There are 512 channels per universe. The PixLite rounds this down to 510
  // to avoid splitting a single RGB pixel across universes. Therefore 170 max
  // pixels per universe.
  //
  // For simplicity, we are mapping one Art-Net universe to one PixLite output.
  // (But the PixLite can support more than 170 pixels per output in reality.)
  final static int PIXELS_PER_UNIVERSE = 170;

  final static String CONTROLLER_IP = "192.168.1.100";

  final static int PIXELS_PER_STRIP = 50;

  Output(LX lx) throws SocketException, UnknownHostException {
    super(lx);
    Model model = (Model) lx.model;

    for (Model.Pillar pillar : model.getPillars()) {
      addFixture((LXFixture) pillar, pillar.getIndex());
    }

    // TODO: In reality we will split the altar over several outputs.
    // addFixture(model.getAltar(), 11);

    setAddress(CONTROLLER_IP);
  }

  void addFixture(LXFixture fixture, int outputIndex) {
    List<LXPoint> points = fixture.getPoints();
    int[] indices = new int[points.size()];

    for (int i = 0; i < points.size(); i++) {
      indices[i] = points.get(i).index;
    }

    addDatagram(
      new ArtNetDatagram(
        indices,
        PIXELS_PER_UNIVERSE,
        outputIndexToUniverse(outputIndex)
      )
    );
  }

  // The PixLite is configured in extended mode, and we have two ArtNet
  // universes per output. However, we are only using one of them for
  // simplicity.
  int outputIndexToUniverse(int outputIndex) {
    return outputIndex * 2 + 1;
  }
}
