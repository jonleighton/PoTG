require "minitest/autorun"
require_relative "pillar_circle"

class PillarCircleTest < Minitest::Test
  def setup
    @pillar_circle = PillarCircle.new
  end

  def test_pillar_toggling
    assert_instance_of Pillar::DormantState, @pillar_circle.pillar(0).state
    @pillar_circle.process_sensor_on(0)
    assert_instance_of Pillar::ActiveState, @pillar_circle.pillar(0).state
  end

  def test_circle_state_transitions
    PillarCircle::PILLAR_COUNT.times.each do |i|
      assert_instance_of PillarCircle::NormalState, @pillar_circle.state
      @pillar_circle.process_sensor_on(i)
    end

    PillarCircle::PILLAR_COUNT.times.each do |i|
      assert_instance_of PillarCircle::FinalState, @pillar_circle.state
      @pillar_circle.process_sensor_off(i)
    end

    assert_instance_of PillarCircle::NormalState, @pillar_circle.state
  end

  def test_listener
    states = []

    @pillar_circle.add_listener { |entity, state|
      states << [entity, state]
    }

    activation_order = (0...10).to_a.shuffle
    activation_order.each { |index| @pillar_circle.process_sensor_on(index) }

    deactivation_order = (0...10).to_a.shuffle
    deactivation_order.each { |index| @pillar_circle.process_sensor_off(index) }

    assert_equal states, [
      *activation_order.map { |index| [@pillar_circle.pillar(index), :active] },
      [@pillar_circle, :final],
      *deactivation_order.map { |index| [@pillar_circle.pillar(index), :dormant] },
      [@pillar_circle, :normal]
    ]
  end
end
