import heronarts.lx.LX;
import heronarts.lx.color.LXColor;
import heronarts.lx.audio.BandGate;
import heronarts.lx.parameter.LXParameter;
import heronarts.lx.parameter.BoundedParameter;
import heronarts.lx.modulator.LinearEnvelope;

public class CrescendoPattern extends PatternVortex {
  public BandGate bandGate;

  public final BoundedParameter decay = (BoundedParameter)
    new BoundedParameter("Decay", 1300, 0, 1600)
    .setDescription("Sets the decay time of the trigger signal")
    .setUnits(LXParameter.Units.MILLISECONDS);

  private final LinearEnvelope decayTransition = (LinearEnvelope)
    addModulator(
      new LinearEnvelope(0, 1, 1)
        .setLooping(false)
    );

  public CrescendoPattern(LX lx, BandGate bandGate) {
    super(lx);
    this.bandGate = bandGate;
    addParameter("decay", decay);
  }

  public void onActive() {
    super.onActive();

    decayTransition
      .setRange(bandGate.decay.getValue(), decay.getValue())
      .setPeriod(getChannel().transitionTimeSecs.getValue() * 1000)
      .trigger();
  }

  public void run(double deltaMs) {
    super.run(deltaMs);

    if (decayTransition.isRunning()) {
      bandGate.decay.setValue(decayTransition.getValue());
    }

    setColor(((Model) model).getAltarHeads(), LXColor.BLACK);
  }
}
