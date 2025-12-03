package ssafy.batt.common.logger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogbackLogger {

  private static final Logger logger = LoggerFactory.getLogger(LogbackLogger.class);

  void method() {
    logger.trace("Trace");
    logger.debug("Debug");
    logger.info("Info");
    logger.warn("Warn");
    logger.error("Error");
  }
}
