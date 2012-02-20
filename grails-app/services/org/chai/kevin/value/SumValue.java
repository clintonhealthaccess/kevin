package org.chai.kevin.value;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.chai.kevin.data.Sum;
import org.chai.kevin.location.CalculationEntity;
import org.chai.kevin.value.SumPartialValue;
import org.chai.kevin.value.Value;
import org.hisp.dhis.period.Period;

public class SumValue extends CalculationValue<SumPartialValue> {

	public SumValue(Set<SumPartialValue> calculationPartialValues, Sum calculation, Period period, CalculationEntity entity) {
		super(new ArrayList<SumPartialValue>(calculationPartialValues), calculation, period, entity);
	}
	
	public SumValue(List<SumPartialValue> calculationPartialValues, Sum calculation, Period period, CalculationEntity entity) {
		super(calculationPartialValues, calculation, period, entity);
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
