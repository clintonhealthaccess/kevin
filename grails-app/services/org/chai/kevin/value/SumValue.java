package org.chai.kevin.value;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.chai.kevin.Period;
import org.chai.kevin.data.Sum;
import org.chai.kevin.location.CalculationLocation;
import org.chai.kevin.value.SumPartialValue;
import org.chai.kevin.value.Value;

public class SumValue extends CalculationValue<SumPartialValue> {

	public SumValue(Set<SumPartialValue> calculationPartialValues, Sum calculation, Period period, CalculationLocation location) {
		super(new ArrayList<SumPartialValue>(calculationPartialValues), calculation, period, location);
	}
	
	public SumValue(List<SumPartialValue> calculationPartialValues, Sum calculation, Period period, CalculationLocation location) {
		super(calculationPartialValues, calculation, period, location);
	}

	@Override
	public Value getValue() {
		Double value = 0d;
		for (SumPartialValue partialValue : getCalculationPartialValues()) {
			if (!partialValue.getValue().isNull()) value += partialValue.getValue().getNumberValue().doubleValue();
		}
		return getData().getType().getValue(value);
	}

	@Override
	public String toString() {
		return "SumValue [getValue()=" + getValue() + "]";
	}
	
}
