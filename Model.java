import heronarts.lx.model.LXModel;
import heronarts.lx.model.LXFixture;
import heronarts.lx.model.LXAbstractFixture;
import heronarts.lx.model.LXPoint;
import heronarts.lx.LXUtils;
import heronarts.lx.color.LXColor;

import java.util.Arrays;

public class Model extends LXModel {
  public final static int CENTIMETER = 1;
  public final static int METER = 100 * CENTIMETER;

  public final static int PILLARS = 10;

  // Altar radius + distance from altar
  public final static int MODEL_RADIUS = Altar.RADIUS + 3 * METER;

  // There are actually 30 LEDs per metre, but with 1 IC controlling 3 LEDs.
  // Our model represents ICs rather than LEDs.
  public final static int LEDS_PER_METER = 10;
  public final static double LED_SPACING = METER / LEDS_PER_METER;
  public final static double HALF_LED_SPACING = LED_SPACING / 2;

  public final static int[] PILLAR_HUES = pillarHues();

  private final Fixture fixture;

  public enum Animal {
    DEER,
    PEACOCK,
    HORSE,
    BEAR,
    LION,
    WOLF,
    ELEPHANT,
    KOALA,
    MEERKAT,
    HAWK,
  }

  // Anti-clockwise
  //
  // For simplicity, this is arranged so that the first animal in the model
  // corresponds to the first animal along the LED strip.
  public final static Animal[] ALTAR_ORDER = {
    Animal.ELEPHANT,
    Animal.WOLF,
    Animal.LION,
    Animal.BEAR,
    Animal.HORSE,
    Animal.PEACOCK,
    Animal.DEER,
    Animal.HAWK,
    Animal.MEERKAT,
    Animal.KOALA,
  };

  // Anti-clockwise
  //
  // For simplicity, the first animal in this list corresponds to PixLite
  // output 1
  public final static Animal[] PILLAR_ORDER = {
    Animal.HORSE,
    Animal.LION,
    Animal.KOALA,
    Animal.DEER,
    Animal.PEACOCK,
    Animal.BEAR,
    Animal.MEERKAT,
    Animal.ELEPHANT,
    Animal.WOLF,
    Animal.HAWK,
  };

  // A mapping from a pillar index to the corresponding altar head index
  public final static int[] PILLAR_TO_ALTAR_HEAD = new int[PILLARS];
  static {
    for (int i = 0; i < PILLARS; i++) {
      PILLAR_TO_ALTAR_HEAD[i] = Arrays.asList(ALTAR_ORDER).indexOf(PILLAR_ORDER[i]);
    }
  }

  private static int[] pillarHues() {
    int[] hues = new int[PILLARS];

    for (int i = 0; i < PILLARS; i++) {
      hues[i] = (int) LXUtils.lerp(0, 360, (double) i / PILLARS);
    }

    return hues;
  }

  public Model() {
    super(new Fixture());
    this.fixture = (Fixture) this.fixtures.get(0);
  }

  public Altar getAltar() {
    return this.fixture.altar;
  }

  public AltarHeads getAltarHeads() {
    return this.getAltar().headsFixture();
  }

  public AltarMiddle getAltarMiddle() {
    return this.getAltar().middleFixture();
  }

  public Pillar[] getPillars() {
    return this.fixture.pillars;
  }

  public Pillar getPillar(int index) {
    return this.fixture.pillars[index];
  }

  public Pillar getPillarNumber(int number) {
    return this.getPillar(number - 1);
  }

  public Head getAltarHead(int pillarIndex) {
    return this.fixture.altar.getHead(PILLAR_TO_ALTAR_HEAD[pillarIndex]);
  }

  public Head getAltarHeadNumber(int number) {
    return this.getAltarHead(number - 1);
  }

  public static class Fixture extends LXAbstractFixture {
    public final Altar altar = new Altar();
    public final Pillar[] pillars = new Pillar[PILLARS];

    Fixture() {
      addPoints(altar);

      CirclePoint[] points = circlePoints(MODEL_RADIUS, PILLARS);

      for (int pillar = 0; pillar < PILLARS; pillar++) {
        CirclePoint point = points[pillar];

        pillars[pillar] = new Pillar(
          pillar,
          point.x(),
          point.y(),
          point.angle()
        );

        addPoints(pillars[pillar]);
      }
    }
  }

  // https://stackoverflow.com/questions/839899/how-do-i-calculate-a-point-on-a-circle-s-circumference#839931
  public static class CirclePoint {
    private int radius;
    private double angle;

    CirclePoint(int radius, double angle) {
      this.radius = radius;
      this.angle = angle;
    }

    public int radius() {
      return this.radius;
    }

    public double angle() {
      return this.angle;
    }

    public int x() {
      return (int) (this.radius * Math.cos(this.angle));
    }

    public int y() {
      return (int) (this.radius * Math.sin(this.angle));
    }
  }

  public static CirclePoint[] circlePoints(int radius, int n, double rotation) {
    CirclePoint[] points = new CirclePoint[n];

    // https://en.wikipedia.org/wiki/Internal_and_external_angles
    double externalAngle = (Math.PI * 2) / n;

    for (int i = 0; i < n; i++) {
      points[i] = new CirclePoint(radius, rotation + externalAngle * i);
    }

    return points;
  }

  public static CirclePoint[] circlePoints(int radius, int n) {
    return circlePoints(radius, n, 0);
  }

  public static class Pillar extends LXAbstractFixture {
    // The LED support is 1.35M, but our strip can only be cut every 10cm, so
    // we'll do 1.3m of strip and mount it 5cm from the bottom of the support.
    public final static int HEIGHT = (int) (1.3 * METER);
    public final static int HEAD_HEIGHT = HEIGHT + 5 * CENTIMETER;
    public final static int FACES = 3;

    private final int index;
    private final PillarVertical vertical;
    private final Head head;

    Pillar(int index, int x, int z, double rotation) {
      this.index = index;

      // This gets the first vertical strip facing towards the altar
      // We want the data to come into the bottom of the pillar, go up the
      // strip facing the altar, down the next strip anti-clockwise, up the
      // next strip, then through a hole into the head triangle. The first head
      // triange strip is then also the side which faces the altar.
      rotation += ((Math.PI * 2) / 6) * 3;

      this.vertical = new PillarVertical(x, z, rotation);
      addPoints(this.vertical);

      this.head = new Head(x, HEAD_HEIGHT, z, rotation);
      addPoints(this.head);
    }

    Pillar(int index, int x, int z) {
      this(index, x, z, 0.0);
    }

    int getNumber() {
      return this.index + 1;
    }

    int getIndex() {
      return this.index;
    }

    int hue() {
      return PILLAR_HUES[this.index];
    }

    Head getHead() {
      return this.head;
    }

    PillarVertical getVertical() {
      return this.vertical;
    }

    VerticalStrip[] verticalStrips() {
      return this.vertical.getStrips();
    }
  }

  public static class PillarVertical extends LXAbstractFixture {
    public final static int RADIUS = 3 * CENTIMETER;
    public final static int POINTS_PER_STRIP = (int) (Pillar.HEIGHT / LED_SPACING);

    private VerticalStrip[] strips = new VerticalStrip[Pillar.FACES];

    PillarVertical(int x, int z, double rotation) {
      CirclePoint[] points = circlePoints(RADIUS, Pillar.FACES, rotation);

      for (int i = 0; i < Pillar.FACES; i++) {
        strips[i] = new VerticalStrip(
          Pillar.HEIGHT,
          x + points[i].x(),
          z + points[i].y()
        );

        addPoints(strips[i]);
      }
    }

    public VerticalStrip[] getStrips() {
      return strips;
    }

    public VerticalStrip getStrip(int index) {
      return strips[index];
    }
  }

  public static class VerticalStrip extends LXAbstractFixture {
    VerticalStrip(int height, int x, int z) {
      int count = (int) (height / LED_SPACING);

      for (int led = 0; led < count; led++) {
        addPoint(new LXPoint(x, (led * LED_SPACING) + HALF_LED_SPACING, z));
      }
    }
  }

  // The head lighting is made of 3 10cm strips of 3 LEDs (1 IC) in a triangle.
  // So the LXPoints in the middle of each side of the trangle are parallel
  // with each face of the pillar.
  public static class Head extends LXAbstractFixture {
    public final static int RADIUS = 6 * CENTIMETER;

    Head(int x, int y, int z, double rotation) {
      for (CirclePoint point : circlePoints(RADIUS, Pillar.FACES, rotation)) {
        addPoint(
          new LXPoint(
            x + point.x(),
            y,
            z + point.y()
          )
        );
      }
    }
  }

  public static class Altar extends LXAbstractFixture {
    public static int HEIGHT = 100 * CENTIMETER;
    public static int DIAMETER = (int) (1.2 * METER);
    public static int RADIUS = DIAMETER / 2;
    public static int HEAD_INSET = 10 * CENTIMETER;

    private AltarHeads heads = new AltarHeads();
    private AltarMiddle middle = new AltarMiddle();

    Altar() {
      addPoints(heads);
      addPoints(middle);
    }

    Head getHead(int index) {
      return this.heads.get(index);
    }

    AltarHeads headsFixture() {
      return this.heads;
    }

    AltarMiddle middleFixture() {
      return this.middle;
    }
  }

  public static class AltarHeads extends LXAbstractFixture {
    public Head[] heads = new Head[PILLARS];

    AltarHeads() {
      CirclePoint points[] = circlePoints(Altar.RADIUS - Altar.HEAD_INSET, PILLARS);

      for (int i = 0; i < points.length; i++) {
        Head head = new Head(
          points[i].x(),
          Altar.HEIGHT,
          points[i].y(),
          points[i].angle()
        );

        heads[i] = head;
        addPoints(head);
      }
    }

    Head get(int index) {
      return this.heads[index];
    }
  }

  public static class AltarMiddle extends LXAbstractFixture {
    public static int DIAMETER = 60 * CENTIMETER;
    public static int RADIUS = DIAMETER / 2;
    public static int NUM_STRIPS = 4;
    public static int POINTS_PER_STRIP = (int) (DIAMETER / LED_SPACING);

    // How far below the lid the LEDs are mounted.
    public static int LID_OFFSET = 5 * CENTIMETER;

    private AltarMiddleStrip strips[] = new AltarMiddleStrip[NUM_STRIPS];

    AltarMiddle() {
      CirclePoint[] points = circlePoints(RADIUS, NUM_STRIPS * 2);

      for (int i = 0; i < NUM_STRIPS; i++) {
        strips[i] = new AltarMiddleStrip(points[i], points[i + NUM_STRIPS]);
        addPoints(strips[i]);
      }
    }

    AltarMiddleStrip getStrip(int index) {
      return strips[index];
    }
  }

  public static class AltarMiddleStrip extends LXAbstractFixture {
    AltarMiddleStrip(CirclePoint start, CirclePoint finish) {
      for (int p = 0; p < AltarMiddle.POINTS_PER_STRIP; p++) {
        // We add 0.5 / POINTS_PER_STRIP so the point is mid-way along the
        // segment
        double progress = ((double) p) / AltarMiddle.POINTS_PER_STRIP + (0.5 / AltarMiddle.POINTS_PER_STRIP);

        addPoint(new LXPoint(
          LXUtils.lerp(start.x(), finish.x(), progress),
          Altar.HEIGHT - AltarMiddle.LID_OFFSET,
          LXUtils.lerp(start.y(), finish.y(), progress)
        ));
      }
    }
  }
}
