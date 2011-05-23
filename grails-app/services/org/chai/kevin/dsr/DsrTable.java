package org.chai.kevin.dsr;

import java.util.List;
import java.util.Map;

import org.chai.kevin.Organisation;
import org.hisp.dhis.period.Period;

public class DsrTable {
	private Period period;
	private DsrObjective objective;
	private List<Organisation> organisations;
	private List<DsrTarget> targets;
	private Map<Organisation, Map<DsrTarget, Dsr>> values;


	public DsrTable(List<Organisation> organisations, Period period, DsrObjective objective,
			List<DsrTarget> targets,
			Map<Organisation, Map<DsrTarget, Dsr>> values) {
		super();
		this.organisations = organisations;
		this.period = period;
		this.objective = objective;
		this.targets = targets;
		this.values = values;
	}

	public void setPeriod(Period period) {
		this.period = period;
	}

	public Period getPeriod() {
		return period;
	}

	public void setOrganisations(List<Organisation> organisations) {
		this.organisations = organisations;
	}

	public List<Organisation> getOrganisations() {
		return organisations;
	}

	public void setTargets(List<DsrTarget> targets) {
		this.targets = targets;
	}

	public List<DsrTarget> getTargets() {
		return targets;
	}

	public String getDsrValue(Organisation organisation, DsrTarget target) {

		return values.get(target).get(organisation).getDsrValue();

	}

	public void setObjective(DsrObjective objective) {
		this.objective = objective;
	}

	public DsrObjective getObjective() {
		return objective;
	}
	
	public Map<Organisation, Map<DsrTarget, Dsr>> getValues() {
		return values;
	}

	public void setValues(Map<Organisation, Map<DsrTarget, Dsr>> values) {
		this.values = values;
	}


}
