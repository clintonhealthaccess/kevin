package org.chai.kevin.location;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity(name="DataEntity")
@Table(name="dhsst_entity_data")
@Cache(usage=CacheConcurrencyStrategy.READ_ONLY)
public class DataEntity extends CalculationEntity {

	private LocationEntity location;
	private DataEntityType type;
	
	@ManyToOne(targetEntity=LocationEntity.class)
	public LocationEntity getLocation() {
		return location;
	}
	
	public void setLocation(LocationEntity location) {
		this.location = location;
	}
	
	@ManyToOne(targetEntity=DataEntityType.class)
	public DataEntityType getType() {
		return type;
	}
	
	public void setType(DataEntityType type) {
		this.type = type;
	}
	
	@Override
	@Transient
	public boolean collectsData() {
		return true;
	}

	@Override
	@Transient
	public List<DataEntity> getDataEntities() {
		List<DataEntity> result = new ArrayList<DataEntity>();
		result.add(this);
		return result;
	}

	@Override
	@Transient
	public List<LocationEntity> getChildren() {
		return new ArrayList<LocationEntity>();
	}

	@Override
	public String toString() {
		return "DataEntity [getCode()=" + getCode() + "]";
	}

	@Override
	@Transient
	public LocationEntity getParent() {
		return location;
	}
	
}
