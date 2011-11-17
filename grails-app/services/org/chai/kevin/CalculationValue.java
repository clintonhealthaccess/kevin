package org.chai.kevin;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Transient;

import org.chai.kevin.data.Calculation;
import org.chai.kevin.value.CalculationPartialValue;
import org.chai.kevin.value.Value;

public abstract class CalculationValue<T extends CalculationPartialValue> {

	private List<T> calculationPartialValues;
	private Calculation<T> calculation;
	private List<String> groupUuids;
	
	public CalculationValue(List<T> calculationPartialValues, Calculation<T> calculation) {
		this.calculationPartialValues = calculationPartialValues;
		this.calculation = calculation;
	}
	
	@Transient
	public abstract Value getValue();
	
	public boolean isComplete() {
		return groupUuids.size() == calculationPartialValues.size();
	}
	
	public Calculation<T> getCalculation() {
		return calculation;
	}
	
	public List<String> getGroupUuids() {
		List<String> groupUuids = new ArrayList<String>();
		for (T calculationPartialValue : calculationPartialValues) {
			groupUuids.add(calculationPartialValue.getGroupUuid());
		}
		return groupUuids;
	}
	
	public List<T> getCalculationPartialValues() {
		return calculationPartialValues;
	}
	
}
