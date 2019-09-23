import heronarts.lx.LX;
import heronarts.lx.color.LXColor;
import heronarts.lx.parameter.LXParameter;
import heronarts.lx.parameter.BoundedParameter;
import heronarts.lx.modulator.LinearEnvelope;
import heronarts.lx.model.LXPoint;

import java.util.List;

// This is a customisation of the Vortex pattern which makes light emanate
// outwards in the middle of the altar
public class EmanatingMiddlePattern extends PatternVortex {
  private Model model;

  public EmanatingMiddlePattern(LX lx) {
    super(lx);
    this.model = (Model) lx.model;

    size.setValue(90);
    xSlope.setValue(1);
    ySlope.setValue(0);
    zSlope.setValue(1);
  }

  public List<LXPoint> getPoints() {
    return model.getAltarMiddle().getPoints();
  }
}
