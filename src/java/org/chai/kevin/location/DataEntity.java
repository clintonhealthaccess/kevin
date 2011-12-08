package org.chai.kevin.location;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity(name="DataCollectingSite")
@Table(name="dhsst_entity_data")
public class DataEntity extends CalculationEntity {

	private LocationEntity location;
	
	@ManyToOne(targetEntity=LocationEntity.class)
	public LocationEntity getLocation() {
		return location;
	}
	
	public void setLocation(LocationEntity location) {
		this.location = location;
	}
	
}
