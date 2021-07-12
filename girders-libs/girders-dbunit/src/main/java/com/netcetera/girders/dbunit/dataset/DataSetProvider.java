package com.netcetera.girders.dbunit.dataset;

import com.netcetera.girders.dbunit.AbstractDbTestCase;
import com.netcetera.girders.dbunit.datamanagement.ParallelizedDataManagementProvider;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.IDataSet;

import java.io.IOException;
import java.util.Optional;

/**
 * Provides the {@link #getDbUnitDataSet() data set} of the test case as well as a
 * {@link #getDataSetName() name} for it. The source of the data set is some file with the name of the data set (with a
 * suitable  suffix depending on the concrete implementation of this class) located in the same package as the
 * {@link #getTestClass() test class}, so the data set gets defined by calling {@link #setTestClass(Class)} and
 * {@link #setDataSetName(String)}. Prior to calling both those methods, the data set is undefined.
 *
 * <p>
 *   Additionally, provides an {@link #getDbUnitUpdateDataSet() update data set} that follows a similar naming scheme
 *   (see concrete subclasses of this class for details) that will be used to {@code UPDATE} the database after the
 *   setup has been run for the {@link #getDbUnitDataSet() data set}.
 * </p>
 */
public abstract class DataSetProvider {

  private Class<? extends AbstractDbTestCase> testClass;

  /**
   * The name of the data set (not that of the file itself, although the file name may be derived from the name of the
   * data set by concrete {@link DataSetProvider}s though).
   */
  private String dataSetName;

  /**
   * Defines the class in whose package the data set file lives.
   *
   * @param testClass .
   */
  public void setTestClass(Class<? extends AbstractDbTestCase> testClass) {
    this.testClass = testClass;
  }

  /**
   * Returns the class in whose package the data set file lives.
   *
   * @return the test class type
   */
  public Class<? extends AbstractDbTestCase> getTestClass() {
    return testClass;
  }

  /**
   * Sets the name of the data set.
   *
   * @param dataSetName .
   */
  public void setDataSetName(String dataSetName) {
    this.dataSetName = dataSetName;
  }

  /**
   * Returns a name for the data set.
   *
   * @return the name of the data set
   */
  public String getDataSetName() {
    return dataSetName;
  }

  /**
   * Returns the {@link IDataSet} to use for a test.
   */
  public abstract IDataSet getDbUnitDataSet() throws DataSetException, IOException;

  /**
   * Returns the update {@link IDataSet} to use for a test , or {@code null} if no update data set is defined. Only
   * used by {@link ParallelizedDataManagementProvider}.
   */
  public abstract Optional<IDataSet> getDbUnitUpdateDataSet() throws DataSetException, IOException;
}
