import java.io.File;
import heronarts.lx.LX;
import heronarts.lx.LXPattern;
import heronarts.lx.model.GridModel;
import heronarts.lx.model.LXModel;
import heronarts.lx.output.ArtNetDatagram;
import heronarts.lx.output.LXDatagramOutput;

public class Headless {
  public static void main(String[] args) {
    try {
      LX lx = new LX(new Model());

      if (args.length > 0) {
        lx.openProject(new File(args[0]));
      }

      lx.engine.start();
    } catch (Exception x) {
      System.err.println(x.getLocalizedMessage());
    }
  }
}
