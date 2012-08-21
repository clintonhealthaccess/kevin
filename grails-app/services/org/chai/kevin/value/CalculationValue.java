package org.chai.kevin.value;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Transient;

import org.chai.kevin.Period;
import org.chai.kevin.data.Calculation;
import org.chai.kevin.location.CalculationLocation;
import org.chai.kevin.location.DataLocationType;

public abstract class CalculationValue<T extends CalculationPartialValue> implements DataValue {

	private CalculationLocation location;
	private Period period;
	private List<T> calculationPartialValues;
	private Calculation<T> calculation;
	
	public CalculationValue(List<T> calculationPartialValues, Calculation<T> calculation, Period period, CalculationLocation location) {
		this.calculationPartialValues = calculationPartialValues;
		this.calculation = calculation;
		this.period = period;
		this.location = location;
	}
	
	public abstract boolean isNull();
	
	@Transient
	public abstract Value getValue();
	
	@Transient
	public abstract Value getAverage();
	
	@Override
	public Period getPeriod() {
		return period;
	}
	
	@Override
	public CalculationLocation getLocation() {
		return location;
	}
	
	public boolean isComplete() {
		return getTypeCodes().size() == calculationPartialValues.size();
	}
	
	public Calculation<T> getData() {
		return calculation;
	}
	
	public List<DataLocationType> getTypeCodes() {
		List<DataLocationType> result = new ArrayList<DataLocationType>();
		for (T calculationPartialValue : calculationPartialValues) {
			result.add(calculationPartialValue.getType());
		}
		return result;
	}
	
		
	public List<T> getCalculationPartialValues() {
		return calculationPartialValues;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((calculationPartialValues == null) ? 0
						: calculationPartialValues.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof CalculationValue))
			return false;
		CalculationValue other = (CalculationValue) obj;
		if (calculationPartialValues == null) {
			if (other.calculationPartialValues != null)
				return false;
		} else if (!calculationPartialValues
				.equals(other.calculationPartialValues))
			return false;
		return true;
	}

}
