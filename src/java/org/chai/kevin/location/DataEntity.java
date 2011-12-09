package org.chai.kevin.location;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity(name="DataCollectingSite")
@Table(name="dhsst_entity_data")
public class DataEntity extends CalculationEntity {

	private LocationEntity location;
	private DataEntityType dataEntityType;
	
	@ManyToOne(targetEntity=LocationEntity.class)
	public LocationEntity getLocation() {
		return location;
	}
	
	public void setLocation(LocationEntity location) {
		this.location = location;
	}

	@ManyToOne(targetEntity=DataEntityType.class)
	public DataEntityType getDataEntityType() {
		return dataEntityType;
	}
	
	public void setDataEntityType(DataEntityType dataEntityType) {
		this.dataEntityType = dataEntityType;
	}
	
	@Override
	public boolean collectsData() {
		return true;
	}
	
}
