package org.chai.kevin;

import java.util.Map;

import org.chai.kevin.value.CalculationValue;
import org.chai.kevin.value.DataValue;
import org.chai.kevin.value.ExpressionValue;
import org.hisp.dhis.period.Period;

public class InfoService {

	private ValueService valueService;
	private ExpressionService expressionService;
	
	public ExpressionInfo getInfo(Expression expression, Organisation organisation, Period period) {
		ExpressionValue expressionValue = valueService.getExpressionValue(organisation.getOrganisationUnit(), expression, period);
		if (expressionValue == null) return null;
		Map<Organisation, Map<DataElement, DataValue>> calculateDataValues = expressionService.calculateDataValues(expression, period, organisation);
		return new ExpressionInfo(expressionValue, calculateDataValues);
	}
	
	public CalculationInfo getInfo(Calculation calculation, Organisation organisation, Period period) {
		CalculationValue calculationValue = valueService.getCalculationValue(organisation.getOrganisationUnit(), calculation, period);
		if (calculationValue == null) return null;
		Map<Organisation, ExpressionValue> expressionValues = expressionService.calculateExpressionValues(calculation, period, organisation);
		CalculationInfo info = new CalculationInfo(calculationValue, expressionValues);
		return info;
	}
	
	public void setValueService(ValueService valueService) {
		this.valueService = valueService;
	}
	
	public void setExpressionService(ExpressionService expressionService) {
		this.expressionService = expressionService;
	}
	
}
