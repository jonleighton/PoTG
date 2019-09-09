import heronarts.lx.LX;
import heronarts.lx.LXPattern;
import heronarts.lx.parameter.DiscreteParameter;
import heronarts.lx.parameter.BooleanParameter;

public abstract class PillarPattern extends LXPattern {
  public static final int DEFAULT_PILLAR_NUMBER = 1;

  public final DiscreteParameter pillarNumber =
    new DiscreteParameter("Pillar", DEFAULT_PILLAR_NUMBER, 1, Model.PILLARS + 1)
      .setDescription("Which pillar is targeted");

  public final BooleanParameter headActive =
    new BooleanParameter("Head", true)
      .setDescription("Whether the head is lit");

  public PillarPattern(LX lx) {
    super(lx);
    addParameter("pillar", pillarNumber);
    addParameter("headActive", headActive);
  }

  public PillarPattern(LX lx, int pillarNumber) {
    this(lx);
    this.pillarNumber.setValue(pillarNumber);
  }

  public void onActive() {
    headActive.setValue(true);
  }

  public Model.Pillar pillar() {
    return ((Model) model).getPillarNumber(pillarNumber.getValuei());
  }

  public void setPillar(int color) {
    setColor(this.pillar(), color);
  }

  public Model.PillarVertical vertical() {
    return this.pillar().getVertical();
  }

  public void setVertical(int color) {
    setColor(this.vertical(), color);
  }

  public Model.Head altarHead() {
    return ((Model) model).getAltarHeadNumber(pillarNumber.getValuei());
  }

  public void setAltarHead(int color) {
    setColor(this.altarHead(), color);
  }

  public Model.Head head() {
    return this.pillar().getHead();
  }

  public void setHead(int color) {
    setColor(this.head(), color);
  }
}
