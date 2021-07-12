package com.netcetera.girders.dbunit.datamanagement;

import com.netcetera.girders.dbunit.dataset.DataSetProvider;
import org.dbunit.IDatabaseTester;

/**
 * Performs a delete-all-then-insert operation for the data set.
 */
public class BasicDataManagementProvider extends DataManagementProvider {


  @SuppressWarnings("ProhibitedExceptionDeclared")
  @Override
  public void setUpDataBase(IDatabaseTester databaseTester,
      DataSetProvider dataSetProvider) throws Exception {

    setDatabaseTester(databaseTester);
    databaseTester.onSetup();
  }

  @SuppressWarnings("ProhibitedExceptionDeclared")
  @Override
  public void tearDownDatabase() throws Exception {
    getDatabaseTester().onTearDown();
  }
}
