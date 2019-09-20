import heronarts.lx.LX;
import heronarts.lx.LXPattern;
import heronarts.lx.model.LXPoint;
import heronarts.lx.parameter.CompoundParameter;
import heronarts.lx.parameter.LXParameter;
import heronarts.lx.color.LXColor;
import heronarts.lx.LXUtils;

import processing.core.PApplet;

public class PatternScanner extends LXPattern {
  public String getAuthor() {
    return "Mark C. Slee";
  }

  public final CompoundParameter speed = (CompoundParameter)
    new CompoundParameter("Speed", .5, -1, 1)
    .setPolarity(LXParameter.Polarity.BIPOLAR)
    .setDescription("Speed that the plane moves at");

  public final CompoundParameter sharp = (CompoundParameter)
    new CompoundParameter("Sharp", 0, -50, 150)
    .setDescription("Sharpness of the falling plane")
    .setExponent(2);

  public final CompoundParameter xSlope =
    new CompoundParameter("XSlope", 0, -1, 1)
    .setDescription("Slope on the X-axis");

  public final CompoundParameter zSlope =
    new CompoundParameter("ZSlope", 0, -1, 1)
    .setDescription("Slope on the Z-axis");

  private float basis = 0;

  public PatternScanner(LX lx) {
    super(lx);
    addParameter("speed", this.speed);
    addParameter("sharp", this.sharp);
    addParameter("xSlope", this.xSlope);
    addParameter("zSlope", this.zSlope);
  }

  public void run(double deltaMs) {
    float speed = this.speed.getValuef();
    speed = speed * speed * ((speed < 0) ? -1 : 1);
    float sharp = this.sharp.getValuef();
    float xSlope = this.xSlope.getValuef();
    float zSlope = this.zSlope.getValuef();
    this.basis = (float) (this.basis - (float) 0.001 * speed * deltaMs) % (float) 1.;
    for (LXPoint point : model.getPoints()) {
      setColor(
        point.index,
        LXColor.gray(
          PApplet.max(
            0,
            50 - sharp +
              (50 + sharp) *
              LXUtils.trif(
                (float) (
                  point.yn +
                    this.basis +
                    (point.xn-.5) * xSlope +
                    (point.zn-.5) * zSlope
                )
              )
          )
        )
      );
    }
  }
}
