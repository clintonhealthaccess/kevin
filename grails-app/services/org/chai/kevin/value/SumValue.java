package org.chai.kevin.value;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.chai.kevin.Period;
import org.chai.kevin.data.Sum;
import org.chai.kevin.location.CalculationLocation;

public class SumValue extends CalculationValue<SumPartialValue> {
	
	public SumValue(Set<SumPartialValue> calculationPartialValues, Sum calculation, Period period, CalculationLocation location) {
		super(new ArrayList<SumPartialValue>(calculationPartialValues), calculation, period, location);
	}
	
	public SumValue(List<SumPartialValue> calculationPartialValues, Sum calculation, Period period, CalculationLocation location) {
		super(calculationPartialValues, calculation, period, location);
	}
	
	
	@Override
	public Value getValue() {
		if (getLocation().collectsData()) {
			return getDataLocationValue();
		}
		Double sum = 0d;
		for (SumPartialValue partialValue : getCalculationPartialValues()) {
			if (!partialValue.getValue().isNull()) sum += partialValue.getValue().getNumberValue().doubleValue();
		}
		
		return getData().getType().getValue(sum);
	}
	
	@Override
	public Value getRatio(){
		if (getLocation().collectsData()) {
			return getDataLocationValue();
		}
		
		Double sum = 0d;
		Integer num = 0;
		for (SumPartialValue sumPartialValue : getCalculationPartialValues()) {
			if (!sumPartialValue.getValue().isNull()) {
				sum += sumPartialValue.getValue().getNumberValue().doubleValue();
				num += sumPartialValue.getNumberOfDataLocations();
			}
		}
		
		Double average = sum / num;
		if (average.isNaN() || average.isInfinite()) average = null;
		return getData().getType().getValue(average);
	}
		

	
	private Value getDataLocationValue(){
		if (getCalculationPartialValues().size() > 1) throw new IllegalStateException("calculation for DataLocation does not contain only one partial value");
		if (getCalculationPartialValues().size() == 0) return Value.NULL_INSTANCE();
		return getCalculationPartialValues().get(0).getValue();
	}
	
	@Override
	public String toString() {
		return "SumValue [getValue()=" + getValue() + "]";
	}

	@Override
	public Date getTimestamp() {
		return null;
	}
	
}
