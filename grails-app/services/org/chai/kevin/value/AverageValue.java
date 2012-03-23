package org.chai.kevin.value;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.chai.kevin.data.Average;
import org.chai.kevin.location.CalculationEntity;
import org.hisp.dhis.period.Period;

public class AverageValue extends CalculationValue<AveragePartialValue> {

	public AverageValue(Set<AveragePartialValue> calculationPartialValues, Average calculation, Period period, CalculationEntity entity) {
		super(new ArrayList<AveragePartialValue>(calculationPartialValues), calculation, period, entity);
	}
	
	public AverageValue(List<AveragePartialValue> calculationPartialValues, Average calculation, Period period, CalculationEntity entity) {
		super(calculationPartialValues, calculation, period, entity);
	}
	

	@Override
	public Value getValue() {
		Double sum = 0d;
		Integer num = 0;
		for (AveragePartialValue averagePartialValue : getCalculationPartialValues()) {
			if (!averagePartialValue.getValue().isNull()) {
				sum += averagePartialValue.getValue().getNumberValue().doubleValue();
				num += averagePartialValue.getNumberOfDataEntities();
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

}
