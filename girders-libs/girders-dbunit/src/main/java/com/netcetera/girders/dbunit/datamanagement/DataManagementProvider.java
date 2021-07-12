package com.netcetera.girders.dbunit.datamanagement;

import com.netcetera.girders.dbunit.dataset.DataSetProvider;
import lombok.Getter;
import lombok.Setter;
import org.dbunit.IDatabaseTester;

import static lombok.AccessLevel.PACKAGE;

/**
 * Determines which operations are performed on setup and teardown of each test case.
 */
public abstract class DataManagementProvider {
  
  /**
   * Data base tester instance.
   */
  @Getter(PACKAGE)
  @Setter(PACKAGE)
  private IDatabaseTester databaseTester;

  /**
   * Sets up the database.
   *
   * @throws Exception if something goes wrong
   */
  @SuppressWarnings("ProhibitedExceptionDeclared")
  public abstract void setUpDataBase(IDatabaseTester databaseTester, DataSetProvider dataSetProvider)

  // CHECKSTYLE:OFF
      throws Exception;
  // CHECKSTYLE:ON

  /**
   * Tears down the database.
   *
   * @throws Exception if something goes wrong
   */
  @SuppressWarnings("ProhibitedExceptionDeclared")
  public abstract void tearDownDatabase()
    // CHECKSTYLE:OFF
      throws Exception;
    // CHECKSTYLE:ON
}
