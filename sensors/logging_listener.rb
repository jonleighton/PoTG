require "logger"

class LoggingListener
  def initialize(io = $stdout)
    @logger = Logger.new(io)
  end

  def call(entity, state)
    @logger.info("#{entity_name(entity)} transitioned to #{state}")
  end

  private

  def entity_name(entity)
    case entity
    when PillarCircle
      "Pillar Circle"
    when Pillar
      "Pillar #{entity.number}"
    else
      raise Argument Error, "Unknown entity #{entity.inspect}"
    end
  end
end
