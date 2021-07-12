package com.netcetera.girders.demo.showcase.jdbc;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * Data transfer object for a project.
 */
@SuppressWarnings("SerializableDeserializableClassInSecureContext")
@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class Project implements Serializable {

  private String id;

  private String title;

}
