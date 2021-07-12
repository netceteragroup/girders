package com.netcetera.girders.autoconfigure.dbunit;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties for the dbUnit feature.
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "girders.dbunit", ignoreUnknownFields = false)
public class DbUnitProperties {

  /**
   * The connection adapter to be used. One of the following is supported:
   * <ul>
   *   <li>org.dbunit.ext.h2.H2Connection</li>
   *   <li>org.dbunit.ext.mssql.MsSqlConnection</li>
   *   <li>org.dbunit.ext.mysql.MySqlConnection</li>
   *   <li>org.dbunit.ext.oracle.OracleConnection</li>
   *   <li>org.dbunit.ext.postgresql.PostgreSQLConnection</li>
   * </ul>
   */
  private String connectionAdapter = null;

  /**
   * The schema name to be used.
   */
  private String schema = null;
}
