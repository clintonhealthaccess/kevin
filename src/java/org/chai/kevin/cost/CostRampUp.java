package org.chai.kevin.cost;

import java.util.List;
import java.util.Map;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapKey;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;
import javax.persistence.Table;

import org.chai.kevin.Translatable;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Columns;

@Entity(name="CostRampUp")
@Table(name="dhsst_cost_ramp_up")
public class CostRampUp extends Translatable {

	private Long id;
	private String name;
	private Map<Integer, CostRampUpYear> years;
	
	@Id
	@GeneratedValue
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	@OneToMany(fetch=FetchType.EAGER, targetEntity=CostRampUpYear.class, cascade=CascadeType.ALL)
	@MapKey(name="year")
	@JoinColumn(name="ramp_up_id")
	@Cascade(value={org.hibernate.annotations.CascadeType.DELETE_ORPHAN})
	public Map<Integer, CostRampUpYear> getYears() {
		return years;
	}
	public void setYears(Map<Integer, CostRampUpYear> years) {
		this.years = years;
	}
	
}
