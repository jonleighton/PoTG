import heronarts.lx.parameter.LXParameter;
import heronarts.lx.modulator.LXModulator;
import heronarts.lx.modulator.Click;

// This is a modulator which triggers another modulator after a certain delay.
public class ModulatorTrigger extends Click {
  private final LXModulator modulator;

  public ModulatorTrigger(LXParameter delay, LXModulator modulator) {
    super("ModulatorTrigger", delay);
    this.setLooping(false);
    this.modulator = modulator;
  }

  public void onStop() {
    modulator.trigger();
  }
}
