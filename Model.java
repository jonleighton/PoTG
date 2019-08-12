import heronarts.lx.model.LXModel;
import heronarts.lx.model.LXFixture;
import heronarts.lx.model.LXAbstractFixture;
import heronarts.lx.model.LXPoint;

public class Model extends LXModel {
  public final static int CENTIMETER = 1;
  public final static int METER = 100 * CENTIMETER;

  public final static int PILLARS = 10;
  public final static int MODEL_RADIUS = 3 * METER;

  // There are actually 30 LEDs per metre, but with 1 IC controlling 3 LEDs.
  // Our model represents ICs rather than LEDs.
  public final static int LEDS_PER_METER = 10;
  public final static double LED_SPACING = METER / LEDS_PER_METER;
  public final static double HALF_LED_SPACING = LED_SPACING / 2;

  public Model() {
    super(buildFixtures());
  }

  public static LXFixture[] buildFixtures() {
    LXFixture[] fixtures = new LXFixture[PILLARS + 1];

    CirclePoint[] points = circlePoints(MODEL_RADIUS, PILLARS);

    for (int pillar = 0; pillar < PILLARS; pillar++) {
      CirclePoint point = points[pillar];

      fixtures[pillar] = new Pillar(
          point.x(),
          point.y(),
          Math.PI + point.angle() // add PI to get it facing inwards to the altar
      );
    }

    fixtures[PILLARS] = new Altar();

    return fixtures;
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
    public final static int HEIGHT = (int) (1.4 * METER);
    public final static int FACES = 3;
    public final static int RADIUS = 6 * CENTIMETER;

    Pillar(int x, int z, double rotation) {
      CirclePoint[] points = circlePoints(RADIUS, FACES, rotation);

      for (int face = 0; face < FACES; face++) {
        addPoints(new Strip(HEIGHT, x + points[face].x(), z + points[face].y()));
      }
    }

    Pillar(int x, int z) {
      this(x, z, 0.0);
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
    public static int HEIGHT = 60 * CENTIMETER;
    public static int RADIUS = 1 * METER;
    public static int STRIPS = 20;

    Altar() {
      CirclePoint[] points = circlePoints(RADIUS, STRIPS);

      for (int strip = 0; strip < STRIPS; strip++) {
        addPoints(new Strip(HEIGHT, points[strip].x(), points[strip].y()));
      }
    }
  }
}
