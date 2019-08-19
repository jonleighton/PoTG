import java.io.File;
import heronarts.lx.LX;
import heronarts.lx.model.LXModel;

public class Project {
  // This is the entrypoint for running headless
  public static void main(String[] args) {
    LX lx = new LX(new Model());

    setup(lx);

    if (args.length > 0) {
      lx.openProject(new File(args[0]));
    }

    lx.engine.start();
  }

  // This is shared setup code used by both headless and UI
  public static void setup(LX lx) {
    lx.registerPattern(ModelPartsPattern.class);

    try {
      lx.addOutput(new Output(lx));
    } catch (Exception x) {
      System.err.println(x.getLocalizedMessage());
    }
  }
}
