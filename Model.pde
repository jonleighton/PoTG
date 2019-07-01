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
  public final static int LEDS_PER_PILLAR_FACE = (int) (PILLAR_HEIGHT / LED_SPACING);
  public final static int PILLAR_FACES = 3;
  public final static int PILLAR_RADIUS = 6 * CENTIMETER;

  public PoTGModel() {
    super(buildFixtures());
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

  public static LXFixture[] buildFixtures() {
    LXFixture[] fixtures = new LXFixture[PILLARS];

    Point2D[] points = circlePoints(MODEL_RADIUS, PILLARS);

    for (int pillar = 0; pillar < PILLARS; pillar++) {
      fixtures[pillar] = new Pillar(points[pillar].x, points[pillar].y);
    }

    return fixtures;
  }

  public static class Pillar extends LXAbstractFixture {
    Pillar(int x, int z) {
      Point2D[] points = circlePoints(PILLAR_RADIUS, PILLAR_FACES);

      for (int face = 0; face < PILLAR_FACES; face++) {
        addPoints(new PillarFace(x + points[face].x, z + points[face].y));
      }
    }
  }

  public static class PillarFace extends LXAbstractFixture {
    PillarFace(int x, int z) {
      for (int led = 0; led < LEDS_PER_PILLAR_FACE; led++) {
        addPoint(new LXPoint(x, (led * LED_SPACING) + HALF_LED_SPACING, z));
      }
    }
  }
}
