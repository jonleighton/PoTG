import heronarts.lx.LX;
import heronarts.lx.LXPattern;
import heronarts.lx.LXCategory;
import heronarts.lx.parameter.BooleanParameter;
import heronarts.lx.color.LXColor;
import heronarts.lx.model.LXPoint;
import heronarts.lx.model.LXFixture;
import java.util.List;

@LXCategory("Form")
public class ModelPartsPattern extends LXPattern {
  public static class PillarParameter extends BooleanParameter {
    private Model.Pillar pillar;

    PillarParameter(Model.Pillar pillar) {
      super(String.format("Pillar %s", pillar.getNumber()));

      this.setDescription(String.format("Enable pillar %s", pillar.getNumber()));
      this.pillar = pillar;
    }

    public String getPath() {
      return String.format("pillar/%s", this.pillar.getNumber());
    }
  }

  private Model model;
  private PillarParameter[] pillarParameters = new PillarParameter[Model.PILLARS];
  private BooleanParameter altarParameter = new BooleanParameter("Altar").setDescription("Enable altar");

  public ModelPartsPattern(LX lx) {
    super(lx);

    this.model = (Model) lx.model;

    for (Model.Pillar pillar : this.model.getPillars()) {
      PillarParameter parameter = new PillarParameter(pillar);
      this.pillarParameters[pillar.getIndex()] = parameter;
      addParameter(parameter.getPath(), parameter);
    }

    addParameter("altar", this.altarParameter);
  }

  public void run(double deltaMs) {
    for (PillarParameter parameter : this.pillarParameters) {
      toggleFixture(parameter, parameter.pillar);
    }

    toggleFixture(this.altarParameter, this.model.getAltar());
  }

  private void toggleFixture(BooleanParameter parameter, LXFixture fixture) {
    int color = LXColor.gray(parameter.getValueb() ? 100 : 0);

    for (LXPoint point : fixture.getPoints()) {
      colors[point.index] = color;
    }
  }
}
