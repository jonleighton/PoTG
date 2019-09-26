require "minitest/autorun"
require_relative "pillar_circle"

class PillarCircleTest < Minitest::Test
  def setup
    @pillar_circle = PillarCircle.new
  end

  def test_pillar_toggling
    pillar = @pillar_circle.pillar(0)
    assert_instance_of Pillar::DormantState, pillar.state

    @pillar_circle.process_sensor_on(0)
    assert_instance_of Pillar::ActiveState, pillar.state

    # This *should* allow the pillar to be de-activated and then re-activated
    @pillar_circle.process_sensor_off(0)
    assert_instance_of Pillar::DormantState, pillar.state

    @pillar_circle.process_sensor_on(0)
    assert_instance_of Pillar::ActiveState, pillar.state

    PillarCircle::PILLAR_COUNT.times.each { |i| @pillar_circle.process_sensor_on(i) }
    assert_instance_of PillarCircle::FinalState, @pillar_circle.state

    @pillar_circle.process_sensor_off(0)
    assert_instance_of Pillar::DormantState, pillar.state

    # This shouldn't now allow the pillar to be re-activated until the normal
    # state of the pillar circle has been reached.
    @pillar_circle.process_sensor_on(0)
    assert_instance_of Pillar::DormantState, pillar.state

    PillarCircle::PILLAR_COUNT.times.each { |i| @pillar_circle.process_sensor_off(i) }
    assert_instance_of PillarCircle::NormalState, @pillar_circle.state

    # Now we can go again
    @pillar_circle.process_sensor_on(0)
    assert_instance_of Pillar::ActiveState, pillar.state
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
