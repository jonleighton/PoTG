require_relative "pillar_circle"
require_relative "logging_listener"

PILLAR_CIRCLE = PillarCircle.new
PILLAR_CIRCLE.add_listener(LoggingListener.new)

if $0 == __FILE__
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

  PILLAR_PINS = [38, 40, 7, 11, 13, 15, 19, 21, 23, 27]
  LOOP_DELAY = 0.050 # 50ms

  require "rpi_gpio"

  GPIO = RPi::GPIO
  GPIO.set_numbering :board

  at_exit { GPIO.clean_up }

  PILLAR_PINS.each do |pin|
    GPIO.setup(pin, as: :input, pull: :down)
  end

  loop do
    PILLAR_PINS.each.with_index do |pin, index|
      if GPIO.high?(pin)
        PILLAR_CIRCLE.process_sensor_on(index)
      elsif GPIO.low?(pin)
        PILLAR_CIRCLE.process_sensor_off(index)
      else
        # Ignore floating input
      end
    end

    sleep LOOP_DELAY
  end
end
