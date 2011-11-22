package org.chai.kevin;

import java.util.List;

import org.chai.kevin.data.Average;
import org.chai.kevin.value.AveragePartialValue;
import org.chai.kevin.value.Value;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.period.Period;

public class AverageValue extends CalculationValue<AveragePartialValue> {

	public AverageValue(List<AveragePartialValue> calculationPartialValues, Average calculation, Period period, OrganisationUnit organisationUnit) {
		super(calculationPartialValues, calculation, period, organisationUnit);
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
		
		return getData().getType().getValue(average); 
	}

	@Override
	public String toString() {
		return "AverageValue [getValue()=" + getValue() + "]";
	}

}
