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
  public static abstract class AbstractFixtureParameter extends BooleanParameter {
    AbstractFixtureParameter(String name) {
      super(name);
      setValue(true);
    }

    abstract LXFixture getFixture();
  }

  public static class FixtureParameter extends AbstractFixtureParameter {
    private String path;
    private LXFixture fixture;

    FixtureParameter(String name, String path, LXFixture fixture) {
      super(name);
      this.path = path;
      this.fixture = fixture;
    }

    public LXFixture getFixture() {
      return this.fixture;
    }

    public String getPath() {
      return this.path;
    }
  }

  public static abstract class PillarParameter extends AbstractFixtureParameter {
    protected Model model;
    protected Model.Pillar pillar;
    private String subPath;

    PillarParameter(Model model, Model.Pillar pillar, String label, String subPath) {
      super(String.format("%s %s", label, pillar.getNumber()));
      this.model = model;
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
    PillarVerticalParameter(Model model, Model.Pillar pillar) {
      super(model, pillar, "PV", "vertical");
    }

    public LXFixture getFixture() {
      return (LXFixture) pillar.getVertical();
    }
  }

  public static class PillarHeadParameter extends PillarParameter {
    PillarHeadParameter(Model model, Model.Pillar pillar) {
      super(model, pillar, "PH", "head");
    }

    public LXFixture getFixture() {
      return (LXFixture) pillar.getHead();
    }
  }

  public static class AltarHeadParameter extends PillarParameter {
    AltarHeadParameter(Model model, Model.Pillar pillar) {
      super(model, pillar, "AH", "altarHead");
    }

    public LXFixture getFixture() {
      return (LXFixture) model.getAltarHead(pillar.getIndex());
    }
  }

  private Model model;
  private ArrayList<AbstractFixtureParameter> toggles = new ArrayList<AbstractFixtureParameter>();

  public FixtureParameter middle;

  public ModelPartsPattern(LX lx, boolean defaultState) {
    super(lx);
    this.model = (Model) lx.model;

    for (Model.Pillar pillar : this.model.getPillars()) {
      addToggle(new PillarVerticalParameter(this.model, pillar), defaultState);
      addToggle(new PillarHeadParameter(this.model, pillar), defaultState);
      addToggle(new AltarHeadParameter(this.model, pillar), defaultState);
    }

    this.middle = new FixtureParameter("AltMid", "altarMiddle", this.model.getAltar().middleFixture());
    addToggle(middle, defaultState);
  }

  public ModelPartsPattern(LX lx) {
    this(lx, true);
  }

  public void addToggle(AbstractFixtureParameter parameter, boolean defaultState) {
    parameter.setValue(defaultState);
    toggles.add(parameter);
    addParameter(parameter.getPath(), parameter);
  }

  public void run(double deltaMs) {
    for (AbstractFixtureParameter parameter : this.toggles) {
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
