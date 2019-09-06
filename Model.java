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
    public final static int HEIGHT = (int) (1.3 * METER);
    public final static int FACES = 3;
    public final static int RADIUS = 6 * CENTIMETER;

    public final int index;

    Pillar(int index, int x, int z, double rotation) {
      this.index = index;

      CirclePoint[] points = circlePoints(RADIUS, FACES, rotation);

      for (int face = 0; face < FACES; face++) {
        addPoints(new Strip(HEIGHT, x + points[face].x(), z + points[face].y()));
      }
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
  }

  public static class Strip extends LXAbstractFixture {
    Strip(int height, int x, int z) {
      int count = (int) (height / LED_SPACING);

      for (int led = 0; led < count; led++) {
        addPoint(new LXPoint(x, (led * LED_SPACING) + HALF_LED_SPACING, z));
      }
    }
  }

  public static class Altar extends LXAbstractFixture {
    public static int HEIGHT = 100 * CENTIMETER;
    public static int RADIUS = 60 * CENTIMETER;
    public static int STRIPS = 20;

    Altar() {
      CirclePoint[] points = circlePoints(RADIUS, STRIPS);

      for (int strip = 0; strip < STRIPS; strip++) {
        addPoints(new Strip(HEIGHT, points[strip].x(), points[strip].y()));
      }
    }
  }
}
