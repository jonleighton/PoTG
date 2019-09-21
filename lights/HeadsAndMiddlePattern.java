import heronarts.lx.LX;
import heronarts.lx.LXPattern;
import heronarts.lx.color.LXColor;
import heronarts.lx.audio.BandGate;
import heronarts.lx.parameter.LXParameter;
import heronarts.lx.parameter.BoundedParameter;

public class HeadsAndMiddlePattern extends LXPattern {
  public BandGate bandGate;

  public final BoundedParameter decay = (BoundedParameter)
    new BoundedParameter("Decay", 400, 0, 1600)
    .setDescription("Sets the decay time of the trigger signal")
    .setUnits(LXParameter.Units.MILLISECONDS);

  public HeadsAndMiddlePattern(LX lx, BandGate bandGate) {
    super(lx);
    this.bandGate = bandGate;
    addParameter("decay", decay);
  }

  public void run(double deltaMs) {
    Model model = (Model) lx.model;

    for (Model.Pillar pillar : model.getPillars()) {
      setColor(pillar.getHead(), LXColor.gray(100));
    }

    setColor(model.getAltarMiddle(), LXColor.gray(100));
  }

  public void onActive() {
    super.onActive();
    bandGate.decay.setValue(decay.getValue());
  }
}
