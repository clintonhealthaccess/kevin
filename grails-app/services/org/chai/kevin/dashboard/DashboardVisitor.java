package org.chai.kevin.dashboard;

import org.chai.kevin.location.CalculationLocation;

import org.hisp.dhis.period.Period;

public interface DashboardVisitor<T> {
	public T visitProgram(DashboardProgram program, CalculationLocation location, Period period);
	public T visitTarget(DashboardTarget target, CalculationLocation location, Period period);
}