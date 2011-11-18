package org.chai.kevin.dashboard;

import org.chai.kevin.Organisation;
import org.hisp.dhis.period.Period;

public interface DashboardVisitor<T> {
	public T visitObjective(DashboardObjective objective, Organisation organisation, Period period);
	public T visitTarget(DashboardTarget target, Organisation organisation, Period period);
}