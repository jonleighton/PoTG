import java.io.File;
import heronarts.lx.LX;
import heronarts.lx.model.LXModel;
import heronarts.lx.LXPattern;
import heronarts.lx.LXEngine;
import heronarts.lx.LXChannel;
import heronarts.lx.effect.StrobeEffect;
import heronarts.lx.blend.LXBlend;
import heronarts.lx.blend.MultiplyBlend;
import heronarts.lx.blend.NormalBlend;

import java.util.ArrayList;

public class Project {
  // This is the entrypoint for running headless
  public static void main(String[] args) {
    LX lx = new LX(new Model());
    setup(lx);

    if (args.length > 0) {
      lx.openProject(new File(args[0]));
    } else {
      setupChannels(lx);
    }

    lx.engine.start();
  }

  // This is shared setup code used by both headless and UI
  public static void setup(LX lx) {
    lx.registerPattern(ModelPartsPattern.class);
    lx.registerPattern(NonePattern.class);
    lx.registerPattern(DormantPillarPattern.class);
    lx.registerPattern(ActivePillarPattern.class);
    lx.registerPattern(FinalPattern.class);
    lx.registerPattern(TextureSparkle.class);

    lx.registerEffect(StrobeEffect.class);

    try {
      lx.addOutput(new Output(lx));
    } catch (Exception x) {
      System.err.println(x.getLocalizedMessage());
    }
  }

  public static void setupChannels(LX lx) {
    LXEngine engine = lx.engine;

    lx.newProject();

    engine.removeChannel(engine.getChannel(0));

    setupPillarChannels(lx);
    setupOverlayChannel(lx);
    setupTextureChannel(lx);
  }

  private static void setupPillarChannels(LX lx) {
    for (Model.Pillar pillar : ((Model) lx.model).getPillars()) {
      ArrayList<LXPattern> patterns = new ArrayList<LXPattern>();

      patterns.add(new DormantPillarPattern(lx, pillar.getNumber()));
      patterns.add(new ActivePillarPattern(lx, pillar.getNumber()));

      LXChannel channel = lx.engine.addChannel(patterns.toArray(new LXPattern[0]));

      channel.label.setValue(String.format("Pillar %s", pillar.getNumber()));
      channel.fader.setValue(1);
      channel.transitionEnabled.setValue(true);
      channel.transitionTimeSecs.setValue(0.5);
    }
  }

  private static void setupTextureChannel(LX lx) {
    ArrayList<LXPattern> patterns = new ArrayList<LXPattern>();

    TextureSparkle sparkle = new TextureSparkle(lx);
    sparkle.bright.setValue(100);
    patterns.add(sparkle);

    LXChannel channel = lx.engine.addChannel(patterns.toArray(new LXPattern[0]));

    channel.label.setValue("Texture");
    channel.fader.setValue(1);
    channel.transitionEnabled.setValue(true);
    channel.transitionTimeSecs.setValue(1);

    setBlend(channel, MultiplyBlend.class);
  }

  private static void setupOverlayChannel(LX lx) {
    ArrayList<LXPattern> patterns = new ArrayList<LXPattern>();

    patterns.add(new NonePattern(lx));
    patterns.add(new FinalPattern(lx));

    LXChannel channel = lx.engine.addChannel(patterns.toArray(new LXPattern[0]));

    channel.label.setValue("Overlay");
    channel.fader.setValue(1);

    setBlend(channel, NormalBlend.class);
  }

  private static void setBlend(LXChannel channel, Class<? extends LXBlend> blend) {
    for (LXBlend blendMode : channel.blendMode.getObjects()) {
      if (blendMode.getClass() == blend) {
        channel.blendMode.setValue(blendMode);
      }
    }
  }
}
