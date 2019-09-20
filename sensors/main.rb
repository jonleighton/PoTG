require_relative "pillar_circle"
require_relative "logging_listener"
require_relative "gpio"

PILLAR_CIRCLE = PillarCircle.new
PILLAR_CIRCLE.add_listener(LoggingListener.new)

# TODO
# PILLAR_CIRCLE.add_listener(LightsListener.new)
