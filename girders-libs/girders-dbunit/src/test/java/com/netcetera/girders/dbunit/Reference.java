package com.netcetera.girders.dbunit;

import com.netcetera.girders.dbunit.datamanagement.ParallelizedDataManagementProvider;
import com.netcetera.girders.dbunit.dataset.DataSetProvider;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.util.List;

/**
 * This class exists to test that inserting foreign-key-cycles into the data set via
 * {@link DataSetProvider#getDbUnitUpdateDataSet()} works as intended for the
 * {@link ParallelizedDataManagementProvider}.
 */
@Entity
@Table(name = "reference")
public class Reference {

  @Id
  private Long id;

  @ManyToOne
  @JoinColumn(name = "sample", referencedColumnName = "id")
  private Sample sample;

  @OneToMany(mappedBy = "reference")
  private List<Sample> samples;

  /**
   * Get the id.
   *
   * @return Identifier of the object.
   */
  public Long getId() {
    return id;
  }

  /**
   * Get the sample that this entity points to.
   *
   * @return Sample field.
   */
  public Sample getSample() {
    return sample;
  }

  /**
   * Get the samples that point to this entity.
   *
   * @return List of samples.
   */
  public List<Sample> getSamples() {
    return samples;
  }
}
