package org.chai.kevin;

import java.util.List;

import org.chai.kevin.data.Average;
import org.chai.kevin.value.AveragePartialValue;
import org.chai.kevin.value.Value;

public class AverageValue extends CalculationValue<AveragePartialValue> {

	public AverageValue(List<AveragePartialValue> calculationPartialValues, Average calculation) {
		super(calculationPartialValues, calculation);
	}

	@Override
	public Value getValue() {
		Double sum = 0d;
		Integer num = 0;
		for (AveragePartialValue averagePartialValue : getCalculationPartialValues()) {
			if (!averagePartialValue.getValue().isNull()) {
				sum += averagePartialValue.getValue().getNumberValue().doubleValue();
				num += averagePartialValue.getNumberOfFacilities();
			}
		}
		Double average = sum / num;
		if (average.isNaN() || average.isInfinite()) average = null;
		
		return getCalculation().getType().getValue(average); 
	}
	
}
