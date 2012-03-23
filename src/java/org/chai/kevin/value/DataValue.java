package org.chai.kevin.value;

import org.chai.kevin.location.CalculationLocation;
import org.hisp.dhis.period.Period;

public interface DataValue {

	public abstract CalculationLocation getLocation();

	public abstract Period getPeriod();

	public abstract Value getValue();

}