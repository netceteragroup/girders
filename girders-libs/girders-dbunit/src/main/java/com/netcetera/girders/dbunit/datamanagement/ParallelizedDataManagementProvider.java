package com.netcetera.girders.dbunit.datamanagement;

import com.netcetera.girders.dbunit.dataset.DataSetProvider;
import org.dbunit.IDatabaseTester;
import org.dbunit.dataset.IDataSet;
import org.dbunit.operation.DatabaseOperation;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static java.lang.Boolean.TRUE;

/**
 * Provides data management for parallelized tests. Every data set will be loaded exactly once, then this data
 * management provider will not interact with the database anymore.
 * <p>
 * Using this data management provider requires that:
 * </p> *
 * <ul>
 *   <li>the test data for a given test case is fully contained within the data set that the {@link DataSetProvider}
 *       supplies for that test case, and the data for each test case is independent of the data for all other test
 *       cases</li>
 *   <li>each data set is self-contained (i.e. loading any one of them on its own never produces any constraint
 *       violations)</li>
 *   <li>the union of all data sets that are loaded in parallel does not contain any constraint violations</li>
 *   <li>the {@link DataSetProvider} needs to provide unique names for all data sets being loaded in parallel</li>
 * </ul>
 *
 */
public class ParallelizedDataManagementProvider extends DataManagementProvider {

  private final DatabaseOperation updateOperation;

  /**
   * .
   */
  public ParallelizedDataManagementProvider() {
    this(DatabaseOperation.UPDATE);
  }

  /**
   *
   * @param updateOperation the operation used to update the database with the
   * {@link DataSetProvider#getDbUnitUpdateDataSet() update data set}.
   */
  @SuppressWarnings("WeakerAccess") // Public API.
  public ParallelizedDataManagementProvider(DatabaseOperation updateOperation) {
    this.updateOperation = updateOperation;
  }

  /**
   * We want to make sure every data set is only loaded once, but we also want parallelized test execution.
   * Therefore we need to do two things:
   * <ul>
   *   <li>have separate flags on a per-data-set basis that tell us whether a given data set has been loaded yet</li>
   *   <li>make that check atomic so there can be no race conditions between threads spawned for test cases for which
   *       the {@link DataSetProvider} provides the same data set</li>
   * </ul>
   * <p>
   * We do the following: for each data set, we keep a flag to determine whether some thread has started loading
   * it. Because JUnit creates a separate instance of the test class for every test method being run, we need to have
   * those flags in a {@code static} context, and access to the flags has to be atomic. We cannot override static
   * methods or fields in Java, so this {@link ConcurrentMap} maps data sets to their flags. The Java standard
   * libraries don't offer a concurrent {@link Set} implementation that's more convenient than this, and having it be
   * a {@link Map} won't cause much overhead. </p>
   * <p>
   * {@link #FINISHED_LOADING} marks whether a given data set has finished loading.
   * </p>
   */
  private static final Map<String, Boolean> STARTED_LOADING = new ConcurrentHashMap<>();

  /**
   * Marks whether a given data set has been loaded.
   *
   * @see #STARTED_LOADING
   */
  private static final Collection<String> FINISHED_LOADING = new HashSet<>();


  @SuppressWarnings("ProhibitedExceptionDeclared")
  @Override
  public void setUpDataBase(IDatabaseTester databaseTester, DataSetProvider dataSetProvider)
      // CHECKSTYLE:OFF
        throws Exception {
      // CHECKSTYLE:ON

    // adding the canonical name of the test class to this dataSetIdentifier that we use to identify the data set
    // makes it possible to have data set files with the same file name for separate test classes
    String dataSetIdentifier = dataSetProvider.getTestClass().getCanonicalName() + dataSetProvider.getDataSetName();

    // Map.put(key, newValue) returns the value previously mapped by the given key before the call, so the first thread
    // that executes this .put will get null, all subsequent calls will get TRUE; works because it's a ConcurrentHashMap
    Boolean someThreadStartedLoadingDataSet = STARTED_LOADING.put(dataSetIdentifier, TRUE);

    //noinspection VariableNotUsedInsideIf
    if (someThreadStartedLoadingDataSet == null) { // the current thread is the first one to try to load the data set
                                                   // -> it starts loading it
      try {
        databaseTester.onSetup();
        updateData(dataSetProvider, databaseTester);
      } finally {
        // Synchronization through a constructor object would complicate the usage for integration projects. Performance
        // seams to be fine like this.
        //noinspection SynchronizationOnStaticField
        synchronized (FINISHED_LOADING) {
          FINISHED_LOADING.add(dataSetIdentifier);
          FINISHED_LOADING.notifyAll();
        }
      }
    } else { // the current thread is not the first one to try to load the data set -> wait until the thread loading
             // the data set finishes

      // Synchronization through a constructor object would complicate the usage for integration projects. Performance
      // seams to be fine like this.
      //noinspection SynchronizationOnStaticField
      synchronized (FINISHED_LOADING) {
        while (!FINISHED_LOADING.contains(dataSetIdentifier)) {
          //noinspection WaitOrAwaitWithoutTimeout
          FINISHED_LOADING.wait();
        }
      }
    }
  }

  /**
   * <p>We cannot insert objects with circular foreign key references with {@link DatabaseOperation#INSERT} because to
   * do that we would have to disable foreign key checks during the insert, but due to this data management provider
   * being used for pparallelized tests, there is no suitable moment to re-enable them, so tests using this data
   * management provider would run without foreign key checks. Instead, keep the checks enabled throughout and
   * separate the data set into the {@link DataSetProvider#getDbUnitDataSet() initial data} and the
   * {@link DataSetProvider#getDbUnitUpdateDataSet() UPDATE data}. First
   * {@link IDatabaseTester#onSetup} inserts the initial data set into the DB,
   * then this method {@code UPDATE}s the initial data with the {@code UPDATE data}. This way, circular foreign key
   * references can be put into the {@code UPDATE data}.</p>
   *
   * <p><b>NOTE:</b> if your data set does not contain any circular foreign key references, you do not need to supply a
   * {@link DataSetProvider#getDbUnitUpdateDataSet()}.</p>
   */
  @SuppressWarnings("ProhibitedExceptionDeclared")
  private void updateData(DataSetProvider dataSetProvider, IDatabaseTester databaseTester)
    //CHECKSTYLE:OFF
      throws Exception {
    //CHECKSTYLE:ON

    Optional<IDataSet> updateDataSet = dataSetProvider.getDbUnitUpdateDataSet();
    if (updateDataSet.isPresent()) {
      databaseTester.setDataSet(updateDataSet.get());
      databaseTester.setSetUpOperation(updateOperation);
      databaseTester.onSetup();
    }
  }

  @Override
  public void tearDownDatabase() {
    // do nothing
  }
  
}
