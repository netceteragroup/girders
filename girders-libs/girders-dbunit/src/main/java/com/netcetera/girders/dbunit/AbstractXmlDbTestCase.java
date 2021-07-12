package com.netcetera.girders.dbunit;

import com.netcetera.girders.dbunit.dataset.FlatXmlDataSetProvider;
import org.junit.jupiter.api.parallel.Execution;

import static org.junit.jupiter.api.parallel.ExecutionMode.SAME_THREAD;

/**
 * Base class for a DB test case with the DB initialized from an XML file.
 * <p>
 * The XML is expected to be named after the concrete class containing the test case with a {@code -db.xml} suffix. It
 * will be loaded using the concrete test case class.
 * </p>
 *
 * @see FlatXmlDataSetProvider
 */
@Execution(SAME_THREAD)
public abstract class AbstractXmlDbTestCase extends AbstractDbTestCase {

  /**
   * Import data from {@link FlatXmlDataSetProvider flatXML files}.
   */
  protected AbstractXmlDbTestCase() {
    super(new FlatXmlDataSetProvider());
  }
}
