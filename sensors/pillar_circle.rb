require_relative "pillar"

class PillarCircle
  class NormalState
    def process_pillars(pillars)
      pillars.all?(&:active?) ? FinalState.new : self
    end

    def to_sym
      :normal
    end
  end

  class FinalState
    def process_pillars(pillars)
      pillars.all?(&:dormant?) ? NormalState.new : self
    end

    def to_sym
      :final
    end
  end

  PILLAR_COUNT = 10

  attr_reader :state

  def initialize
    @state = NormalState.new
    @pillars = PILLAR_COUNT.times.map { |i| Pillar.new(i) }
    @listeners = []
  end

  def add_listener(callable = nil, &block)
    callable ||= block
    @listeners << callable
    @pillars.each { |pillar| pillar.add_listener(callable) }
  end

  def pillar(index)
    @pillars.fetch(index)
  end

  def process_sensor_on(index)
    pillar(index).process_sensor_on
    update_state
  end

  def process_sensor_off(index)
    pillar(index).process_sensor_off
    update_state
  end

  private

  def update_state
    prev_state = state
    @state = prev_state.process_pillars(@pillars)

    if @state != prev_state
      @listeners.each { |listener| listener.call(self, @state.to_sym) }
    end
  end
end
