import heronarts.lx.LX;
import heronarts.lx.LXPattern;
import heronarts.lx.LXCategory;
import heronarts.lx.parameter.BooleanParameter;
import heronarts.lx.color.LXColor;
import heronarts.lx.model.LXPoint;
import heronarts.lx.model.LXFixture;
import java.util.List;
import java.util.ArrayList;

@LXCategory("Form")
public class ModelPartsPattern extends LXPattern {
  public static abstract class FixtureParameter extends BooleanParameter {
    FixtureParameter(String name) {
      super(name);
    }

    abstract LXFixture getFixture();
  }

  public static abstract class PillarParameter extends FixtureParameter {
    protected Model.Pillar pillar;
    private String subPath;

    PillarParameter(Model.Pillar pillar, String label, String subPath) {
      super(String.format("%s %s", label, pillar.getNumber()));
      this.subPath = subPath;
      this.pillar = pillar;
    }

    public String getPath() {
      return String.format(
        "pillar/%s/%s",
        this.pillar.getNumber(),
        this.subPath
      );
    }
  }

  public static class PillarVerticalParameter extends PillarParameter {
    PillarVerticalParameter(Model.Pillar pillar) {
      super(pillar, "PV", "vertical");
    }

    public LXFixture getFixture() {
      return (LXFixture) pillar.getVertical();
    }
  }

  public static class PillarHeadParameter extends PillarParameter {
    PillarHeadParameter(Model.Pillar pillar) {
      super(pillar, "PH", "head");
    }

    public LXFixture getFixture() {
      return (LXFixture) pillar.getHead();
    }
  }

  public static class AltarParameter extends FixtureParameter {
    private Model.Altar altar;

    AltarParameter(Model.Altar altar) {
      super("Altar");
      this.altar = altar;
    }

    public String getPath() {
      return "altar";
    }

    public LXFixture getFixture() {
      return (LXFixture) this.altar;
    }
  }

  private Model model;
  private ArrayList<FixtureParameter> toggles = new ArrayList<FixtureParameter>();

  public ModelPartsPattern(LX lx) {
    super(lx);
    this.model = (Model) lx.model;

    for (Model.Pillar pillar : this.model.getPillars()) {
      addToggle(new PillarVerticalParameter(pillar));
      addToggle(new PillarHeadParameter(pillar));
    }

    addToggle(new AltarParameter(this.model.getAltar()));
  }

  public void addToggle(FixtureParameter parameter) {
    toggles.add(parameter);
    addParameter(parameter.getPath(), parameter);
  }

  public void run(double deltaMs) {
    for (FixtureParameter parameter : this.toggles) {
      toggleFixture(parameter, parameter.getFixture());
    }
  }

  private void toggleFixture(BooleanParameter parameter, LXFixture fixture) {
    int color = LXColor.gray(parameter.getValueb() ? 100 : 0);

    for (LXPoint point : fixture.getPoints()) {
      colors[point.index] = color;
    }
  }
}
