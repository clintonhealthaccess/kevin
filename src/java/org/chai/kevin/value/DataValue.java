package org.chai.kevin.value;

import org.chai.kevin.Period;
import org.chai.kevin.location.CalculationEntity;

public interface DataValue {

	public abstract CalculationEntity getEntity();

	public abstract Period getPeriod();

	public abstract Value getValue();

}