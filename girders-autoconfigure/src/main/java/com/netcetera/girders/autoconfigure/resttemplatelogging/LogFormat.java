package com.netcetera.girders.autoconfigure.resttemplatelogging;

/**
 * Supported formats for the logging of REST template client requests.
 */
public enum LogFormat {

  /**
   * Log format primarily designed to be used for local development and debugging,
   * not for production use. This is because the output is spread over multiple
   * lines and it’s not as readily machine-readable as the JSON log format.
   */
  PRETTY_PRINT,

  /**
   * Log format primarily designed for production use since it outputs the data as
   * a JSON object on a single line which makes it easy to further process the log
   * output. The structure of the JSON object is inspired by Zalando's Logbook
   * library but the contents will not be identical.
   */
  JSON
}
