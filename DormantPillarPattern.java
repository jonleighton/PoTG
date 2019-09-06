import heronarts.lx.LX;
import heronarts.lx.LXPattern;
import heronarts.lx.LXCategory;
import heronarts.lx.color.LXColor;
import heronarts.lx.parameter.CompoundParameter;
import heronarts.lx.parameter.DiscreteParameter;
import heronarts.lx.modulator.SinLFO;
import heronarts.lx.modulator.LXModulator;
import heronarts.lx.model.LXPoint;

@LXCategory("Form")
public class DormantPillarPattern extends LXPattern {
  public final DiscreteParameter pillarNumber =
    new DiscreteParameter("Pillar", 1, 1, Model.PILLARS + 1)
      .setDescription("Which pillar is targeted");

  public final CompoundParameter speed =
    new CompoundParameter("Speed", 5000, 10000, 200)
      .setDescription("Speed of the modulation");

  public final CompoundParameter probability =
    new CompoundParameter("Probability", 0.5)
      .setDescription("The probability this pillar will light up");

  private final LXModulator intensity = new SinLFO(0, 100, speed);

  private long nextIteration = 0;
  private boolean active = false;

  public DormantPillarPattern(LX lx) {
    super(lx);

    addParameter("pillar", pillarNumber);
    addParameter("speed", speed);
    addParameter("probability", probability);

    addModulator(intensity);
  }

  public DormantPillarPattern(LX lx, int pillarNumber) {
    this(lx);
    this.pillarNumber.setValue(pillarNumber);
  }

  public Model.Pillar pillar() {
    return ((Model) model).getPillarNumber(pillarNumber.getValuei());
  }

  public Model.Head altarHead() {
    return ((Model) model).getAltarHeadNumber(pillarNumber.getValuei());
  }

  public void run(double deltaMs) {
    long currentTime = System.currentTimeMillis();

    if (currentTime > nextIteration) {
      active = Math.random() <= probability.getValuef();

      if (active) {
        intensity.trigger();
        nextIteration = currentTime + (int) speed.getValuef();
      } else {
        intensity.stop();
        nextIteration = currentTime + ((int) speed.getValuef() / 2);
      }
    }

    int color = this.currentColor();

    for (LXPoint point : this.pillar().getVertical().getPoints()) {
      colors[point.index] = color;
    }

    for (LXPoint point : this.altarHead().getPoints()) {
      colors[point.index] = LXColor.WHITE;
    }
  }

  public int currentColor() {
    return LXColor.gray(active ? intensity.getValuef() : 0);
  }
}
