package com.netcetera.girders.dbunit;

import com.netcetera.girders.dbunit.datamanagement.ParallelizedDataManagementProvider;
import com.netcetera.girders.dbunit.dataset.DataSetProvider;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.List;

/**
 * Sample entity.
 */
@Entity
@Table(name = "sample")
public class Sample {

  @Id
  private Long id;

  @Column(name = "name", unique = true)
  private String name;

  /**
   * The fields {@link #reference} and {@link #references} exist to test that inserting foreign-key-cycles into the
   * data set via {@link DataSetProvider#getDbUnitUpdateDataSet()} works as intended for the
   * {@link ParallelizedDataManagementProvider}.
   */
  @ManyToOne
  @JoinColumn(name = "reference", referencedColumnName = "id")
  @SuppressWarnings("JavaDoc")
  private Reference reference;

  @OneToMany(mappedBy = "sample", fetch = FetchType.EAGER)
  private List<Reference> references;


  /**
   * Get the id.
   * 
   * @return Identifier of the object.
   */
  public Long getId() {
    return id;
  }

  /**
   * Get the name of the object.
   * 
   * @return Name
   */
  public String getName() {
    return name;
  }

  /**
   * Get the reference that this entity points to.
   *
   * @return Reference field.
   */
  public Reference getReference() {
    return reference;
  }

  /**
   * Get the references that point to this entity.
   *
   * @return List of references.
   */
  public List<Reference> getReferences() {
    return references;
  }

}
