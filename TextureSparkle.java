import heronarts.lx.LX;
import heronarts.lx.LXCategory;
import heronarts.lx.LXPattern;
import heronarts.lx.parameter.LXParameter;
import heronarts.lx.parameter.CompoundParameter;
import heronarts.lx.model.LXPoint;
import heronarts.lx.color.LXColor;
import heronarts.lx.LXUtils;
import heronarts.lx.modulator.SinLFO;

import java.util.List;

// This pattern is adapted from the Tenere project:
// https://github.com/treeoftenere/Tenere/blob/6beaa0c93be58fa264738bce73a155e42af101dc/Tenere/Textures.pde#L124

@LXCategory("Texture")
public class TextureSparkle extends LXPattern {
  public String getAuthor() {
    return "Mark C. Slee";
  }

  private final LXPoint[] points = lx.model.points;
  private final SinLFO[] modulators = new SinLFO[points.length];

  public final CompoundParameter speed = (CompoundParameter)
    new CompoundParameter("Speed", 1500, 5000, 200)
    .setExponent(.5)
    .setDescription("Speed of the sparkling");

  public final CompoundParameter bright =
    new CompoundParameter("Bright", 60, 20, 100)
    .setDescription("Brightness of the sparkling");

  public final CompoundParameter min =
    new CompoundParameter("Min", 20, 0, 100)
    .setDescription("Min brightness of any point");

  public TextureSparkle(LX lx) {
    super(lx);

    addParameter("speed", this.speed);
    addParameter("bright", this.bright);
    addParameter("min", this.min);

    for (int i = 0; i < points.length; ++i) {
      this.modulators[i] = new SinLFO(0, 0, 1000);
      setupModulator(this.modulators[i]);
      startModulator(this.modulators[i].randomBasis());
    }
  }

  private void setupModulator(SinLFO level) {
    level
      .setRange(min.getValuef(), LXUtils.random(bright.getValuef(), 100))
      .setPeriod(Math.min(7000, speed.getValuef() * LXUtils.random(1, 2)));
  }

  public void run(double deltaMs) {
    for (int i = 0; i < points.length; ++i) {
      if (modulators[i].loop()) {
        setupModulator(modulators[i]);
      }

      setColor(points[i].index, LXColor.gray(modulators[i].getValuef()));
    }
  }
}
