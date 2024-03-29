import java.io.File;
import heronarts.lx.LX;
import heronarts.lx.model.LXModel;
import heronarts.lx.LXPattern;
import heronarts.lx.LXEngine;
import heronarts.lx.LXChannel;
import heronarts.lx.LXChannelBus;
import heronarts.lx.effect.StrobeEffect;
import heronarts.lx.blend.LXBlend;
import heronarts.lx.blend.MultiplyBlend;
import heronarts.lx.blend.NormalBlend;
import heronarts.lx.blend.SubtractBlend;
import heronarts.lx.pattern.GradientPattern;
import heronarts.lx.audio.BandGate;
import heronarts.lx.parameter.LXCompoundModulation;
import heronarts.lx.color.LXPalette;

import java.util.ArrayList;

public class Project {
  // This is the entrypoint for running headless
  public static void main(String[] args) {
    LX lx = new LX(new Model());
    registerComponents(lx);
    processArgs(lx, args);
    lx.engine.start();
  }

  public static void processArgs(LX lx, String[] args) {
    if (args != null && args.length > 0) {
      lx.openProject(new File(args[0]));
    } else {
      new Project(lx).build();
    }
  }

  private final LX lx;
  private final LXEngine engine;
  private final Model model;

  private static final int HERTZ = 1;
  private static final int KILOHERTZ = 1000 * HERTZ;

  private static final int MILLISECONDS = 1;
  private static final int SECONDS = 1000 * MILLISECONDS;

  public Project(LX lx) {
    this.lx = lx;
    this.engine = lx.engine;
    this.model = (Model) lx.model;
  }

  public static void registerComponents(LX lx) {
    lx.registerPattern(ModelPartsPattern.class);
    lx.registerPattern(DormantPillarPattern.class);
    lx.registerPattern(ActivePillarPattern.class);
    lx.registerPattern(FinalOffPattern.class);
    lx.registerPattern(FinalOnPattern.class);

    lx.registerPattern(TextureSparkle.class);
    lx.registerPattern(TextureCrawl.class);

    lx.registerPattern(PatternClouds.class);
    lx.registerPattern(PatternScanner.class);
    lx.registerPattern(PatternStarlight.class);
    lx.registerPattern(PatternWaves.class);
    lx.registerPattern(PatternVortex.class);

    lx.registerPattern(ColorLighthouse.class);

    lx.registerEffect(StrobeEffect.class);

    try {
      lx.addOutput(new Output(lx));
    } catch (Exception x) {
      System.err.println(x.getLocalizedMessage());
    }
  }

  public void build() {
    lx.newProject();

    engine.setThreaded(true);
    engine.isChannelMultithreaded.setValue(true);
    engine.isNetworkMultithreaded.setValue(true);

    engine.removeChannel(engine.getChannel(0));
    engine.osc.receiveActive.setValue(true);
    engine.crossfader.setValue(-1);

    lx.palette.hueMode.setValue(LXPalette.Mode.CYCLE);
    lx.palette.period.setValue(10 * SECONDS);

    buildPillarChannels();
    buildMiddleChannel();
    buildNormalTextureChannel();

    buildFinalPatternChannel();
    buildFinalMiddleChannel();
    buildFinalColorChannel();
    buildFinalOverlayChannels();
    buildFinalChannel();

    // We'll sample audio from the sound card
    engine.audio.enabled.setValue(true);
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
      channel.crossfadeGroup.setValue(LXChannelBus.CrossfadeGroup.A);
    }
  }

  private void buildMiddleChannel() {
    ArrayList<LXPattern> patterns = new ArrayList<LXPattern>();

    ModelPartsPattern parts = new ModelPartsPattern(lx, false);
    parts.middle.setValue(true);
    patterns.add(parts);

    LXChannel channel = addChannel(patterns);

    channel.label.setValue("Middle");
    channel.fader.setValue(1);
    channel.crossfadeGroup.setValue(LXChannelBus.CrossfadeGroup.A);
  }

  private void buildNormalTextureChannel() {
    ArrayList<LXPattern> patterns = new ArrayList<LXPattern>();

    TextureSparkle sparkle = new TextureSparkle(lx);
    sparkle.bright.setValue(100);
    patterns.add(sparkle);

    patterns.add(new TextureCrawl(lx));

    LXChannel channel = addChannel(patterns);

    channel.label.setValue("Texture");
    channel.fader.setValue(1);
    channel.transitionEnabled.setValue(true);
    channel.transitionTimeSecs.setValue(1);
    channel.crossfadeGroup.setValue(LXChannelBus.CrossfadeGroup.A);

    setBlend(channel, MultiplyBlend.class);
  }

  private void buildFinalPatternChannel() {
    ArrayList<LXPattern> patterns = new ArrayList<LXPattern>();

    PatternScanner scanner = new PatternScanner(lx);
    scanner.speed.setValue(0.56);
    scanner.xSlope.setValue(1);
    scanner.sharp.setValue(30);
    patterns.add(scanner);

    PatternStarlight starlight = new PatternStarlight(lx);
    starlight.speed.setValue(1782);
    patterns.add(starlight);

    PatternWaves waves = new PatternWaves(lx);
    waves.size.setValue(31.7);
    patterns.add(waves);

    PatternVortex vortex = new PatternVortex(lx);
    vortex.speed.setValue(2563);
    patterns.add(vortex);

    LXChannel channel = addChannel(patterns);

    channel.label.setValue("Pattern");
    channel.fader.setValue(1);
    channel.transitionEnabled.setValue(true);
    channel.transitionTimeSecs.setValue(5);
    channel.autoCycleEnabled.setValue(true);
    channel.autoCycleMode.setValue(LXChannel.AutoCycleMode.RANDOM);
    channel.autoCycleTimeSecs.setValue(8);
    channel.crossfadeGroup.setValue(LXChannelBus.CrossfadeGroup.B);
  }

  private void buildFinalMiddleChannel() {
    ArrayList<LXPattern> patterns = new ArrayList<LXPattern>();

    EmanatingMiddlePattern middle = new EmanatingMiddlePattern(lx);
    middle.speed.setValue(1786);
    patterns.add(middle);

    LXChannel channel = addChannel(patterns);

    channel.label.setValue("Middle");
    channel.fader.setValue(1);
    channel.crossfadeGroup.setValue(LXChannelBus.CrossfadeGroup.B);

    setBlend(channel, NormalBlend.class);
  }

  private void buildFinalColorChannel() {
    ArrayList<LXPattern> patterns = new ArrayList<LXPattern>();

    ColorLighthouse lighthouse = new ColorLighthouse(lx);
    lighthouse.speed.setValue(7910);
    lighthouse.spread.setValue(180);
    lighthouse.slope.setValue(1);
    patterns.add(lighthouse);

    ColorRain rain = new ColorRain(lx);
    rain.speed.setValue(1.25);
    rain.range.setValue(50);
    patterns.add(rain);

    ColorSwirl swirl = new ColorSwirl(lx);
    swirl.speed.setValue(1);
    swirl.slope.setValue(1.5);
    patterns.add(swirl);

    LXChannel channel = addChannel(patterns);

    channel.label.setValue("Color");
    channel.fader.setValue(1);
    channel.transitionEnabled.setValue(true);
    channel.transitionTimeSecs.setValue(5);
    channel.autoCycleEnabled.setValue(true);
    channel.autoCycleMode.setValue(LXChannel.AutoCycleMode.RANDOM);
    channel.autoCycleTimeSecs.setValue(10);
    channel.crossfadeGroup.setValue(LXChannelBus.CrossfadeGroup.B);

    setBlend(channel, MultiplyBlend.class);
  }

  private void buildFinalOverlayChannels() {
    BandGate bandGate = new BandGate(lx);
    bandGate.threshold.setValue(0.16);
    bandGate.floor.setValue(1.0);
    bandGate.running.setValue(true);
    bandGate.maxFreq.setValue(16 * KILOHERTZ);
    bandGate.minFreq.setValue(5.33 * KILOHERTZ);

    engine.modulation.addModulator(bandGate);

    ArrayList<LXPattern> patterns = new ArrayList<LXPattern>();

    patterns.add(new HeadsAndMiddlePattern(lx, bandGate));

    CrescendoPattern crescendo = new CrescendoPattern(lx, bandGate);
    crescendo.speed.setValue(500);
    crescendo.size.setValue(0.3);
    patterns.add(crescendo);

    LXChannel channel = addChannel(patterns);

    channel.label.setValue("Overlay");
    channel.transitionEnabled.setValue(true);
    channel.transitionTimeSecs.setValue(20);
    channel.crossfadeGroup.setValue(LXChannelBus.CrossfadeGroup.B);

    LXCompoundModulation modulation = new LXCompoundModulation(bandGate, channel.fader);
    modulation.range.setValue(1);
    modulation.enabled.setValue(true);

    engine.modulation.addModulation(modulation);
  }

  private void buildFinalChannel() {
    ArrayList<LXPattern> patterns = new ArrayList<LXPattern>();

    patterns.add(new FinalOffPattern(lx));
    patterns.add(new FinalOnPattern(lx));

    LXChannel channel = addChannel(patterns);

    channel.label.setValue("Final");
    channel.fader.setValue(1);
    channel.transitionEnabled.setValue(true);
    channel.transitionTimeSecs.setValue(10);
    channel.crossfadeGroup.setValue(LXChannelBus.CrossfadeGroup.B);

    setBlend(channel, SubtractBlend.class);
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
