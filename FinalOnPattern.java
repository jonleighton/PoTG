import heronarts.lx.LX;
import heronarts.lx.LXCategory;
import heronarts.lx.LXPattern;
import heronarts.lx.color.LXColor;
import heronarts.lx.parameter.CompoundParameter;
import heronarts.lx.modulator.QuadraticEnvelope;
import heronarts.lx.modulator.LXPeriodicModulator;
import heronarts.lx.modulator.LXRangeModulator;
import heronarts.lx.modulator.LinearEnvelope;

@LXCategory("Form")
public class FinalOnPattern extends LXPattern {
  public final CompoundParameter speed =
    new CompoundParameter("Speed", 10000, 10000, 200)
      .setDescription("Speed to bring pattern in");

  private final LXPeriodicModulator intensity =
    new QuadraticEnvelope(0, 100, speed)
      .setLooping(false);

  public final CompoundParameter crossfadeSpeed =
    new CompoundParameter("XFSpeed", 3000, 10000, 200)
      .setDescription("Speed to move the crossfader");

  private final LXRangeModulator crossfader =
    new LinearEnvelope(0, 1, crossfadeSpeed);

  public FinalOnPattern(LX lx) {
    super(lx);

    addParameter("speed", speed);
    addParameter("crossfadeSpeed", crossfadeSpeed);

    crossfader.setLooping(false);

    addModulator(intensity);
    addModulator(crossfader);
  }

  public void onTransitionStart() {
    intensity.trigger();

    crossfader
      .setRange(lx.engine.crossfader.getValue(), 1)
      .setPeriod(crossfadeSpeed)
      .trigger();

    lx.engine.audio.output.trigger.setValue(true);
  }

  public void run(double deltaMs) {
    if (crossfader.isRunning()) {
      lx.engine.crossfader.setValue(crossfader.getValue());
    }

    setColors(LXColor.gray(intensity.getValue()));
    setColor(((Model) model).getAltarHeads(), LXColor.BLACK);
  }
}