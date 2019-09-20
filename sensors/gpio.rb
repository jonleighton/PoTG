# Pin   Name    Purpose Pin     Name    Purpose
# 01                    02
# 03    		04
# 05    		06
# 07    BCM4    P_03    08      BCM14   A_01
# 09                    10      BCM15   A_02
# 11    BCM17   P_04    12      BCM18   A_03
# 13    BCM27   P_05    14
# 15    BCM22   P_06    16      BCM23   A_04
# 17                    18      BCM24   A_05
# 19    BCM10   P_07    20
# 21    BCM9    P_08    22      BCM25   A_06
# 23    BCM11   P_09    24      BCM8    A_07
# 25                    26      BCM7    A_08
# 27    BCM0    P_10    28      BCM1    A_09
# 29                    30
# 31    BCM6    Mode_4  32      BCM12   A_10
# 33    BCM13   Mode_3  34
# 35    BCM19   Mode_2  36
# 37    BCM26   Mode_1  38      BCM20   P_01
# 39                    40      BCM21   P_02

class GPIO
  # BCM pin numbers
  PILLAR_PINS = [20, 21, 4, 17, 27, 22, 10, 9, 11, 0]

  LOOP_DELAY = 0.050 # 50ms

  attr_reader :pillar_circle

  def initialize(pillar_circle)
    # Doing this require late allows us to run the non-GPIO code on a normal
    # computer
    require "rpi_gpio"

    @pillar_circle = pillar_circle

    @gpio = RPi::GPIO
    @gpio.set_numbering :bcm
    at_exit { @gpio.clean_up }

    PILLAR_PINS.each do |pin|
      @gpio.setup(pin, as: :input, pull: :down)
    end
  end

  def run
    loop do
      process_sensors
      sleep LOOP_DELAY
    end
  end

  def process_sensors
    PILLAR_PINS.each.with_index do |pin, index|
      if @gpio.high?(pin)
        @pillar_circle.process_sensor_on(index)
      elsif @gpio.low?(pin)
        @pillar_circle.process_sensor_off(index)
      else
        # Ignore floating input
      end
    end
  end
end
