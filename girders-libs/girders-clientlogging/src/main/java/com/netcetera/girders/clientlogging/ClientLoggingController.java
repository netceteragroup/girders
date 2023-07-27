package com.netcetera.girders.clientlogging;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.Locale;

import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * Controller handling client side log data (i.e. logging them).
 */
@RequestMapping("/log")
@Slf4j
@RestController
@Tag(name = "girders", description = "Logging")
public class ClientLoggingController {

  /**
   * Writes client side log data to the server log.
   *
   * @param clientLogEntry client log data to log
   */
  @PostMapping(consumes = APPLICATION_JSON_VALUE)
  @ResponseStatus(OK)
  @Operation(summary = "Add log message",
      description = "Add a log message from a client to the server-side logging component.")
  public void post(@RequestBody ClientLogEntry clientLogEntry) {

    checkPreconditions(clientLogEntry);

    switch (clientLogEntry.getLogLevel().toUpperCase(Locale.ROOT)) {
      case "INFO" -> logger.info(clientLogEntry.joinValues());
      case "WARN" -> logger.warn(clientLogEntry.joinValues());
      case "ERROR" -> logger.error(clientLogEntry.joinValues());
      case "LOG" -> logger.trace(clientLogEntry.joinValues());
      default -> logger.debug(clientLogEntry.joinValues());
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
