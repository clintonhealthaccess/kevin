package org.chai.kevin.location;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity(name="LocationEntity")
@Table(name="dhsst_entity_location")
@Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
public class LocationEntity extends CalculationEntity {

	private LocationEntity parent;
	private List<LocationEntity> children = new ArrayList<LocationEntity>();
	private List<DataLocationEntity> dataLocationEntities = new ArrayList<DataLocationEntity>();
	private LocationLevel level;
	
	@ManyToOne(targetEntity=LocationEntity.class)
	public LocationEntity getParent() {
		return parent;
	}
	
	public void setParent(LocationEntity parent) {
		this.parent = parent;
	}
	
	@OneToMany(targetEntity=LocationEntity.class, mappedBy="parent")
	@Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
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
	@OneToMany(targetEntity=DataLocationEntity.class, mappedBy="location")
	@Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
	public List<DataLocationEntity> getDataEntities() {
		return dataLocationEntities;
	}
	
	public void setDataEntities(List<DataLocationEntity> dataLocationEntities) {
		this.dataLocationEntities = dataLocationEntities;
	}			
	
	//gets all location children
	@Override
	public List<LocationEntity> getChildren(Set<LocationLevel> skipLevels) {		
		List<LocationEntity> result = new ArrayList<LocationEntity>();
		for (LocationEntity child : children) {
			if (skipLevels != null && skipLevels.contains(child.getLevel())) {
				result.addAll(child.getChildren(skipLevels));
			}
			else result.add(child);
		}
		return result;
	}

	//gets all data locations
	@Override
	public List<DataLocationEntity> getDataLocations(Set<LocationLevel> skipLevels, Set<DataEntityType> types) {
		List<DataLocationEntity> result = new ArrayList<DataLocationEntity>();
		
		List<DataLocationEntity> dataEntities = getDataEntities();
		for (DataLocationEntity dataEntity : dataEntities) {
			if (types == null || types.contains(dataEntity.getType())) 
				result.add(dataEntity);
		}
		
		for (LocationEntity child : children) {
			if (skipLevels != null && skipLevels.contains(child.getLevel())) {
				result.addAll(child.getDataLocations(skipLevels, types));
			}
		}
		
		return result;				
	}
			
	//gets all location children and data locations
	@Transient
	public List<CalculationEntity> getChildrenEntities(Set<LocationLevel> skipLevels, Set<DataEntityType> types) {
		List<CalculationEntity> result = new ArrayList<CalculationEntity>();
		result.addAll(getChildren(skipLevels));
		result.addAll(getDataLocations(skipLevels, types));
		return result;
	}
	
	//gets all location children and data locations (that have data locations)
	@Transient
	public List<CalculationEntity> getChildrenEntitiesWithDataLocations(Set<LocationLevel> skipLevels, Set<DataEntityType> types) {
		List<CalculationEntity> result = new ArrayList<CalculationEntity>();
		
		List<LocationEntity> locationChildren = getChildren(skipLevels);
		List<LocationEntity> locationTree = collectTreeWithDataEntities(skipLevels, types);
		for(LocationEntity locationChild : locationChildren){
			if(locationTree.contains(locationChild))
				result.add(locationChild);	
		}
		
		result.addAll(getDataLocations(skipLevels, types));
		return result;
	}
	
	//gets all location children, grandchildren, etc (that have data locations)
	public List<LocationEntity> collectTreeWithDataEntities(Set<LocationLevel> skipLevels, Set<DataEntityType> types) {
		List<LocationEntity> locations = new ArrayList<LocationEntity>();
		collectLocations(locations, null, skipLevels, types);
		return locations;
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