package org.chai.kevin.location;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity(name="DataLocation")
@Table(name="dhsst_location_data_location")
@Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
public class DataLocation extends CalculationLocation {

	private Location location;
	private DataLocationType type;
	
	@ManyToOne(targetEntity=Location.class)
	public Location getLocation() {
		return location;
	}
	
	public void setLocation(Location location) {
		this.location = location;
	}
	
	@ManyToOne(targetEntity=DataLocationType.class)
	public DataLocationType getType() {
		return type;
	}
	
	public void setType(DataLocationType type) {
		this.type = type;
	}
	
	@Override
	@Transient
	public boolean collectsData() {
		return true;
	}

	@Override
	@Transient
	public List<DataLocation> getDataLocations() {
		List<DataLocation> result = new ArrayList<DataLocation>();
		result.add(this);
		return result;
	}

	@Override
	@Transient
	public List<Location> getChildren() {
		return new ArrayList<Location>();
	}
	
	@Override
	public List<DataLocation> getDataLocations(Set<LocationLevel> skipLevels, Set<DataLocationType> types) {
		List<DataLocation> result = new ArrayList<DataLocation>();
		if (types == null || types.contains(type)) result.add(this);
		return result;
	}

	@Override
	public List<Location> getChildren(Set<LocationLevel> skipLevels) {
		return getChildren();
	}

	@Override
	public String toString() {
		return "DataLocation [type=" + type + ", getNames()="
				+ getNames() + ", getCode()=" + getCode() + "]";
	}

	@Override
	@Transient
	public Location getParent() {
		return location;
	}

}
