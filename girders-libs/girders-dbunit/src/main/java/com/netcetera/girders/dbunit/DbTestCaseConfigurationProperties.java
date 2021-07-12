package com.netcetera.girders.dbunit;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * The configuration properties necessary for configuring dbUnit. This is either provided by auto-configuration or
 * manually by the application.
 */
@RequiredArgsConstructor
@Getter
public class DbTestCaseConfigurationProperties {

  private final String connectionAdapter;
  private final String schema;

}
