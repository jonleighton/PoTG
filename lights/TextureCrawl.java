import heronarts.lx.LX;
import heronarts.lx.LXCategory;
import heronarts.lx.LXPattern;
import heronarts.lx.color.LXColor;
import heronarts.lx.LXUtils;
import heronarts.lx.modulator.SinLFO;
import heronarts.lx.modulator.SawLFO;
import heronarts.lx.modulator.TriangleLFO;
import heronarts.lx.modulator.LXModulator;

@LXCategory("Texture")
public class TextureCrawl extends LXPattern {
  public String getAuthor() {
    return "Mark C. Slee";
  }

  private static final int NUM_MASKS = 24;
  private static final int MASK_SIZE = 5;

  private final int[][] mask = new int[NUM_MASKS][MASK_SIZE];

  private final LXModulator[] pos = new LXModulator[NUM_MASKS];
  private final LXModulator[] size = new LXModulator[NUM_MASKS];

  public TextureCrawl(LX lx) {
    super(lx);
    for (int i = 0; i < NUM_MASKS; ++i) {
      this.pos[i] = startModulator(new SawLFO(0, MASK_SIZE, startModulator(new SinLFO(2000, 7000, 19000).randomBasis())));
      this.size[i] = startModulator(new TriangleLFO(-3, 2*MASK_SIZE, 19000).randomBasis());
    }
  }

  public void run(double deltaMs) {
    for (int i = 0; i < NUM_MASKS; ++i) {
      float pos = this.pos[i].getValuef();
      float falloff = 100 / Math.max(1, this.size[i].getValuef());
      for (int j = 0; j < MASK_SIZE; ++j) {
        this.mask[i][j] = LXColor.gray(Math.max(0, 100 - falloff * LXUtils.wrapdistf(j, pos, MASK_SIZE)));
      }
    }

    int[] mask = this.mask[0];
    int mi = 0;

    for (int i = 0; i < model.points.length; i++) {
      if ((i % MASK_SIZE) == 0) {
        mask = this.mask[(mi++) % NUM_MASKS];
      }

      colors[model.points[i].index] = mask[i % MASK_SIZE];
    }
  }
}
