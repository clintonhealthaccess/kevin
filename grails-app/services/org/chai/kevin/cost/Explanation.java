package org.chai.kevin.cost;

import java.util.List;
import java.util.Map;

import org.chai.kevin.Organisation;
import org.hisp.dhis.period.Period;


public class Explanation {

	// if this is null, it means no expression is defined for the specified objective and organisation
	private Map<Organisation, Map<Integer, Cost>> costs;
	private List<Organisation> organisations;
	private List<Integer> years;
	private CostTarget currentTarget;
	private CostObjective currentObjective;
	private Period currentPeriod;
	
	public Explanation(CostTarget currentTarget, CostObjective currentObjective, Period currentPeriod, List<Organisation> organisations, List<Integer> years, Map<Organisation, Map<Integer, Cost>> costs) {
		this.costs = costs;
		this.organisations = organisations;
		this.years = years;
		this.currentTarget = currentTarget;
		this.currentObjective = currentObjective;
		this.currentPeriod = currentPeriod;
	}

	public CostTarget getCurrentTarget() {
		return currentTarget;
	}
	
	public CostObjective getCurrentObjective() {
		return currentObjective;
	}
	
	public Period getCurrentPeriod() {
		return currentPeriod;
	}
	
	public List<Organisation> getOrganisations() {
		return organisations;
	}
	
	public Cost getCost(Organisation organisation, Integer year) {
		return costs.get(organisation).get(year);
	}
	
	public List<Integer> getYears() {
		return years;
	}
	
}
