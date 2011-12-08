package org.chai.kevin.location;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.chai.kevin.Translation;

@Entity(name="EntityType")
@Table(name="dhsst_site_type")
public class DataEntityType {

	private Long id;
	private Translation names;
	
	@Id
	@GeneratedValue
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	public Translation getNames() {
		return names;
	}
	
	public void setNames(Translation names) {
		this.names = names;
	}
	
}
