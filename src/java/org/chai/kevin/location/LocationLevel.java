package org.chai.kevin.location;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.chai.kevin.Exportable;
import org.chai.kevin.Orderable;
import org.chai.kevin.Translation;
import org.chai.kevin.util.Utils;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity(name="LocationLevel")
@Table(name="dhsst_location_location_level", uniqueConstraints={@UniqueConstraint(columnNames="code")})
@Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
public class LocationLevel extends Orderable<Integer> implements Exportable {

	private Long id;
	private String code;
	private Integer order;
	private Translation names = new Translation();
	private List<Location> locations = new ArrayList<Location>();
	
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
	
	@OneToMany(targetEntity=Location.class, mappedBy="level")
	public List<Location> getLocations() {
		return locations;
	}
	
	public void setLocations(List<Location> locations) {
		this.locations = locations;
	}

	@Basic
	@Override
	@Column(name="ordering")
	public Integer getOrder() {
		return order;
	}
	
	public void setOrder(Integer order) {
		this.order = order;
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
		if (!(obj instanceof LocationLevel))
			return false;
		LocationLevel other = (LocationLevel) obj;
		if (code == null) {
			if (other.code != null)
				return false;
		} else if (!code.equals(other.code))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "LocationLevel[getId()=" + getId() + ", getCode()=" + getCode() + "]";
	}

	@Override
	public String toExportString() {
		return "[" + Utils.formatExportCode(getCode()) + "]";
	}
	
}
