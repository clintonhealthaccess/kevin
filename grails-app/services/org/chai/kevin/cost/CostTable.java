package org.chai.kevin.cost;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.chai.kevin.Organisation;
import org.chai.kevin.cost.CostTarget.CostType;
import org.hisp.dhis.period.Period;

public class CostTable {

	private Period currentPeriod;
	private Organisation currentOrganisation;
	private CostObjective currentObjective;

	private List<CostTarget> targets;
	private List<Integer> years;
	private Map<CostTarget, Map<Integer, Cost>> values;


	public CostTable(Period currentPeriod, CostObjective currentObjective, List<CostTarget> targets, List<Integer> years, Organisation currentOrganisation,
			Map<CostTarget, Map<Integer, Cost>> values) {
		super();
		this.currentPeriod = currentPeriod;
		this.currentObjective = currentObjective;
		this.targets = targets;
		this.years = years;
		this.currentOrganisation = currentOrganisation;
		this.values = values;
	}
	
	public CostTable(Period currentPeriod, List<Integer> years,
			Organisation currentOrganisation) {
		this.currentPeriod = currentPeriod;
		this.years = years;
		this.currentOrganisation = currentOrganisation;
		this.targets = new ArrayList<CostTarget>();
		this.values = new HashMap<CostTarget, Map<Integer,Cost>>();
	}

	public Period getCurrentPeriod() {
		return currentPeriod;
	}
	
	public CostObjective getCurrentObjective() {
		return currentObjective;
	}
	
	public Integer getCurrentObjectiveId() {
		return currentObjective!=null?currentObjective.getId():null;
	}
	
	public List<CostTarget> getTargets() {
		return targets;
	}
	
	public Organisation getCurrentOrganisation() {
		return currentOrganisation;
	}
	
	public List<CostTarget> getTargetsOfType(CostType type) {
		List<CostTarget> result = new ArrayList<CostTarget>();
		for (CostTarget costTarget : getTargets()) {
			if (costTarget.getCostType().equals(type)) result.add(costTarget);
		}
		return result;
	}
	
	public List<Integer> getYears() {
		return years;
	}
	
	public Cost getCost(CostTarget target, Integer year) {
		return values.get(target).get(year);
	}
	
}
