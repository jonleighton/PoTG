import heronarts.lx.LX;
import heronarts.lx.LXCategory;
import heronarts.lx.color.LXColor;
import heronarts.lx.parameter.CompoundParameter;
import heronarts.lx.modulator.LXModulator;
import heronarts.lx.modulator.QuadraticEnvelope;

@LXCategory("Form")
public class ActivePillarPattern extends PillarPattern {
  public final CompoundParameter delay =
    new CompoundParameter("Delay", 2000, 10000, 200)
      .setDescription("Length of time the pillar goes dark before the head lights up");

  private final LXModulator headIntensity =
    new QuadraticEnvelope(0, 100, 500)
      .setLooping(false);

  private final LXModulator trigger = new ModulatorTrigger(delay, headIntensity);

  public ActivePillarPattern(LX lx) {
    this(lx, DEFAULT_PILLAR_NUMBER);
  }

  public ActivePillarPattern(LX lx, int pillarNumber) {
    super(lx, pillarNumber);

    addParameter("delay", delay);

    addModulator(headIntensity);
    addModulator(trigger);
  }

  public void onActive() {
    trigger.trigger();
  }

  public void run(double deltaMs) {
    setHead(LXColor.gray(headIntensity.getValue()));
  }
}
