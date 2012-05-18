package org.chai.kevin.value;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.chai.kevin.Period;
import org.chai.kevin.data.Average;
import org.chai.kevin.location.CalculationLocation;

public class AverageValue extends CalculationValue<AveragePartialValue> {

	public AverageValue(Set<AveragePartialValue> calculationPartialValues, Average calculation, Period period, CalculationLocation location) {
		super(new ArrayList<AveragePartialValue>(calculationPartialValues), calculation, period, location);
	}
	
	public AverageValue(List<AveragePartialValue> calculationPartialValues, Average calculation, Period period, CalculationLocation location) {
		super(calculationPartialValues, calculation, period, location);
	}
	

	@Override
	public Value getValue() {
		Double sum = 0d;
		Integer num = 0;
		for (AveragePartialValue averagePartialValue : getCalculationPartialValues()) {
			if (!averagePartialValue.getValue().isNull()) {
				sum += averagePartialValue.getValue().getNumberValue().doubleValue();
				num += averagePartialValue.getNumberOfDataLocations();
			}
		}
		Double average = sum / num;
		if (average.isNaN() || average.isInfinite()) average = null;
		
		return getData().getType().getValue(average); 
	}

	@Override
	public String toString() {
		return "AverageValue [getValue()=" + getValue() + "]";
	}

	@Override
	public Date getTimestamp() {
		return null;
	}

}
