package org.chai.kevin.location;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import org.chai.kevin.Translation;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity(name="CalculationLocation")
@Table(name="dhsst_location_abstract", uniqueConstraints={@UniqueConstraint(columnNames="code")})
@Inheritance(strategy=InheritanceType.JOINED)
@Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
public abstract class CalculationLocation {

	private Long id;
	private Translation names = new Translation();
	private String code;
	private String coordinates;
	
	@Id
	@GeneratedValue
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	@Embedded
	@AttributeOverrides({
		@AttributeOverride(name="jsonValue", column=@Column(name="names", nullable=false))
	})
	public Translation getNames() {
		return names;
	}
	
	public void setNames(Translation names) {
		this.names = names;
	}
	
	@Basic
	public String getCode() {
		return code;
	}
	
	public void setCode(String code) {
		this.code = code;
	}

	@Basic
	public String getCoordinates() {
		return coordinates;
	}
	
	public void setCoordinates(String coordinates) {
		this.coordinates = coordinates;
	}
	
	@Transient
	public abstract Location getParent();		
	
	@Transient
	public abstract List<DataLocation> getDataLocations();
	
	@Transient
	public abstract List<DataLocation> getDataLocations(Set<LocationLevel> skipLevels, Set<DataLocationType> types);		
	
	@Transient
	public abstract List<Location> getChildren();
	
	@Transient
	public abstract List<Location> getChildren(Set<LocationLevel> skipLevels);

	public boolean collectLocations(List<Location> locations, List<DataLocation> dataLocations, Set<LocationLevel> skipLevels, Set<DataLocationType> types) {
		boolean result = false;
		for (Location child : getChildren(skipLevels)) {
			result = result | child.collectLocations(locations, dataLocations, skipLevels, types);
		}
	
		List<DataLocation> dataLocationsChildren = getDataLocations(skipLevels, types);
		if (!dataLocationsChildren.isEmpty()) {
			result = true;
			if (dataLocations != null) dataLocations.addAll(dataLocationsChildren);
		}
		
		if (result) {
			if (locations != null && !this.collectsData()) locations.add((Location) this);
		}
		return result;
	}
	
	public List<DataLocation> collectDataLocations(Set<LocationLevel> skipLevels, Set<DataLocationType> types) {
		List<DataLocation> dataLocations = new ArrayList<DataLocation>();
		collectLocations(null, dataLocations, skipLevels, types);
		return dataLocations;
	}
	
	@Transient
	public abstract boolean collectsData();
	
	public String toJson() {
		// TODO 
		return "";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((code == null) ? 0 : code.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof CalculationLocation))
			return false;
		CalculationLocation other = (CalculationLocation) obj;
		if (code == null) {
			if (other.code != null)
				return false;
		} else if (!code.equals(other.code))
			return false;
		return true;
	}
	
	public abstract String toString();
}
