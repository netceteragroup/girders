package com.netcetera.girders.dbunit;

import com.netcetera.girders.dbunit.datamanagement.ParallelizedDataManagementProvider;
import com.netcetera.girders.dbunit.dataset.FlatXmlDataSetProvider;
import org.dbunit.operation.DatabaseOperation;
import org.junit.jupiter.api.parallel.Execution;

import static org.junit.jupiter.api.parallel.ExecutionMode.CONCURRENT;

/**
 * Base class for a parallelized DB test case with the DB initialized from an XML file.
 * <p>
 * The XML is expected to be named after the concrete class containing the test case with a {@code -db.xml} suffix. It
 * will be loaded using the concrete test case class.
 * </p>
 *
 * @see FlatXmlDataSetProvider
 */
@Execution(CONCURRENT)
public abstract class AbstractParallelizedXmlDbTestCase extends AbstractDbTestCase {

  /**
   * Import data from {@link FlatXmlDataSetProvider flatXML files}.
   */
  @SuppressWarnings("WeakerAccess") // public API
  protected AbstractParallelizedXmlDbTestCase() {
    super(new ParallelizedDataManagementProvider(), new FlatXmlDataSetProvider());
  }

  @Override
  public DatabaseOperation getDbUnitSetUpOperation() {
    return DatabaseOperation.INSERT;
  }
}
