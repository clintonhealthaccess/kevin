package org.chai.kevin.location;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity(name="DataCollectingSite")
@Table(name="dhsst_entity_data")
public class DataEntity extends CalculationEntity {

	private LocationEntity parent;
	private DataEntityType type;
	
	@ManyToOne(targetEntity=LocationEntity.class)
	@Override
	public LocationEntity getParent() {
		return parent;
	}
	
	public void setLocation(LocationEntity parent) {
		this.parent = parent;
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
	
}
