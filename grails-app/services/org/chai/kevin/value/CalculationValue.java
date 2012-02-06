package org.chai.kevin.value;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.persistence.Transient;

import org.chai.kevin.data.Calculation;
import org.chai.kevin.location.CalculationEntity;
import org.chai.kevin.location.DataEntityType;
import org.hisp.dhis.period.Period;

public abstract class CalculationValue<T extends CalculationPartialValue> implements DataValue {

	private CalculationEntity entity;
	private Period period;
	private Set<T> calculationPartialValues;
	private Calculation<T> calculation;
	
	public CalculationValue(Set<T> calculationPartialValues, Calculation<T> calculation, Period period, CalculationEntity entity) {
		this.calculationPartialValues = calculationPartialValues;
		this.calculation = calculation;
		this.period = period;
		this.entity = entity;
	}
	
	@Transient
	public abstract Value getValue();
	
	@Override
	public Period getPeriod() {
		return period;
	}
	
	@Override
	public CalculationEntity getEntity() {
		return entity;
	}
	
	public boolean isComplete() {
		return getTypeCodes().size() == calculationPartialValues.size();
	}
	
	public Calculation<T> getData() {
		return calculation;
	}
	
	public List<DataEntityType> getTypeCodes() {
		List<DataEntityType> result = new ArrayList<DataEntityType>();
		for (T calculationPartialValue : calculationPartialValues) {
			result.add(calculationPartialValue.getType());
		}
		return result;
	}
	
	public Set<T> getCalculationPartialValues() {
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
