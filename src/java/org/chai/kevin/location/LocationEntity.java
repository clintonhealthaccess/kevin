package org.chai.kevin.location;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity(name="StructuralEntity")
@Table(name="dhsst_entity_location")
public class LocationEntity extends CalculationEntity {

	private LocationEntity parent;
	private List<LocationEntity> children;
	private LocationLevel level;

	@ManyToOne(targetEntity=LocationEntity.class)
	public LocationEntity getParent() {
		return parent;
	}
	
	public void setParent(LocationEntity parent) {
		this.parent = parent;
	}
	
	@OneToMany(targetEntity=LocationEntity.class, mappedBy="parent")
	public List<LocationEntity> getChildren() {
		return children;
	}
	
	public void setChildren(List<LocationEntity> children) {
		this.children = children;
	}

	@ManyToOne(targetEntity=LocationLevel.class)
	public LocationLevel getLevel() {
		return level;
	}
	
	public void setLevel(LocationLevel level) {
		this.level = level;
	}
	
}

