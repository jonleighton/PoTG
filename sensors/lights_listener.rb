class LightsListener
  attr_reader :control_path

  def initialize(control_path = ENV.fetch("POTG_LIGHTS_CONTROL", "bin/lights-control"))
    @control_path = control_path
  end

  def call(entity, state)
    case entity
    when PillarCircle
      process_pillar_circle(state)
    when Pillar
      process_pillar(entity, state)
    else
      raise ArgumentError, "Unknown entity #{entity.inspect}"
    end
  end

  def process_pillar_circle(state)
    case state
    when :normal
      # Do nothing, this doesn't affect the lights
    when :final
      run "final"
    else
      raise ArgumentError, "Unknown pillar circle state #{state}"
    end
  end

  def process_pillar(pillar, state)
    case state
    when :dormant
      run "deactivate", pillar.number
    when :active
      run "activate", pillar.number
    else
      raise ArgumentError, "Unknown pillar state #{state}"
    end
  end

  def run(*args)
    Kernel.system(control_path, *args.map(&:to_s))
  end
end
