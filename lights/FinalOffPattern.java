import heronarts.lx.LX;
import heronarts.lx.LXCategory;
import heronarts.lx.LXPattern;
import heronarts.lx.color.LXColor;
import heronarts.lx.parameter.CompoundParameter;
import heronarts.lx.modulator.QuadraticEnvelope;
import heronarts.lx.modulator.LXRangeModulator;
import heronarts.lx.modulator.LinearEnvelope;

@LXCategory("Form")
public class FinalOffPattern extends LXPattern {
  public final CompoundParameter crossfadeSpeed =
    new CompoundParameter("XFSpeed", 10000, 10000, 200)
      .setDescription("Speed to move the crossfader");

  private final LXRangeModulator crossfader =
    new LinearEnvelope(1, 0, crossfadeSpeed);

  public FinalOffPattern(LX lx) {
    super(lx);
    addParameter("crossfadeSpeed", crossfadeSpeed);
    crossfader.setLooping(false);
    addModulator(crossfader);
  }

  public void onTransitionStart() {
    crossfader
      .setRange(lx.engine.crossfader.getValue(), 0)
      .setPeriod(crossfadeSpeed)
      .trigger();
  }

  public void run(double deltaMs) {
    if (crossfader.isRunning()) {
      lx.engine.crossfader.setValue(crossfader.getValue());
    }

    // This pattern is used with a subtract blend, so this turns everything off
    setColors(LXColor.WHITE);
  }
}
