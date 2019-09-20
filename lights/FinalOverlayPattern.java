import heronarts.lx.LX;
import heronarts.lx.LXPattern;
import heronarts.lx.color.LXColor;

public class FinalOverlayPattern extends LXPattern {
  public FinalOverlayPattern(LX lx) {
    super(lx);
  }

  public void run(double deltaMs) {
    Model model = (Model) lx.model;

    for (Model.Pillar pillar : model.getPillars()) {
      setColor(pillar.getHead(), LXColor.gray(100));
    }

    setColor(model.getAltarMiddle(), LXColor.gray(100));
  }
}
