import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Arrays;

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

  // We're using a PixLite 16
  final static int OUTPUTS_COUNT = 16;

  final static int HALF_PILLAR_COUNT = Model.PILLARS / 2;

  // This is the first head in the chain that is connected to the PixLite, i.e.
  // the one which aligns with the hinge on the altar lid.
  final static Model.Animal FIRST_ALTAR_HEAD = Model.Animal.ELEPHANT;
  final static int FIRST_ALTAR_HEAD_INDEX = Arrays.asList(Model.ALTAR_ORDER).indexOf(FIRST_ALTAR_HEAD);

  final static int ALTAR_HEADS_OUTPUT = 8;
  final static int ALTAR_MIDDLE_OUTPUT = 9;

  Output(LX lx) throws SocketException, UnknownHostException {
    super(lx);
    Model model = (Model) lx.model;

    for (Model.Pillar pillar : model.getPillars()) {
      addFixture(pillar);
    }

    addFixture(model.getAltar());

    setAddress(CONTROLLER_IP);
  }

  void addFixture(Model.Pillar pillar) {
    ArrayList<Integer> indices = new ArrayList<Integer>();
    Model.VerticalStrip[] strips = pillar.verticalStrips();

    for (int i = 0; i < strips.length; i++) {
      ArrayList<LXPoint> points = new ArrayList<LXPoint>(strips[i].getPoints());

      // The strips on the pillar verticals are wired in zig zags, i.e. we plug
      // in at the bottom, then go to the top, then connect the next strip at
      // its top, etc. Therefore odd-indexed strips (i.e. the second one of
      // the three) need the output mapping reversed as the top point is the
      // first one on the physical layout of the strip.
      if (i % 2 == 1) {
        Collections.reverse(points);
      }

      appendIndices(indices, points);
    }

    appendIndices(indices, pillar.getHead().getPoints());

    mapOutput(pillarOutput(pillar), indices);
  }

  int pillarOutput(Model.Pillar pillar) {
    int pillarNumber = pillar.getNumber();

    if (pillarNumber <= HALF_PILLAR_COUNT) {
      return pillarNumber;
    } else {
      // Pillar 6 is mapped to output 12, pillar 7 to output 13, etc.
      return OUTPUTS_COUNT - HALF_PILLAR_COUNT + (pillarNumber - HALF_PILLAR_COUNT);
    }
  }

  void addFixture(Model.Altar altar) {
    addFixture(altar.headsFixture());
    addFixture(altar.middleFixture());
  }

  void addFixture(Model.AltarHeads heads) {
    ArrayList<Integer> indices = new ArrayList<Integer>();

    for (int i = 0; i < Model.PILLARS; i++) {
      appendIndices(indices, heads.get((FIRST_ALTAR_HEAD_INDEX + i) % Model.PILLARS).getPoints());
    }

    mapOutput(ALTAR_HEADS_OUTPUT, indices);
  }

  void addFixture(Model.AltarMiddle middle) {
    ArrayList<Integer> indices = new ArrayList<Integer>();

    for (int i = 0; i < Model.AltarMiddle.NUM_STRIPS; i++) {
      ArrayList<LXPoint> points = new ArrayList<LXPoint>(middle.getStrip(i).getPoints());

      // Odd numbered strips are connected from the finish end of the
      // previous strip to the finish of this strip.
      if (i % 2 == 1) {
        Collections.reverse(points);
      }

      appendIndices(indices, points);
    }

    mapOutput(ALTAR_MIDDLE_OUTPUT, indices);
  }

  private void mapOutput(int outputNumber, List<Integer> indices) {
    addDatagram(
      new ArtNetDatagram(
        indicesAsArray(indices),
        PIXELS_PER_UNIVERSE,
        outputNumberToUniverse(outputNumber)
      )
    );
  }

  private void appendIndices(List<Integer> indices, List<LXPoint> points) {
    for (LXPoint point : points) {
      indices.add(point.index);
    }
  }

  private int[] indicesAsArray(List<Integer> indicesList) {
    int[] indicesArray = new int[indicesList.size()];

    for (int i = 0; i < indicesArray.length; i++) {
      indicesArray[i] = indicesList.get(i).intValue();
    }

    return indicesArray;
  }

  // The PixLite is configured in extended mode, and we have two ArtNet
  // universes per output. However, we are only using one of them for
  // simplicity. (So the universes we are using are 1, 3, 5, ...)
  private int outputNumberToUniverse(int outputNumber) {
    return outputNumber * 2 - 1;
  }
}
