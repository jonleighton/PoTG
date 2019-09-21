import heronarts.lx.LX;
import heronarts.lx.color.LXColor;
import heronarts.lx.audio.BandGate;
import heronarts.lx.parameter.LXParameter;
import heronarts.lx.parameter.BoundedParameter;

public class CrescendoPattern extends PatternVortex {
  public BandGate bandGate;

  public final BoundedParameter decay = (BoundedParameter)
    new BoundedParameter("Decay", 1300, 0, 1600)
    .setDescription("Sets the decay time of the trigger signal")
    .setUnits(LXParameter.Units.MILLISECONDS);

  public CrescendoPattern(LX lx, BandGate bandGate) {
    super(lx);
    this.bandGate = bandGate;
    addParameter("decay", decay);
  }

  public void run(double deltaMs) {
    super.run(deltaMs);
    setColor(((Model) model).getAltarHeads(), LXColor.BLACK);
  }

  public void onActive() {
    super.onActive();
    bandGate.decay.setValue(decay.getValue());
  }
}
