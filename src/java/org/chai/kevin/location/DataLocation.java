package org.chai.kevin.location;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.chai.kevin.Exportable;
import org.chai.kevin.util.Utils;

@Entity(name="DataLocation")
@Table(name="dhsst_location_data_location")
public class DataLocation extends CalculationLocation implements Exportable {

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
	public Location getParent() {
		return location;
	}
	
	@Override
	@Transient
	public List<Location> getChildren() {
		return new ArrayList<Location>();
	}
	
	@Override
	@Transient
	public List<DataLocation> getDataLocations() {
		List<DataLocation> result = new ArrayList<DataLocation>();
		result.add(this);
		return result;
	}

	@Override
	public List<CalculationLocation> getLocationChildren(Set<LocationLevel> skipLevels) {
		return new ArrayList<CalculationLocation>();
	}
	
	@Override
	public List<DataLocation> getDataLocationChildren(Set<LocationLevel> skipLevels, Set<DataLocationType> types) {
		List<DataLocation> result = new ArrayList<DataLocation>();
		if (types == null || types.contains(type)) result.add(this);
		return result;
	}

	//gets all location and data location children
	@Transient
	public List<CalculationLocation> getAllChildren(Set<LocationLevel> skipLevels, Set<DataLocationType> types) {
		List<CalculationLocation> result = new ArrayList<CalculationLocation>();
		result.addAll(getLocationChildren(skipLevels));
		result.addAll(getDataLocationChildren(skipLevels, types));
		return result;
	}

	@Override
	public String toString() {
		return "DataLocation[getId()=" + getId() + ", getCode()=" + getCode() + "]";
	}

	@Override
	public String toExportString() {
		return "[" + Utils.formatExportCode(getCode()) + "]";
	}

	
}
