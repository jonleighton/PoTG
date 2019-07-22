LXModel buildModel() {
  return new PoTGModel();
}

public static class PoTGModel extends LXModel {
  public final static int PILLARS = 10;
  public final static int MODEL_RADIUS = 3 * METER;
  public final static int LEDS_PER_METER = 60;
  public final static float LED_SPACING = METER / LEDS_PER_METER;
  public final static float HALF_LED_SPACING = LED_SPACING / 2;

  public PoTGModel() {
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
          PI + point.angle() // add PI to get it facing inwards to the altar
      );
    }

    fixtures[PILLARS] = new Altar();

    return fixtures;
  }

  // https://stackoverflow.com/questions/839899/how-do-i-calculate-a-point-on-a-circle-s-circumference#839931
  public static class CirclePoint {
    private int radius;
    private float angle;

    CirclePoint(int radius, float angle) {
      this.radius = radius;
      this.angle = angle;
    }

    public int radius() {
      return this.radius;
    }

    public float angle() {
      return this.angle;
    }

    public int x() {
      return (int) (this.radius * cos(this.angle));
    }

    public int y() {
      return (int) (this.radius * sin(this.angle));
    }
  }

  public static CirclePoint[] circlePoints(int radius, int n, float rotation) {
    CirclePoint[] points = new CirclePoint[n];

    // https://en.wikipedia.org/wiki/Internal_and_external_angles
    float externalAngle = TWO_PI / n;

    for (int i = 0; i < n; i++) {
      points[i] = new CirclePoint(radius, rotation + externalAngle * i);
    }

    return points;
  }

  public static CirclePoint[] circlePoints(int radius, int n) {
    return circlePoints(radius, n, 0);
  }

  public static class Pillar extends LXAbstractFixture {
    public final static int HEIGHT = (int) (1.2 * METER);
    public final static int FACES = 3;
    public final static int RADIUS = 6 * CENTIMETER;

    Pillar(int x, int z, float rotation) {
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
