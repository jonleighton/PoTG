import java.io.File;
import heronarts.lx.LX;
import heronarts.lx.model.LXModel;
import heronarts.lx.LXPattern;
import heronarts.lx.LXEngine;
import heronarts.lx.LXChannel;
import heronarts.lx.effect.StrobeEffect;

public class Project {
  // This is the entrypoint for running headless
  public static void main(String[] args) {
    LX lx = new LX(new Model());
    setup(lx);

    if (args.length > 0) {
      lx.openProject(new File(args[0]));
    } else {
      setupChannels(lx);
    }

    lx.engine.start();
  }

  // This is shared setup code used by both headless and UI
  public static void setup(LX lx) {
    lx.registerPattern(ModelPartsPattern.class);
    lx.registerPattern(DormantPillarPattern.class);

    lx.registerEffect(StrobeEffect.class);

    try {
      lx.addOutput(new Output(lx));
    } catch (Exception x) {
      System.err.println(x.getLocalizedMessage());
    }
  }

  public static void setupChannels(LX lx) {
    LXEngine engine = lx.engine;
    Model model = (Model) lx.model;

    lx.newProject();

    engine.removeChannel(engine.getChannel(0));

    for (Model.Pillar pillar : model.getPillars()) {
      LXPattern[] patterns = { new DormantPillarPattern(lx, pillar.getNumber()) };
      LXChannel channel = engine.addChannel(patterns);
      channel.label.setValue(String.format("Pillar %s", pillar.getNumber()));
      channel.fader.setValue(100);
    }
  }
}
