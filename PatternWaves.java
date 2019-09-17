import heronarts.lx.LX;
import heronarts.lx.LXPattern;
import heronarts.lx.model.LXPoint;
import heronarts.lx.parameter.CompoundParameter;
import heronarts.lx.parameter.LXParameter;
import heronarts.lx.color.LXColor;
import heronarts.lx.LXUtils;
import heronarts.lx.modulator.LXModulator;
import heronarts.lx.modulator.SawLFO;
import heronarts.lx.modulator.DampedParameter;

import processing.core.PApplet;

public class PatternWaves extends LXPattern {
  public String getAuthor() {
    return "Mark C. Slee";
  }

  final int NUM_LAYERS = 3;

  final float AMP_DAMPING_V = (float) 1.5;
  final float AMP_DAMPING_A = (float) 2.5;

  final float LEN_DAMPING_V = (float) 1.5;
  final float LEN_DAMPING_A = (float) 1.5;

  public final CompoundParameter rate = (CompoundParameter)
    new CompoundParameter("Rate", 6000, 48000, 2000)
    .setDescription("Rate of the of the wave motion")
    .setExponent(.3);

  public final CompoundParameter size =
    new CompoundParameter("Size", 1.2*Model.METER, 15*Model.CENTIMETER, 8.5*Model.METER)
    .setDescription("Width of the wave");

  public final CompoundParameter amp1 =
    new CompoundParameter("Amp1", .5, 2, .2)
    .setDescription("First modulation size");

  public final CompoundParameter amp2 =
    new CompoundParameter("Amp2", 1.4, 2, .2)
    .setDescription("Second modulation size");

  public final CompoundParameter amp3 =
    new CompoundParameter("Amp3", .5, 2, .2)
    .setDescription("Third modulation size");

  public final CompoundParameter len1 =
    new CompoundParameter("Len1", 1, 2, .2)
    .setDescription("First wavelength size");

  public final CompoundParameter len2 =
    new CompoundParameter("Len2", .8, 2, .2)
    .setDescription("Second wavelength size");

  public final CompoundParameter len3 =
    new CompoundParameter("Len3", 1.5, 2, .2)
    .setDescription("Third wavelength size");

  private final LXModulator phase =
    startModulator(new SawLFO(0, PApplet.TWO_PI, rate));

  private final LXModulator amp1Damp = startModulator(new DampedParameter(this.amp1, AMP_DAMPING_V, AMP_DAMPING_A));
  private final LXModulator amp2Damp = startModulator(new DampedParameter(this.amp2, AMP_DAMPING_V, AMP_DAMPING_A));
  private final LXModulator amp3Damp = startModulator(new DampedParameter(this.amp3, AMP_DAMPING_V, AMP_DAMPING_A));

  private final LXModulator len1Damp = startModulator(new DampedParameter(this.len1, LEN_DAMPING_V, LEN_DAMPING_A));
  private final LXModulator len2Damp = startModulator(new DampedParameter(this.len2, LEN_DAMPING_V, LEN_DAMPING_A));
  private final LXModulator len3Damp = startModulator(new DampedParameter(this.len3, LEN_DAMPING_V, LEN_DAMPING_A));

  private final LXModulator sizeDamp = startModulator(new DampedParameter(this.size, 12*Model.METER, 24*Model.METER));

  private final double[] bins = new double[512];

  public PatternWaves(LX lx) {
    super(lx);
    addParameter("rate", this.rate);
    addParameter("size", this.size);
    addParameter("amp1", this.amp1);
    addParameter("amp2", this.amp2);
    addParameter("amp3", this.amp3);
    addParameter("len1", this.len1);
    addParameter("len2", this.len2);
    addParameter("len3", this.len3);
  }

  public void run(double deltaMs) {
    double phaseValue = phase.getValue();
    float amp1 = this.amp1Damp.getValuef();
    float amp2 = this.amp2Damp.getValuef();
    float amp3 = this.amp3Damp.getValuef();
    float len1 = this.len1Damp.getValuef();
    float len2 = this.len2Damp.getValuef();
    float len3 = this.len3Damp.getValuef();
    float falloff = 100 / this.sizeDamp.getValuef();

    for (int i = 0; i < bins.length; ++i) {
      bins[i] = model.cy + model.yRange/2 * Math.sin(i * PApplet.TWO_PI / bins.length + phaseValue);
    }
    for (LXPoint point : model.getPoints()) {
      int idx = Math.round((bins.length-1) * (len1 * point.xn)) % bins.length;
      int idx2 = (int) Math.round((bins.length-1) * (len2 * (.2 + point.xn))) % bins.length;
      int idx3 = (int) Math.round((bins.length-1) * (len3 * (1.7 - point.xn))) % bins.length;

      float y1 = (float) bins[idx];
      float y2 = (float) bins[idx2];
      float y3 = (float) bins[idx3];

      float d1 = Math.abs(point.y*amp1 - y1);
      float d2 = Math.abs(point.y*amp2 - y2);
      float d3 = Math.abs(point.y*amp3 - y3);

      float b = PApplet.max(0, 100 - falloff * PApplet.min(PApplet.min(d1, d2), d3));
      setColor(point.index, b > 0 ? LXColor.gray(b) : LXColor.gray(0));
    }
  }
}
