class Pillar
  class State
    attr_reader :pillar

    def initialize(pillar)
      @pillar = pillar
    end

    def circle
      pillar.circle
    end
  end

  class DormantState < State
    def process_sensor_on
      if circle.normal?
        ActiveState.new(pillar)
      else
        self
      end
    end

    def process_sensor_off
      self
    end

    def to_sym
      :dormant
    end
  end

  class ActiveState < State
    def process_sensor_on
      self
    end

    def process_sensor_off
      if circle.final?
        DormantState.new(pillar)
      else
        self
      end
    end

    def to_sym
      :active
    end
  end

  attr_reader :circle, :state, :index

  def initialize(circle, index)
    @circle = circle
    @index = index
    @state = DormantState.new(self)
    @listeners = []
  end

  def add_listener(callable = nil, &block)
    callable ||= block
    @listeners << callable
  end

  def process_sensor_on
    update_state state.process_sensor_on
  end

  def process_sensor_off
    update_state state.process_sensor_off
  end

  def active?
    state.is_a? ActiveState
  end

  def dormant?
    state.is_a? DormantState
  end

  def number
    index + 1
  end

  private

  def update_state(new_state)
    prev_state = state
    @state = new_state

    if @state != prev_state
      @listeners.each { |listener| listener.call(self, @state.to_sym) }
    end
  end
end
