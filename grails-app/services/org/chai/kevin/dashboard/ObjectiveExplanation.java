package org.chai.kevin.dashboard;

import java.util.Map;

import org.hisp.dhis.organisationunit.OrganisationUnitLevel;

public class ObjectiveExplanation extends Explanation {

	private Map<DashboardObjectiveEntry, DashboardPercentage> objectives;
	
	public ObjectiveExplanation(DashboardPercentage average, OrganisationUnitLevel level, Map<DashboardObjectiveEntry, DashboardPercentage> objectives) {
		super(average, level);

		this.objectives = objectives;
	}

	@Override
	public boolean isTarget() {
		return false;
	}

	public Map<DashboardObjectiveEntry, DashboardPercentage> getObjectives() {
		return objectives;
	}

	
}
