import java.io.File;
import heronarts.lx.LX;
import heronarts.lx.model.LXModel;

public class Headless {
  public static void main(String[] args) {
    try {
      LX lx = new LX(new Model());
      lx.addOutput(new Output(lx));

      if (args.length > 0) {
        lx.openProject(new File(args[0]));
      }

      lx.engine.start();
    } catch (Exception x) {
      System.err.println(x.getLocalizedMessage());
    }
  }
}
