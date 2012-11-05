package org.chai.kevin.dashboard;

import org.chai.kevin.Period;
import org.chai.location.CalculationLocation;

public interface DashboardVisitor<T> {
	public T visitProgram(DashboardProgram program, CalculationLocation location, Period period);
	public T visitTarget(DashboardTarget target, CalculationLocation location, Period period);
}