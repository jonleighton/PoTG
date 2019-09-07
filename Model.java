import heronarts.lx.model.LXModel;
import heronarts.lx.model.LXFixture;
import heronarts.lx.model.LXAbstractFixture;
import heronarts.lx.model.LXPoint;

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

  private final Fixture fixture;

  public Model() {
    super(new Fixture());
    this.fixture = (Fixture) this.fixtures.get(0);
  }

  public Altar getAltar() {
    return this.fixture.altar;
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

  public Head getAltarHead(int index) {
    return this.fixture.altar.heads[index];
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

    Head getHead() {
      return this.head;
    }

    PillarVertical getVertical() {
      return this.vertical;
    }
  }

  public static class PillarVertical extends LXAbstractFixture {
    public final static int RADIUS = 3 * CENTIMETER;
    public final static int POINTS_PER_STRIP = (int) (Pillar.HEIGHT / LED_SPACING);

    private Strip[] strips = new Strip[Pillar.FACES];

    PillarVertical(int x, int z, double rotation) {
      CirclePoint[] points = circlePoints(RADIUS, Pillar.FACES, rotation);

      for (int i = 0; i < Pillar.FACES; i++) {
        strips[i] = new Strip(
          Pillar.HEIGHT,
          x + points[i].x(),
          z + points[i].y()
        );

        addPoints(strips[i]);
      }
    }

    public Strip[] getStrips() {
      return strips;
    }

    public Strip getStrip(int index) {
      return strips[index];
    }
  }

  public static class Strip extends LXAbstractFixture {
    Strip(int height, int x, int z) {
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
      rotation += (Math.PI * 2) / 6;

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
    public static int RADIUS = 60 * CENTIMETER;
    public static int HEAD_INSET = 10 * CENTIMETER;

    public Head[] heads = new Head[PILLARS];

    Altar() {
      CirclePoint points[] = circlePoints(RADIUS - HEAD_INSET, PILLARS);

      for (int i = 0; i < points.length; i++) {
        Head head = new Head(
          points[i].x(),
          HEIGHT,
          points[i].y(),
          points[i].angle()
        );

        heads[i] = head;
        addPoints(head);
      }
    }
  }
}
