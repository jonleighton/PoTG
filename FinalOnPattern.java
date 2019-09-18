import heronarts.lx.LX;
import heronarts.lx.LXCategory;
import heronarts.lx.LXPattern;
import heronarts.lx.color.LXColor;
import heronarts.lx.parameter.CompoundParameter;
import heronarts.lx.modulator.QuadraticEnvelope;
import heronarts.lx.modulator.LXPeriodicModulator;
import heronarts.lx.modulator.LXRangeModulator;
import heronarts.lx.modulator.LinearEnvelope;
import heronarts.lx.audio.LXAudioOutput;

@LXCategory("Form")
public class FinalOnPattern extends LXPattern {
  private final static String AUDIO_FILE = "final-test.wav";

  public final CompoundParameter speed =
    new CompoundParameter("Speed", 5000, 10000, 200)
      .setDescription("Speed to bring pattern in");

  private final LXPeriodicModulator intensity = (LXPeriodicModulator)
    addModulator(
      new QuadraticEnvelope(0, 100, speed)
        .setLooping(false)
    );

  public final CompoundParameter crossfadeSpeed =
    new CompoundParameter("XFSpeed", 3000, 10000, 200)
      .setDescription("Speed to move the crossfader");

  private final LXRangeModulator crossfader = (LXRangeModulator)
    addModulator(
      new LinearEnvelope(0, 1, crossfadeSpeed)
        .setLooping(false)
    );

  public final CompoundParameter audioDelay =
    new CompoundParameter("AuDelay", 3000, 10000, 200)
      .setDescription("Delay before audio starts");

  private final LXRangeModulator audioTrigger = (LXRangeModulator)
    addModulator(
      new LinearEnvelope(0, 1, audioDelay)
        .setLooping(false)
    );

  private final LXAudioOutput audioOutput = lx.engine.audio.output;

  public FinalOnPattern(LX lx) {
    super(lx);

    addParameter("speed", speed);
    addParameter("crossfadeSpeed", crossfadeSpeed);
    addParameter("audioDelay", audioDelay);
  }

  public void onTransitionStart() {
    intensity.trigger();

    crossfader
      .setRange(lx.engine.crossfader.getValue(), 1)
      .setPeriod(crossfadeSpeed)
      .trigger();

    audioTrigger.trigger();
  }

  public void run(double deltaMs) {
    if (this.finished()) {
      getChannel().goNext();
      return;
    }

    if (audioTrigger.finished()) {
      audioOutput.file.setValue(AUDIO_FILE);
      audioOutput.trigger.setValue(true);
    }

    if (crossfader.isRunning()) {
      lx.engine.crossfader.setValue(crossfader.getValue());
    }

    setColors(LXColor.gray(intensity.getValue()));
    setColor(((Model) model).getAltarHeads(), LXColor.BLACK);
  }

  public boolean finished() {
    return !(
      audioTrigger.isRunning() ||
        audioTrigger.finished() ||
        audioOutput.trigger.isOn() ||
        audioOutput.play.isOn()
    );
  }
}
