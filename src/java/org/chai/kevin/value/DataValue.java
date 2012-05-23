package org.chai.kevin.value;

import java.util.Date;

import org.chai.kevin.Period;
import org.chai.kevin.location.CalculationLocation;

public interface DataValue {

	public abstract CalculationLocation getLocation();

	public abstract Period getPeriod();

	public abstract Value getValue();
	
	public abstract Date getTimestamp();

}