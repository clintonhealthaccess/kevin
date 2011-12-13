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
import javax.persistence.Table;
import javax.persistence.Transient;

import org.chai.kevin.Translation;

@Entity(name="CalculationEntity")
@Table(name="dhsst_entity_calculation")
public abstract class CalculationEntity {

	private Long id;
	private Translation names;
	private String code;
	private String coordinates;
	
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

	@Basic
	public String getCoordinates() {
		return coordinates;
	}
	
	public void setCoordinates(String coordinates) {
		this.coordinates = coordinates;
	}
	
	@Transient
	public abstract LocationEntity getParent();
	
	@Transient
	public abstract List<DataEntity> getDataEntities();
	
	@Transient
	public abstract List<LocationEntity> getChildren();
	
	@Transient
	public abstract boolean collectsData();
	
	public String toJson() {
		// TODO 
		return "";
	}
	
}
