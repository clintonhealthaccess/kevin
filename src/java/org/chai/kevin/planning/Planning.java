package org.chai.kevin.planning;

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
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.chai.kevin.Period;
import org.chai.kevin.Translation;
import org.chai.kevin.location.DataLocationType;
import org.chai.kevin.util.Utils;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

@Entity(name="Planning")
@Table(name="dhsst_planning")
public class Planning {

	private Long id;
	
	private Period period;
	private String typeCodeString;
	private List<PlanningType> planningTypes = new ArrayList<PlanningType>();
	private List<PlanningSkipRule> skipRules = new ArrayList<PlanningSkipRule>();
	private List<PlanningOutput> planningOutputs = new ArrayList<PlanningOutput>();
	private Translation names = new Translation();
	private Translation overviewHelps = new Translation();
	private Translation budgetHelps = new Translation();
	
	private Boolean active = false;
	
	@Id
	@GeneratedValue
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	@OneToMany(mappedBy="planning", targetEntity=PlanningOutput.class, orphanRemoval=true)
	@Cascade({CascadeType.ALL })
	@OrderBy("order")
	public List<PlanningOutput> getPlanningOutputs() {
		return planningOutputs;
	}
	
	public void setPlanningOutputs(List<PlanningOutput> planningOutputs) {
		this.planningOutputs = planningOutputs;
	}
	
	@OneToMany(mappedBy="planning", targetEntity=PlanningSkipRule.class, orphanRemoval=true)
	@Cascade({CascadeType.ALL })
	public List<PlanningSkipRule> getSkipRules() {
		return skipRules;
	}
	
	public void setSkipRules(List<PlanningSkipRule> skipRules) {
		this.skipRules = skipRules;
	}
	
	public void addSkipRule(PlanningSkipRule skipRule) {
		skipRule.setPlanning(this);
		skipRules.add(skipRule);
	}
	
	@OneToMany(mappedBy="planning", targetEntity=PlanningType.class)
	public List<PlanningType> getPlanningTypes() {
		return planningTypes;
	}
	
	public void setPlanningTypes(List<PlanningType> planningTypes) {
		this.planningTypes = planningTypes;
	}
	
	@Lob
	public String getTypeCodeString() {
		return typeCodeString;
	}

	public void setTypeCodeString(String typeCodeString) {
		this.typeCodeString = typeCodeString;
	}
	
	@Transient
	public Set<String> getTypeCodes() {
		return Utils.split(typeCodeString, DataLocationType.DEFAULT_CODE_DELIMITER);
	}
	public void setTypeCodes(Set<String> typeCodes) {
		this.typeCodeString = Utils.unsplit(typeCodes, DataLocationType.DEFAULT_CODE_DELIMITER);
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
	
	@Embedded
	@AttributeOverrides({ @AttributeOverride(name = "jsonText", column = @Column(name = "jsonOverviewHelps", nullable = false)) })
	public Translation getOverviewHelps() {
		return overviewHelps;
	}
	
	public void setOverviewHelps(Translation overviewHelps) {
		this.overviewHelps = overviewHelps;
	}
	
	@Embedded
	@AttributeOverrides({ @AttributeOverride(name = "jsonText", column = @Column(name = "jsonBudgetHelps", nullable = false)) })
	public Translation getBudgetHelps() {
		return budgetHelps;
	}
	
	public void setBudgetHelps(Translation budgetHelps) {
		this.budgetHelps = budgetHelps;
	}
	
	public String toString(){
		return "Planning[getId()=" + getId() + ", getNames()=" + getNames() + "]";
	}

	@Transient
	public List<PlanningCost> getPlanningCosts() {
		List<PlanningCost> planningCosts = new ArrayList<PlanningCost>();
		for (PlanningType planningType : planningTypes) {
			planningCosts.addAll(planningType.getCosts());
		}
		return planningCosts;
	}
}
