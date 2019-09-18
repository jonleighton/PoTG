import heronarts.lx.LX;
import heronarts.lx.LXCategory;
import heronarts.lx.model.LXPoint;
import heronarts.lx.color.LXColor;
import heronarts.lx.parameter.CompoundParameter;
import heronarts.lx.parameter.FunctionalParameter;
import heronarts.lx.modulator.LXModulator;
import heronarts.lx.modulator.LXPeriodicModulator;
import heronarts.lx.modulator.QuadraticEnvelope;
import heronarts.lx.modulator.LinearEnvelope;
import heronarts.lx.audio.LXAudioOutput;

import java.util.List;

@LXCategory("Form")
public class ActivePillarPattern extends PillarPattern {
  private final static String AUDIO_FILE = "activation.wav";

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
    addModulator(
      new QuadraticEnvelope(0, 100, headDuration)
        .setLooping(false)
    );

  private final LXPeriodicModulator headTrigger = (LXPeriodicModulator)
    addModulator(
      new LinearEnvelope(0, 1, delay)
        .setLooping(false)
    );

  private final LXModulator verticalProgress =
    addModulator(
      new LinearEnvelope(0, 1, verticalDuration)
        .setLooping(false)
    );

  private final FunctionalParameter headActiveTime =
    new FunctionalParameter() {
      // Activate after the head has finished activating
      public double getValue() {
        return delay.getValue() + headDuration.getValue();
      }
    };

  private final LXPeriodicModulator verticalTrigger = (LXPeriodicModulator)
    addModulator(
      new LinearEnvelope(0, 1, headActiveTime)
        .setLooping(false)
    );

  private final LXModulator colorIntensity =
    addModulator(
      new QuadraticEnvelope(0, 100, colorDuration)
        .setLooping(false)
    );

  private final LXAudioOutput audioOutput = lx.engine.audio.output;

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
  }

  public void onActive() {
    super.onActive();

    headTrigger.trigger();
    headIntensity.reset();

    verticalTrigger.trigger();
    verticalProgress.reset();
    colorIntensity.reset();

    verticalPoints = new LXModulator[POINTS_PER_VERTICAL];
  }

  public void run(double deltaMs) {
    if (headTrigger.finished()) {
      headIntensity.trigger();

      audioOutput.file.setValue(AUDIO_FILE);
      audioOutput.trigger.setValue(true);
    }

    if (verticalTrigger.finished()) {
      verticalProgress.trigger();
      colorIntensity.trigger();
    }

    setHead(headColor());

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

      for (Model.VerticalStrip strip : vertical().getStrips()) {
        // Start from the last point as we want to fade in from the top
        int pointIndex = strip.getPoints().get(POINTS_PER_VERTICAL - i - 1).index;
        int color = color(intensity == null ? 0 : intensity.getValue());

        setColor(pointIndex, color);
      }
    }
  }

  public int headColor() {
    if (headActive.getValueb()) {
      return color(headIntensity.getValue());
    } else {
      return LXColor.BLACK;
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
