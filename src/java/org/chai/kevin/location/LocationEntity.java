package org.chai.kevin.location;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity(name="LocationEntity")
@Table(name="dhsst_entity_location")
@Cache(usage=CacheConcurrencyStrategy.READ_ONLY)
public class LocationEntity extends CalculationEntity {

	private LocationEntity parent;
	private List<LocationEntity> children = new ArrayList<LocationEntity>();
	private List<DataEntity> dataEntities = new ArrayList<DataEntity>();
	private LocationLevel level;

	@ManyToOne(targetEntity=LocationEntity.class)
	public LocationEntity getParent() {
		return parent;
	}
	
	public void setParent(LocationEntity parent) {
		this.parent = parent;
	}
	
	@OneToMany(targetEntity=LocationEntity.class, mappedBy="parent")
	@Cache(usage=CacheConcurrencyStrategy.READ_ONLY)
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
	
	@Override
	@OneToMany(targetEntity=DataEntity.class, mappedBy="location")
	@Cache(usage=CacheConcurrencyStrategy.READ_ONLY)
	public List<DataEntity> getDataEntities() {
		return dataEntities;
	}
	
	public void setDataEntities(List<DataEntity> dataEntities) {
		this.dataEntities = dataEntities;
	}

	@Transient
	public List<CalculationEntity> getChildrenEntities() {
		List<CalculationEntity> result = new ArrayList<CalculationEntity>();
		result.addAll(getDataEntities());
		result.addAll(getChildren());
		return result;
	}
	
	@Override
	public boolean collectsData() {
		return false;
	}

	@Override
	public String toString() {
		return "LocationEntity [getCode()=" + getCode() + "]";
	}
	
}

