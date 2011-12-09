package org.chai.kevin.location;

import java.util.List;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.chai.kevin.Translation;

@Entity(name="DataCollectingEntity")
@Table(name="dhsst_location_level")
public class LocationLevel {

	private Long id;
	private String code;
	private Translation names;
	private List<LocationEntity> locations;
	
	@Id
	@GeneratedValue
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	@Embedded
	@AttributeOverrides({
		@AttributeOverride(name="jsonValue", column=@Column(name="names", nullable=false))
	})
	public Translation getNames() {
		return names;
	}
	
	public void setNames(Translation names) {
		this.names = names;
	}
	
	@Basic
	public String getCode() {
		return code;
	}
	
	public void setCode(String code) {
		this.code = code;
	}
	
	@OneToMany(targetEntity=LocationEntity.class, mappedBy="level")
	public List<LocationEntity> getLocations() {
		return locations;
	}
	
	public void setLocations(List<LocationEntity> locations) {
		this.locations = locations;
	}
	
}
