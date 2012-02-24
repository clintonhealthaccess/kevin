package org.chai.kevin.planning;

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
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.chai.kevin.Translation;
import org.hisp.dhis.period.Period;

@Entity(name="Planning")
@Table(name="dhsst_planning")
public class Planning {

	private Long id;
	
	private Period period;
	private List<PlanningType> planningTypes = new ArrayList<PlanningType>();
	private Translation names = new Translation();
	// TODO add settings form page
	
	private Boolean active = false;
	
	@Id
	@GeneratedValue
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	@OneToMany(mappedBy="planning", targetEntity=PlanningType.class)
	public List<PlanningType> getPlanningTypes() {
		return planningTypes;
	}
	
	public void setPlanningTypes(List<PlanningType> planningTypes) {
		this.planningTypes = planningTypes;
	}
	
	@ManyToOne(targetEntity=Period.class)
	public Period getPeriod() {
		return period;
	}
	
	public void setPeriod(Period period) {
		this.period = period;
	}
	
	@Basic
	public Boolean getActive() {
		return active;
	}
	
	public void setActive(Boolean active) {
		this.active = active;
	}
	
	@Embedded
	@AttributeOverrides({ @AttributeOverride(name = "jsonText", column = @Column(name = "jsonNames", nullable = false)) })
	public Translation getNames() {
		return names;
	}
	
	public void setNames(Translation names) {
		this.names = names;
	}
	
}
