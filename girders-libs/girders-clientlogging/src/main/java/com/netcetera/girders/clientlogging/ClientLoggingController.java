package com.netcetera.girders.clientlogging;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * Controller handling client side log data (i.e. logging them).
 */
@RequestMapping("/log")
@Slf4j
@Api(value = "log", tags = "girders", consumes = "application/json", produces = "application/json",
    description = "Logging")
public class ClientLoggingController {

  /**
   * Writes client side log data to the server log.
   *
   * @param clientLogEntry client log data to log
   */
  @PostMapping(consumes = APPLICATION_JSON_VALUE)
  @ResponseStatus(OK)
  @ApiOperation(value = "Add log message",
      notes = "Add a log message from a client to the server-side logging component.")
  public void post(@RequestBody ClientLogEntry clientLogEntry) {

    checkPreconditions(clientLogEntry);

    switch (clientLogEntry.getLogLevel().toUpperCase()) {
      case "INFO":
        logger.info(clientLogEntry.joinValues());
        break;
      case "WARN":
        logger.warn(clientLogEntry.joinValues());
        break;
      case "ERROR":
        logger.error(clientLogEntry.joinValues());
        break;
      case "LOG":
        logger.trace(clientLogEntry.joinValues());
        break;
      case "DEBUG":
      default:
        logger.debug(clientLogEntry.joinValues());
        break;
    }
  }

  private static void checkPreconditions(ClientLogEntry clientLogEntry) {
    if (clientLogEntry == null) {
      throw new IllegalArgumentException("log entry must not be null");
    }
    if (clientLogEntry.getLogLevel() == null) {
      throw new IllegalArgumentException("log level must not be null");
    }
    if (StringUtils.isEmpty(clientLogEntry.getMessage())) {
      throw new IllegalArgumentException("log message must not be empty");
    }
  }

}
