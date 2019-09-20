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
  #
  # Pillars 3 and 10 aren't mapped to where you would think as the P3 and P10
  # ports on the board weren't working properly.
  PILLAR_PINS = [
    20, # Pillar 1  - P1 on board
    21, # Pillar 2  - P2 on board
    23, # Pillar 3  - A4 on board (different!)
    17, # Pillar 4  - P4 on board
    27, # Pillar 5  - P5 on board
    22, # Pillar 6  - P6 on board
    10, # Pillar 7  - P7 on board
    9,  # Pillar 8  - P8 on board
    11, # Pillar 9  - P9 on board
    12  # Pillar 10 - A10 on board (different!)
  ]

  LOOP_DELAY = 0.050 # 50ms

  # Require this number of consecutive identical readings to consider it an
  # accurate reading.
  CONSECUTIVE_READINGS = 2

  ALL_HIGH_READINGS = CONSECUTIVE_READINGS.times.map { :high }
  ALL_LOW_READINGS = CONSECUTIVE_READINGS.times.map { :low }

  attr_reader :pillar_circle

  def initialize(pillar_circle)
    # Doing this require late allows us to run the non-GPIO code on a normal
    # computer
    require "rpi_gpio"

    @pillar_circle = pillar_circle

    @readings = PILLAR_PINS.map { [] }

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
      pillar_readings = @readings[index]

      pillar_readings.unshift(reading_for(pin))
      pillar_readings.slice!(CONSECUTIVE_READINGS, pillar_readings.length)

      next unless pillar_readings.length == CONSECUTIVE_READINGS

      if pillar_readings == ALL_HIGH_READINGS
        @pillar_circle.process_sensor_on(index)
      elsif pillar_readings == ALL_LOW_READINGS
        @pillar_circle.process_sensor_off(index)
      end
    end
  end

  def reading_for(pin)
    if @gpio.high?(pin)
      :high
    elsif @gpio.low?(pin)
      :low
    else
      :floating
    end
  end
end
