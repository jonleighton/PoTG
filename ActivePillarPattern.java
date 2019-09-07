import heronarts.lx.LX;
import heronarts.lx.LXCategory;
import heronarts.lx.model.LXPoint;
import heronarts.lx.color.LXColor;
import heronarts.lx.parameter.CompoundParameter;
import heronarts.lx.parameter.FunctionalParameter;
import heronarts.lx.modulator.LXModulator;
import heronarts.lx.modulator.QuadraticEnvelope;
import heronarts.lx.modulator.LinearEnvelope;

import java.util.List;

@LXCategory("Form")
public class ActivePillarPattern extends PillarPattern {
  public final CompoundParameter delay =
    new CompoundParameter("Delay", 1000, 200, 3000)
      .setDescription("Length of time the pillar goes dark before the head lights up");

  public final CompoundParameter headDuration =
    new CompoundParameter("HdDur", 500, 200, 2000)
      .setDescription("How quickly we light the head");

  public final CompoundParameter verticalDuration =
    new CompoundParameter("VertDur", 500, 200, 2000)
      .setDescription("How quickly we move down the pillar");

  public final CompoundParameter verticalFade =
    new CompoundParameter("VertFade", 1000, 200, 3000)
      .setDescription("How quickly a point on the vertical fades in");

  public final CompoundParameter colorDuration =
    new CompoundParameter("ColDur", 2000, 200, 5000)
      .setDescription("How quickly the color fades in");

  private final LXModulator headIntensity =
    new QuadraticEnvelope(0, 100, headDuration)
      .setLooping(false);

  private final LXModulator headTrigger = new ModulatorTrigger(delay, headIntensity);

  private final LXModulator verticalProgress =
    new LinearEnvelope(0, 1, verticalDuration)
      .setLooping(false);

  private final FunctionalParameter headActiveTime =
    new FunctionalParameter() {
      // Activate after the head has finished activating
      public double getValue() {
        return delay.getValue() + headDuration.getValue();
      }
    };

  private final LXModulator verticalTrigger =
    new ModulatorTrigger(headActiveTime, verticalProgress);

  private final LXModulator colorIntensity =
    new QuadraticEnvelope(0, 100, colorDuration)
      .setLooping(false);

  private final LXModulator colorTrigger =
    new ModulatorTrigger(headActiveTime, colorIntensity);

  public static final int POINTS_PER_VERTICAL = Model.PillarVertical.POINTS_PER_STRIP;

  public LXModulator[] verticalPoints;

  public ActivePillarPattern(LX lx) {
    this(lx, DEFAULT_PILLAR_NUMBER);
  }

  public ActivePillarPattern(LX lx, int pillarNumber) {
    super(lx, pillarNumber);

    addParameter("delay", delay);
    addParameter("headDuration", headDuration);
    addParameter("verticalDuration", verticalDuration);
    addParameter("verticalFade", verticalFade);
    addParameter("colorDuration", colorDuration);

    addModulator(headTrigger);
    addModulator(verticalTrigger);
    addModulator(colorTrigger);
  }

  public void onActive() {
    headTrigger.trigger();
    verticalTrigger.trigger();
    colorTrigger.trigger();
    verticalPoints = new LXModulator[POINTS_PER_VERTICAL];
  }

  public void run(double deltaMs) {
    setHead(color(headIntensity.getValue()));

    // Points after this threshold should be activated
    double threshold = verticalProgress.getValue() * POINTS_PER_VERTICAL;

    for (int i = 0; i < POINTS_PER_VERTICAL; i++) {
      LXModulator intensity = verticalPoints[i];

      // If this point should be activated then create and start a modulator
      // to fade it in
      if (i < threshold && intensity == null) {
        intensity =
          verticalPoints[i] =
          new QuadraticEnvelope(0, 100, verticalFade)
            .setLooping(false);

        startModulator(intensity);
      }

      for (Model.Strip strip : vertical().getStrips()) {
        // Start from the last point as we want to fade in from the top
        int pointIndex = strip.getPoints().get(POINTS_PER_VERTICAL - i - 1).index;
        int color = color(intensity == null ? 0 : intensity.getValue());

        setColor(pointIndex, color);
      }
    }
  }

  public int color(double brightness) {
    return LXColor.hsb(
      pillar().hue(),
      colorIntensity.getValue(),
      brightness
    );
  }
}
