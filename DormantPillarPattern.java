import heronarts.lx.LX;
import heronarts.lx.LXCategory;
import heronarts.lx.color.LXColor;
import heronarts.lx.parameter.CompoundParameter;
import heronarts.lx.modulator.SinLFO;
import heronarts.lx.modulator.LXModulator;

@LXCategory("Form")
public class DormantPillarPattern extends PillarPattern {
  public final CompoundParameter speed =
    new CompoundParameter("Speed", 5000, 10000, 200)
      .setDescription("Speed of the modulation");

  public final CompoundParameter probability =
    new CompoundParameter("Probability", 0.5)
      .setDescription("The probability this pillar will light up");

  private final LXModulator intensity = new SinLFO(0, 100, speed);

  private long nextIteration = 0;
  private boolean active = false;

  public DormantPillarPattern(LX lx) {
    this(lx, DEFAULT_PILLAR_NUMBER);
  }

  public DormantPillarPattern(LX lx, int pillarNumber) {
    super(lx, pillarNumber);

    addParameter("speed", speed);
    addParameter("probability", probability);

    addModulator(intensity);
  }

  public void run(double deltaMs) {
    long currentTime = System.currentTimeMillis();

    // FIXME: somehow use modulators for this?
    if (currentTime > nextIteration) {
      active = Math.random() <= probability.getValuef();

      if (active) {
        intensity.trigger();
        nextIteration = currentTime + (int) speed.getValuef();
      } else {
        intensity.stop();
        nextIteration = currentTime + ((int) speed.getValuef() / 2);
      }
    }

    setVertical(LXColor.gray(active ? intensity.getValuef() : 0));
    setAltarHead(headColor());
  }

  public int headColor() {
    if (headActive.getValueb()) {
      return LXColor.WHITE;
    } else {
      return LXColor.BLACK;
    }
  }
}
