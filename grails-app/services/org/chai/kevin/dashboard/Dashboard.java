package org.chai.kevin.dashboard;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.chai.kevin.Organisation;
import org.hisp.dhis.period.Period;

public class Dashboard {
	
	private Organisation organisation;
	private DashboardObjective objective;
	private Period period;
	
	private List<Organisation> organisations;
	private List<DashboardObjectiveEntry> objectiveEntries;
	
	private List<Organisation> organisationPath;
	private List<DashboardObjective> objectivePath;
	
	private Map<Organisation, Map<DashboardEntry, DashboardPercentage>> values;
	
	public Dashboard(Organisation organisation, DashboardObjective objective, Period period, 
			List<Organisation> organisations, List<DashboardObjectiveEntry> objectiveEntries,
			List<Organisation> organisationPath, List<DashboardObjective> objectivePath,
			Map<Organisation, Map<DashboardEntry, DashboardPercentage>> values
	) {
		this.organisation = organisation;
		this.objective = objective;
		this.period = period;
		this.organisations = organisations;
		this.objectiveEntries = objectiveEntries;
		this.organisationPath = organisationPath;
		this.objectivePath = objectivePath;
		this.values = values;
	}
	

	public Organisation getCurrentOrganisation() {
		return organisation;
	}
	
	public DashboardObjective getCurrentObjective() {
		return objective;
	}
	
	public Period getCurrentPeriod() {
		return period;
	}
	
	public List<Organisation> getOrganisations() {
		return organisations;
	}
	
	public List<DashboardObjectiveEntry> getObjectiveEntries() {
		return objectiveEntries;
	}
	
	public List<DashboardObjective> getObjectivePath() {
		return objectivePath;
	}
	
	public List<Organisation> getOrganisationPath() {
		return organisationPath;
	}
	
	
	public DashboardPercentage getPercentage(Organisation organisation, DashboardEntry objective) {
		return values.get(organisation).get(objective);
	}
	
	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		for (Entry<Organisation, Map<DashboardEntry, DashboardPercentage>> organisationEntry : this.values.entrySet()) {
			buffer.append(organisationEntry.getKey());
			for (Entry<DashboardEntry, DashboardPercentage> objectiveEntry : organisationEntry.getValue().entrySet()) {
				buffer.append(objectiveEntry.getKey());
				buffer.append(":");
				buffer.append(objectiveEntry.getValue());
				buffer.append(",");
			}
			buffer.append("\n");
		}
		return buffer.toString();
	}
	
}
