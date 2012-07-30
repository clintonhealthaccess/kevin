package org.chai.kevin.location;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.chai.kevin.Exportable;
import org.chai.kevin.util.Utils;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity(name="Location")
@Table(name="dhsst_location_location")
public class Location extends CalculationLocation implements Exportable {

	private Location parent;
	private List<Location> children = new ArrayList<Location>();
	private List<DataLocation> dataLocations = new ArrayList<DataLocation>();
	private LocationLevel level;
	
	@ManyToOne(targetEntity=Location.class)
	public Location getParent() {
		return parent;
	}
	
	public void setParent(Location parent) {
		this.parent = parent;
	}
	
	@OneToMany(targetEntity=Location.class, mappedBy="parent")
	@Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
	public List<Location> getChildren() {
		return children;
	}
	
	public void setChildren(List<Location> children) {
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
	@OneToMany(targetEntity=DataLocation.class, mappedBy="location")
	@Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
	public List<DataLocation> getDataLocations() {
		return dataLocations;
	}
	
	public void setDataLocations(List<DataLocation> dataLocations) {
		this.dataLocations = dataLocations;
	}			
	
	//gets all location children
	@Override
	public List<Location> getChildren(Set<LocationLevel> skipLevels) {		
		List<Location> result = new ArrayList<Location>();
		for (Location child : children) {
			if (skipLevels != null && skipLevels.contains(child.getLevel())) {
				result.addAll(child.getChildren(skipLevels));
			}
			else result.add(child);
		}
		return result;
	}

	//gets all data location children
	@Override
	public List<DataLocation> getDataLocations(Set<LocationLevel> skipLevels, Set<DataLocationType> types) {
		List<DataLocation> result = new ArrayList<DataLocation>();
		
		List<DataLocation> dataLocations = getDataLocations();
		for (DataLocation dataLocation : dataLocations) {
			if (types == null || types.contains(dataLocation.getType())) 
				result.add(dataLocation);
		}
		
		for (Location child : children) {
			if (skipLevels != null && skipLevels.contains(child.getLevel())) {
				result.addAll(child.getDataLocations(skipLevels, types));
			}
		}
		
		return result;				
	}
			
	//gets all location and data location children
	@Transient
	public List<CalculationLocation> getChildren(Set<LocationLevel> skipLevels, Set<DataLocationType> types) {
		List<CalculationLocation> result = new ArrayList<CalculationLocation>();
		result.addAll(getChildren(skipLevels));
		result.addAll(getDataLocations(skipLevels, types));
		return result;
	}
	
	//gets only location and data location children with data locations
	@Transient
	public List<CalculationLocation> getChildrenWithData(Set<LocationLevel> skipLevels, Set<DataLocationType> types, boolean dataLocations) {
		List<CalculationLocation> result = new ArrayList<CalculationLocation>();
		
		List<Location> locationChildren = getChildren(skipLevels);
		List<CalculationLocation> locationTree = collectLocationTreeWithData(skipLevels, types, false);
		for(Location locationChild : locationChildren){
			if(locationTree.contains(locationChild))
				result.add(locationChild);	
		}
		
		if(dataLocations){
			result.addAll(getDataLocations(skipLevels, types));
		}
		
		return result;
	}
	
	//gets only location tree with data locations
	public List<CalculationLocation> collectLocationTreeWithData(Set<LocationLevel> skipLevels, Set<DataLocationType> types, boolean dataLocations) {
		List<CalculationLocation> locations = new ArrayList<CalculationLocation>();
		collectLocations(locations, null, skipLevels, types);
		if(dataLocations){
			locations.addAll(getDataLocations(skipLevels, types));
		}
		return locations;
	}
	
	@Override
	public boolean collectsData() {
		return false;
	}

	@Override
	public String toString() {
		return "Location[getId()=" + getId() + ", getCode()=" + getCode() + "]";
	}

	@Override
	public String toExportString() {
		return "[" + Utils.formatExportCode(getCode()) + "]";
	}

}