package com.netcetera.girders.dbunit;

import lombok.extern.slf4j.Slf4j;
import org.dbunit.DataSourceDatabaseTester;
import org.dbunit.database.IDatabaseConnection;

import javax.sql.DataSource;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * A vendor specific data source.
 */
@Slf4j
public class VendorAwareDataSourceDatabaseTester extends DataSourceDatabaseTester {

  private final DataSource dataSource;

  private final Class<IDatabaseConnection> connectionAdapter;

  /**
   * Constructor.
   *
   * @param dataSource dataSource
   * @param connectionAdapterClassName connection adapter
   * @param schema the DB schema to use
   * @throws ClassNotFoundException is thrown if connection adapter can not be found
   */
  @SuppressWarnings("unchecked")
  public VendorAwareDataSourceDatabaseTester(DataSource dataSource,
      String connectionAdapterClassName,
      String schema) throws ClassNotFoundException {
    super(dataSource, schema);
    logger.info("Using schema: {}", schema);
    this.dataSource = dataSource;
    connectionAdapter = (Class<IDatabaseConnection>) Class.forName(connectionAdapterClassName);
    logger.info("Using connection adapter: {}", connectionAdapterClassName);
  }

  @Override
  public IDatabaseConnection getConnection() throws NoSuchMethodException,
      InvocationTargetException, InstantiationException, IllegalAccessException, SQLException {
    return connectionAdapter.getConstructor(Connection.class, String.class).newInstance(dataSource.getConnection(),
        getSchema());
  }
}
