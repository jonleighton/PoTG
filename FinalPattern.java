import heronarts.lx.LX;
import heronarts.lx.LXCategory;
import heronarts.lx.LXPattern;
import heronarts.lx.color.LXColor;
import heronarts.lx.parameter.CompoundParameter;
import heronarts.lx.modulator.QuadraticEnvelope;
import heronarts.lx.modulator.LXPeriodicModulator;

@LXCategory("Form")
public class FinalPattern extends LXPattern {
  public final CompoundParameter speed =
    new CompoundParameter("Speed", 600, 10000, 200)
      .setDescription("Speed of the modulation");

  private final LXPeriodicModulator alpha =
    new QuadraticEnvelope(1, 0, speed)
      .setLooping(false);

  public FinalPattern(LX lx) {
    super(lx);
    addParameter("speed", speed);
    addModulator(alpha);
  }

  public void onActive() {
    alpha.trigger();
  }

  public void run(double deltaMs) {
    // When we're done, automatically switch the channel back to the first
    // pattern (which should be a NonePattern)
    if (alpha.finished()) {
      getChannel().goIndex(0);
    }

    setColors(LXColor.hsba(55, 100, 100, alpha.getValue()));
  }
}
