package org.chai.kevin.dashboard;

import org.chai.kevin.location.CalculationEntity;

import org.hisp.dhis.period.Period;

public interface DashboardVisitor<T> {
	public T visitProgram(DashboardProgram program, CalculationEntity entity, Period period);
	public T visitTarget(DashboardTarget target, CalculationEntity entity, Period period);
}