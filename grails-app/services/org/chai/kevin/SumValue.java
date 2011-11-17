package org.chai.kevin;

import java.util.List;

import org.chai.kevin.data.Sum;
import org.chai.kevin.value.SumPartialValue;
import org.chai.kevin.value.Value;

public class SumValue extends CalculationValue<SumPartialValue> {

	public SumValue(List<SumPartialValue> calculationPartialValues, Sum calculation) {
		super(calculationPartialValues, calculation);
	}

	@Override
	public Value getValue() {
		Double value = 0d;
		for (SumPartialValue partialValue : getCalculationPartialValues()) {
			if (!partialValue.getValue().isNull()) value += partialValue.getValue().getNumberValue().doubleValue();
		}
		return getCalculation().getType().getValue(value);
	}
	
}
