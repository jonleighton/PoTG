import heronarts.lx.LX;
import heronarts.lx.LXCategory;
import heronarts.lx.color.LXColor;
import heronarts.lx.parameter.CompoundParameter;
import heronarts.lx.parameter.FunctionalParameter;
import heronarts.lx.modulator.LXModulator;
import heronarts.lx.modulator.LXPeriodicModulator;
import heronarts.lx.modulator.SinLFO;
import heronarts.lx.modulator.LinearEnvelope;

@LXCategory("Form")
public class DormantPillarPattern extends PillarPattern {
  public final CompoundParameter speed =
    new CompoundParameter("Speed", 5000, 10000, 200)
      .setDescription("Speed of the modulation");

  public final CompoundParameter probability =
    new CompoundParameter("Probability", 0.5)
      .setDescription("The probability this pillar will light up");

  private final LXModulator intensity =
    addModulator(
      new SinLFO(0, 100, speed)
        .setLooping(false)
    );

  // Our 'heartbeat' loops twice as fast as the speed, so some pillars are
  // fading in while others are fading out.
  private final LXPeriodicModulator heartbeat = (LXPeriodicModulator)
    startModulator(
      new LinearEnvelope(0, 1, new FunctionalParameter() {
        public double getValue() {
          return speed.getValue() / 2.0;
        }
      }).setLooping(true)
    );

  public DormantPillarPattern(LX lx) {
    this(lx, DEFAULT_PILLAR_NUMBER);
  }

  public DormantPillarPattern(LX lx, int pillarNumber) {
    super(lx, pillarNumber);

    addParameter("speed", speed);
    addParameter("probability", probability);
  }

  public void onActive() {
    heartbeat.trigger();
    maybeActivate();
  }

  public void run(double deltaMs) {
    if (heartbeat.loop()) maybeActivate();

    setVertical(LXColor.gray(intensity.getValuef()));
    setAltarHead(headColor());
  }

  public int headColor() {
    if (headActive.getValueb()) {
      return LXColor.WHITE;
    } else {
      return LXColor.BLACK;
    }
  }

  private void maybeActivate() {
    if (!intensity.isRunning() && shouldActivate()) {
      intensity.trigger();
    }
  }

  private boolean shouldActivate() {
    return Math.random() <= probability.getValuef();
  }
}
