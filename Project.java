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
    registerComponents(lx);

    if (args.length > 0) {
      lx.openProject(new File(args[0]));
    } else {
      new Project(lx).build();
    }

    lx.engine.start();
  }

  private final LX lx;
  private final LXEngine engine;
  private final Model model;

  public Project(LX lx) {
    this.lx = lx;
    this.engine = lx.engine;
    this.model = (Model) lx.model;
  }

  public static void registerComponents(LX lx) {
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

  public void build() {
    lx.newProject();

    engine.removeChannel(engine.getChannel(0));
    engine.osc.receiveActive.setValue(true);

    buildPillarChannels();
    buildOverlayChannel();
    buildTextureChannel();
  }

  private void buildPillarChannels() {
    for (Model.Pillar pillar : this.model.getPillars()) {
      ArrayList<LXPattern> patterns = new ArrayList<LXPattern>();

      patterns.add(new DormantPillarPattern(lx, pillar.getNumber()));
      patterns.add(new ActivePillarPattern(lx, pillar.getNumber()));

      LXChannel channel = addChannel(patterns);

      channel.label.setValue(String.format("Pillar %s", pillar.getNumber()));
      channel.fader.setValue(1);
      channel.transitionEnabled.setValue(true);
      channel.transitionTimeSecs.setValue(0.5);
    }
  }

  private void buildTextureChannel() {
    ArrayList<LXPattern> patterns = new ArrayList<LXPattern>();

    TextureSparkle sparkle = new TextureSparkle(lx);
    sparkle.bright.setValue(100);
    patterns.add(sparkle);

    LXChannel channel = addChannel(patterns);

    channel.label.setValue("Texture");
    channel.fader.setValue(1);
    channel.transitionEnabled.setValue(true);
    channel.transitionTimeSecs.setValue(1);

    setBlend(channel, MultiplyBlend.class);
  }

  private void buildOverlayChannel() {
    ArrayList<LXPattern> patterns = new ArrayList<LXPattern>();

    patterns.add(new NonePattern(lx));
    patterns.add(new FinalPattern(lx));

    LXChannel channel = addChannel(patterns);

    channel.label.setValue("Overlay");
    channel.fader.setValue(1);

    setBlend(channel, NormalBlend.class);
  }

  private LXChannel addChannel(ArrayList<LXPattern> patterns) {
    return engine.addChannel(patterns.toArray(new LXPattern[0]));
  }

  private void setBlend(LXChannel channel, Class<? extends LXBlend> blend) {
    for (LXBlend blendMode : channel.blendMode.getObjects()) {
      if (blendMode.getClass() == blend) {
        channel.blendMode.setValue(blendMode);
      }
    }
  }
}
