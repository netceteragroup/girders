package com.netcetera.girders.dbunit.dataset;

import lombok.extern.slf4j.Slf4j;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Optional;

/**
 * Provides a data set from a {@link #getDataSetName()}{@code -db.xml}-file in FlatXML format, and allows for using an
 * update data set from a {@link #getDataSetName()}{@code -update-db.xml}-file if desired.
 */
@Slf4j
public class FlatXmlDataSetProvider extends DataSetProvider {

  @Override
  public IDataSet getDbUnitDataSet() throws DataSetException, IOException {
    FlatXmlDataSetBuilder builder = new FlatXmlDataSetBuilder();
    configureDbUnitFlatXmlDataSetBuilder(builder);
    try (InputStream resourceAsStream = getTestClass().getResourceAsStream(getDataSetName() + "-db.xml")) {
      logger.info("Loading data set '{}' for test class '{}'", getDataSetName(), getTestClass());
      return builder.build(resourceAsStream);
    }
  }

  @Override
  public Optional<IDataSet> getDbUnitUpdateDataSet() throws DataSetException, IOException {
    FlatXmlDataSetBuilder builder = new FlatXmlDataSetBuilder();
    configureDbUnitFlatXmlDataSetBuilder(builder);

    URL updateDataSetPath = getTestClass().getResource(getDataSetName() + "-update-db.xml");
    if (updateDataSetPath == null) {
      return Optional.empty();
    }

    try (InputStream resourceAsStream = updateDataSetPath.openStream()) {
      logger.info("Loading update data set '{}' for test class '{}'", getDataSetName(), getTestClass());
      return Optional.of(builder.build(resourceAsStream));
    }
  }

  /**
   * Configure the given {@link FlatXmlDataSetBuilder}.
   *
   * @param builder the {@link FlatXmlDataSetBuilder}
   */
  protected void configureDbUnitFlatXmlDataSetBuilder(FlatXmlDataSetBuilder builder) {
    builder.setColumnSensing(true);
  }
}
