import heronarts.lx.LX;
import heronarts.lx.LXPattern;
import heronarts.lx.model.LXPoint;
import heronarts.lx.parameter.CompoundParameter;
import heronarts.lx.parameter.LXParameter;
import heronarts.lx.color.LXColor;
import heronarts.lx.LXUtils;

import processing.core.PApplet;

import java.util.ArrayList;
import java.util.Collections;

public class PatternStarlight extends LXPattern {
  public String getAuthor() {
    return "Mark C. Slee";
  }

  final static int MAX_STARS = 5000;
  final static int POINTS_PER_STAR = 3;

  final LXUtils.LookupTable flicker = new LXUtils.LookupTable(360, new LXUtils.LookupTable.Function() {
    public float compute(int i, int tableSize) {
      return (float) 0.5 - (float) 0.5 * PApplet.cos(i * PApplet.TWO_PI / tableSize);
    }
  });

  public final CompoundParameter speed =
    new CompoundParameter("Speed", 3000, 9000, 300)
    .setDescription("Speed of the twinkling");

  public final CompoundParameter variance =
    new CompoundParameter("Variance", .5, 0, .9)
    .setDescription("Variance of the twinkling");

  public final CompoundParameter numStars = (CompoundParameter)
    new CompoundParameter("Num", 5000, 50, MAX_STARS)
    .setExponent(2)
    .setDescription("Number of stars");

  private final Star[] stars = new Star[MAX_STARS];

  private final ArrayList<LXPoint> shuffledPoints;

  public PatternStarlight(LX lx) {
    super(lx);
    addParameter("speed", this.speed);
    addParameter("numStars", this.numStars);
    addParameter("variance", this.variance);
    this.shuffledPoints = new ArrayList<LXPoint>(model.getPoints());
    Collections.shuffle(this.shuffledPoints);
    for (int i = 0; i < MAX_STARS; ++i) {
      this.stars[i] = new Star(i);
    }
  }

  public void run(double deltaMs) {
    setColors(LXColor.gray(0));
    float numStars = this.numStars.getValuef();
    float speed = this.speed.getValuef();
    float variance = this.variance.getValuef();
    for (Star star : this.stars) {
      if (star.active) {
        star.run(deltaMs);
      } else if (star.num < numStars) {
        star.activate(speed, variance);
      }
    }
  }

  class Star {

    final int num;

    double period;
    float amplitude = 50;
    double accum = 0;
    boolean active = false;

    private PApplet processing = new PApplet();

    Star(int num) {
      this.num = num;
    }

    void activate(float speed, float variance) {
      this.period = PApplet.max(400, speed * (1 + processing.random(-variance, variance)));
      this.accum = 0;
      this.amplitude = processing.random(20, 100);
      this.active = true;
    }

    void run(double deltaMs) {
      int c = LXColor.gray(this.amplitude * flicker.get(this.accum / this.period));
      int maxLeaves = shuffledPoints.size();
      for (int i = 0; i < POINTS_PER_STAR; ++i) {
        int pointIndex = num * POINTS_PER_STAR + i;
        if (pointIndex < maxLeaves) {
          setColor(shuffledPoints.get(pointIndex).index, c);
        }
      }
      this.accum += deltaMs;
      if (this.accum > this.period) {
        this.active = false;
      }
    }
  }

}
