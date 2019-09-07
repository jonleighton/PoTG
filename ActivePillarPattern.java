import heronarts.lx.LX;
import heronarts.lx.LXCategory;
import heronarts.lx.model.LXPoint;
import heronarts.lx.color.LXColor;
import heronarts.lx.parameter.CompoundParameter;
import heronarts.lx.modulator.LXModulator;
import heronarts.lx.modulator.QuadraticEnvelope;
import heronarts.lx.modulator.LinearEnvelope;

import java.util.List;

@LXCategory("Form")
public class ActivePillarPattern extends PillarPattern {
  public final CompoundParameter delay =
    new CompoundParameter("Delay", 2000, 10000, 200)
      .setDescription("Length of time the pillar goes dark before the head lights up");

  public final CompoundParameter headActivationDuration =
    new CompoundParameter("Head Activation Duration", 500, 10000, 200)
      .setDescription("Length of time for the head to light up");

  public final CompoundParameter verticalActivationDuration =
    new CompoundParameter("Vertical Activation Duration", 1000, 10000, 200)
      .setDescription("Length of time for the whole of the vertical to light up");

  private final LXModulator headIntensity =
    new QuadraticEnvelope(0, 100, headActivationDuration)
      .setLooping(false);

  private final LXModulator headTrigger = new ModulatorTrigger(delay, headIntensity);

  private final LXModulator verticalProgress =
    new LinearEnvelope(0, 1, verticalActivationDuration)
      .setLooping(false);

  private final LXModulator verticalTrigger = new ModulatorTrigger(delay, verticalProgress);

  public ActivePillarPattern(LX lx) {
    this(lx, DEFAULT_PILLAR_NUMBER);
  }

  public ActivePillarPattern(LX lx, int pillarNumber) {
    super(lx, pillarNumber);

    addParameter("delay", delay);

    addModulator(headIntensity);
    addModulator(headTrigger);

    addModulator(verticalProgress);
    addModulator(verticalTrigger);
  }

  public void onActive() {
    headTrigger.trigger();
    verticalTrigger.trigger();
  }

  public void run(double deltaMs) {
    setHead(LXColor.gray(headIntensity.getValue()));

    Model.Strip[] strips = vertical().getStrips();

    int threshold =
      Model.PillarVertical.POINTS_PER_STRIP -
        (int) (verticalProgress.getValue() * Model.PillarVertical.POINTS_PER_STRIP);

    for (Model.Strip strip : strips) {
      List<LXPoint> points = strip.getPoints();

      for (int i = 0; i < points.size(); i++) {
        setColor(
          points.get(i).index,
          i < threshold ? LXColor.BLACK : LXColor.WHITE
        );
      }
    }
  }
}
