package com.netcetera.girders.clientlogging;

import com.google.common.base.Joiner;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Model class for log data entry.
 */
@Getter
@Setter
@Schema(description = "Data object for a log statement from the client")
public class ClientLogEntry {

  private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(
      "yyyy-MM-dd'T'HH:mm:ss.SSSXXX");

  /**
   * Time from the client.
   */
  @Schema(name = "client timestamp", description = "Timestamp in format yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
  private ZonedDateTime clientTime;

  /**
   * Level of the log message.
   */
  @Schema(name = "logLevel", description = "DEBUG, INFO, WARN or ERROR", example = "INFO", required = true)
  private String logLevel;

  /**
   * Message from the client.
   */
  @Schema(name = "message", description = "Log message", example = "Hello World", required = true)
  private String message;

  /**
   * Exception cause. Only valid for client side exceptions.
   */
  @Schema(name = "cause", description = "Cause of the log statement", example = "An error occurred")
  private String cause;

  /**
   * Exception stack trace. Only valid for client side exceptions.
   */
  @Schema(name = "stacktrace", description = "Stack trace for the problem")
  private String stackTrace;

  /**
   * URL on which exception happened. Only valid for client side exceptions.
   */
  @Schema(name = "url", description = "URL on which the error happened", example = "/calculation.js")
  private String url;

  /**
   * Joins all individual values on ' | '.
   *
   * @return concatenated message from all the client log data
   */
  public String joinValues() {
    Joiner joiner = Joiner.on(" | ").skipNulls();
    return joiner.join((clientTime != null) ? DATE_TIME_FORMATTER.format(clientTime) : null, url, message, cause,
        stackTrace);
  }

}
