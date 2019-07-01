LXModel buildModel() {
  return new PoTGModel();
}

public static class PoTGModel extends LXModel {
  public final static int PILLARS = 10;
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

  public static LXFixture[] buildFixtures() {
    LXFixture[] fixtures = new LXFixture[1];
    fixtures[0] = new Pillar(0, 0);
    return fixtures;
  }

  public static class Pillar extends LXAbstractFixture {
    Pillar(int x, int z) {
      // https://en.wikipedia.org/wiki/Internal_and_external_angles
      float externalAngle = (2 * PI) / PILLAR_FACES;

      for (int face = 0; face < PILLAR_FACES; face++) {
        // https://stackoverflow.com/questions/839899/how-do-i-calculate-a-point-on-a-circle-s-circumference#839931
        int faceX = (int) (x + PILLAR_RADIUS * cos(externalAngle * face));
        int faceZ = (int) (z + PILLAR_RADIUS * sin(externalAngle * face));

        addPoints(new PillarFace(faceX, faceZ));
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
