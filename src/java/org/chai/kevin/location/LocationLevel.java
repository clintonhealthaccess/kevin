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

import org.chai.kevin.Orderable;
import org.chai.kevin.Translation;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity(name="LocationLevel")
@Table(name="dhsst_entity_location_level")
@Cache(usage=CacheConcurrencyStrategy.READ_ONLY)
public class LocationLevel extends Orderable<Integer> {

	private Long id;
	private String code;
	private Integer order;
	private Translation names = new Translation();
	private List<LocationEntity> locations = new ArrayList<LocationEntity>();
	
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
	
	@OneToMany(targetEntity=LocationEntity.class, mappedBy="level")
	public List<LocationEntity> getLocations() {
		return locations;
	}
	
	public void setLocations(List<LocationEntity> locations) {
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
	
}
