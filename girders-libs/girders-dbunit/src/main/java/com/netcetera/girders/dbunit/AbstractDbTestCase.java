package com.netcetera.girders.dbunit;

import com.netcetera.girders.dbunit.datamanagement.BasicDataManagementProvider;
import com.netcetera.girders.dbunit.datamanagement.DataManagementProvider;
import com.netcetera.girders.dbunit.dataset.DataSetProvider;
import org.dbunit.IDatabaseTester;
import org.dbunit.IOperationListener;
import org.dbunit.operation.DatabaseOperation;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import javax.sql.DataSource;

import static com.google.common.base.Strings.emptyToNull;

/**
 * Base class for a DB test case with the DB initialized from a {@link DataManagementProvider#databaseTester
 * IDatabaseTester}.
 */
@SuppressWarnings({"JavadocReference", "MethodMayBeStatic", "WeakerAccess"})
public abstract class AbstractDbTestCase {

  @Autowired
  private DbTestCaseConfigurationProperties configurationProperties;

  /**
   * Data source for the test case.
   */
  @Autowired
  private DataSource dataSource;

  /**
   * Transaction manager.
   */
  @Autowired
  private PlatformTransactionManager transactionManager;

  /**
   * Provider for the setup and teardown of the data base.
   */
  private final DataManagementProvider dataManagementProvider;

  /**
   * Provider for the data set to be used.
   */
  private final DataSetProvider dataSetProvider;

  /**
   * Returns the database setup operation.
   * <p>
   * Override this method if you don't want to use the default database setup operation
   * {@link DatabaseOperation#CLEAN_INSERT}.
   * </p>
   *
   * @return the database setup operation to use for a test.
   */
  public DatabaseOperation getDbUnitSetUpOperation() {
    return DatabaseOperation.CLEAN_INSERT;
  }

  /**
   * Returns the database tear-down operation.
   * <p>
   * Override this method if you don't want to use the default database tear-down operation
   * {@link DatabaseOperation#NONE} (which actually does nothing on tear-down).
   * </p>
   *
   * @return the database tear-down operation to use for a test.
   */
  public DatabaseOperation getDbUnitTearDownOperation() {
    return DatabaseOperation.NONE;
  }

  /**
   * Returns the {@link IOperationListener} to set on the database tester.
   * <p>
   * Override this method if you want to use a {@link IOperationListener} with the database tester.
   * </p>
   *
   * @return the {@link IOperationListener} to use
   */
  @Nullable
  public IOperationListener getDbUnitOperationListener() {
    return null;
  }

  /**
   * By default, a {@link BasicDataManagementProvider} is used.
   */
  protected AbstractDbTestCase(DataSetProvider dataSetProvider) {
    this(new BasicDataManagementProvider(), dataSetProvider);
  }

  /**
   * To use a {@link DataManagementProvider} other than a {@link BasicDataManagementProvider}, use this constructor.
   *
   * <p>
   * If you want to use different data set files per test method within a test class, or a single data set file that
   * is named differently from the concrete test class, then you can subclass this class, use this constructor, and
   * call {@link DataSetProvider#setDataSetName(String)} within the constructor.
   * </p>
   *
   * @param dataManagementProvider the provider to use
   */
  protected AbstractDbTestCase(DataManagementProvider dataManagementProvider, DataSetProvider dataSetProvider) {
    this.dataManagementProvider = dataManagementProvider;
    this.dataSetProvider = dataSetProvider;

    setDataSetName(getClass().getSimpleName());
    setTestClass(getClass());
  }

  /**
   * Sets the name of the data set. Use this to change the name of the file containing the data set to something
   * other than {@code getClass().getSimpleName()}; for the change to work, you need to call this method after the
   * constructor, but before {@link DataManagementProvider#setUpDataBase(DataSetProvider,
   * DatabaseOperation, DatabaseOperation, IOperationListener, DataSource, DbTestCaseConfigurationProperties, String)}.
   *
   * <p>
   * Since {@link BeforeEach}-annotated methods are inherited from superclasses and executed before
   * {@link BeforeEach}-annotated methods of subclasses unless they are overridden, you will have to override
   * {@link #setUpDatabase()} (copy-pasting it into the subclass should be enough) if you want to use different data
   * sets for different test methods in the same test class.
   * </p>
   *
   * @param dataSetName The name of the data set.
   */
  public final void setDataSetName(String dataSetName) {
    dataSetProvider.setDataSetName(dataSetName);
  }

  /**
   * Sets the class in whose package the data set file is located on the class path. Use this to change it to
   * something other than {@link #getClass}; for the change to work, you need to call this method after the
   * constructor, but before {@link DataManagementProvider#setUpDataBase(DataSetProvider,
   * DatabaseOperation, DatabaseOperation, IOperationListener, DataSource, DbTestCaseConfigurationProperties, String)}.
   *
   * @param testClass The class in whose package the data set files reside.
   */
  public final void setTestClass(Class<? extends AbstractDbTestCase> testClass) {
    dataSetProvider.setTestClass(testClass);
  }

  /**
   * Returns the DB schema to use.
   * <p>
   * Override this method if you want to set a schema for use with the database tester.
   * </p>
   *
   * @return the schema to use (defaults to the configuration value used for
   * {@code hibernate.default_schema})
   */
  public String getSchema() {
    return emptyToNull(configurationProperties.getSchema());
  }

  /**
   * Sets up the database.
   *
   * @throws Exception if something goes wrong
   */
  @SuppressWarnings("ProhibitedExceptionDeclared")
  @BeforeEach
  public final void setUpDatabase()
    // CHECKSTYLE:OFF
      throws Exception {
    // CHECKSTYLE:ON

    IDatabaseTester databaseTester = new VendorAwareDataSourceDatabaseTester(dataSource,
        configurationProperties.getConnectionAdapter(), getSchema());
    databaseTester.setOperationListener(getDbUnitOperationListener());
    databaseTester.setDataSet(dataSetProvider.getDbUnitDataSet());
    databaseTester.setSetUpOperation(getDbUnitSetUpOperation());
    databaseTester.setTearDownOperation(getDbUnitTearDownOperation());

    dataManagementProvider.setUpDataBase(databaseTester, dataSetProvider);
  }

  /**
   * Tears down the database.
   *
   * @throws Exception if something goes wrong
   */
  @SuppressWarnings("ProhibitedExceptionDeclared")
  @AfterEach
  public final void tearDownDatabase()
    // CHECKSTYLE:OFF
      throws Exception {
    // CHECKSTYLE:ON
    dataManagementProvider.tearDownDatabase();
  }

  /**
   * Creates a transaction template.
   *
   * @return the transaction template
   */
  public TransactionTemplate createTransactionTemplate() {
    return new TransactionTemplate(transactionManager);
  }
}
