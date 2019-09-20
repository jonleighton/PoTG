import heronarts.lx.LX;
import heronarts.lx.LXPattern;
import heronarts.lx.parameter.CompoundParameter;
import heronarts.lx.model.LXPoint;
import heronarts.lx.color.LXColor;

public class ColorRain extends LXPattern {
  public String getAuthor() {
    return "Mark C. Slee";
  }

  public final CompoundParameter speed =
    new CompoundParameter("Speed", 1, 5)
    .setDescription("Speed of the rainfall");

  public final CompoundParameter range =
    new CompoundParameter("Range", 30, 5, 50)
    .setDescription("Range of hue");

  private static final int BUCKETS = 37;
  private final float[][] buckets = new float[BUCKETS+1][BUCKETS+1];

  public ColorRain(LX lx) {
    super(lx);
    addParameter("speed", this.speed);
    addParameter("range", this.range);
    for (int i = 0; i < BUCKETS+1; ++i) {
      for (int j = 0; j < BUCKETS+1; ++j) {
        this.buckets[i][j] = (float) Math.random();
      }
    }
  }

  private double accum = 0;

  public void run(double deltaMs) {
    int range = (int) this.range.getValue();
    accum += this.speed.getValue() * .02 * deltaMs;
    float saturation = palette.getSaturationf();
    float hue = palette.getHuef();
    for (LXPoint point : model.getPoints()) {
      float offset = this.buckets[(int) (BUCKETS * point.xn)][(int) (BUCKETS * point.zn)];
      int hMove = ((int) (180 * point.yn + 120 * offset + accum)) % 80;
      if (hMove > range) {
        hMove = Math.max(0, range - 8*(hMove - range));
      }
      setColor(point.index, LXColor.hsb(
        hue - hMove,
        saturation,
        100
      ));
    }
  }
}
