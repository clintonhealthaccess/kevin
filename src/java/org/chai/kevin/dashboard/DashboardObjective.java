package org.chai.kevin.dashboard;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.chai.kevin.Organisation;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hisp.dhis.period.Period;

@Entity(name="StrategicObjective")
@Table(name="dhsst_dashboard_objective")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class DashboardObjective extends DashboardEntry {

	private List<DashboardObjectiveEntry> objectiveEntries = new ArrayList<DashboardObjectiveEntry>();
	
	@OneToMany(mappedBy="parent", targetEntity=DashboardObjectiveEntry.class)
	@OrderBy(value="order")
	public List<DashboardObjectiveEntry> getObjectiveEntries() {
		return objectiveEntries;
	}
	public void setObjectiveEntries(List<DashboardObjectiveEntry> objectiveEntries) {
		this.objectiveEntries = objectiveEntries;
	}
	
	public void addObjectiveEntry(DashboardObjectiveEntry objectiveEntry) {
		objectiveEntry.setParent(this);
		objectiveEntry.getEntry().setParent(objectiveEntry);
		objectiveEntries.add(objectiveEntry);
		Collections.sort(objectiveEntries);
	}
	
	@Override
	public DashboardPercentage getValue(PercentageCalculator calculator, Organisation organisation, Period period, boolean isFacility) {
		return calculator.getPercentageForObjective(this, organisation, period);
	}
	
	@Override
	public DashboardExplanation getExplanation(ExplanationCalculator calculator, Organisation organisation, Period period, boolean isFacility) {
		return calculator.explainObjective(this, organisation, period);
	}
	
	@Override
	public boolean hasChildren() {
		return objectiveEntries.size() > 0;
	}
	@Override
	@Transient
	public boolean isTarget() {
		return false;
	}
	
}
