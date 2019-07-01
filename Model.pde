LXModel buildModel() {
  return new PoTGModel();
}

public static class PoTGModel extends LXModel {
  public final static int PILLARS = 10;
  public final static int MODEL_RADIUS = 3 * METER;
  public final static int PILLAR_HEIGHT = (int) (1.5 * METER);
  public final static int LEDS_PER_METER = 60;
  public final static float LED_SPACING = METER / LEDS_PER_METER;
  public final static float HALF_LED_SPACING = LED_SPACING / 2;
  public final static int PILLAR_FACES = 3;
  public final static int PILLAR_RADIUS = 6 * CENTIMETER;

  public PoTGModel() {
    super(buildFixtures());
  }

  public static LXFixture[] buildFixtures() {
    LXFixture[] fixtures = new LXFixture[PILLARS + 1];

    Point2D[] points = circlePoints(MODEL_RADIUS, PILLARS);

    for (int pillar = 0; pillar < PILLARS; pillar++) {
      fixtures[pillar] = new Pillar(points[pillar].x, points[pillar].y);
    }

    fixtures[PILLARS] = new Altar();

    return fixtures;
  }

  public static class Point2D {
    int x;
    int y;

    Point2D(int x, int y) {
      this.x = x;
      this.y = y;
    }
  }

  public static Point2D[] circlePoints(int radius, int n) {
    Point2D[] points = new Point2D[n];

    // https://en.wikipedia.org/wiki/Internal_and_external_angles
    float externalAngle = TWO_PI / n;

    for (int i = 0; i < n; i++) {
      // https://stackoverflow.com/questions/839899/how-do-i-calculate-a-point-on-a-circle-s-circumference#839931
      points[i] = new Point2D(
        (int) (radius * cos(externalAngle * i)),
        (int) (radius * sin(externalAngle * i))
      );
    }

    return points;
  }

  public static class Pillar extends LXAbstractFixture {
    Pillar(int x, int z) {
      Point2D[] points = circlePoints(PILLAR_RADIUS, PILLAR_FACES);

      for (int face = 0; face < PILLAR_FACES; face++) {
        addPoints(new Strip(PILLAR_HEIGHT, x + points[face].x, z + points[face].y));
      }
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
    public static int HEIGHT = 50 * CENTIMETER;
    public static int RADIUS = 1 * METER;
    public static int STRIPS = 20;

    Altar() {
      Point2D[] points = circlePoints(RADIUS, STRIPS);

      for (int strip = 0; strip < STRIPS; strip++) {
        addPoints(new Strip(HEIGHT, points[strip].x, points[strip].y));
      }
    }
  }
}
