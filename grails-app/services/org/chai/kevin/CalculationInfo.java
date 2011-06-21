package org.chai.kevin;

import java.util.Map;

import org.chai.kevin.value.CalculationValue;
import org.chai.kevin.value.ExpressionValue;
import org.chai.kevin.value.Value;

public class CalculationInfo extends Info {

	private CalculationValue calculationValue;
	private Map<Organisation, ExpressionValue> expressionValues;
	
	public CalculationInfo(CalculationValue calculationValue,
			Map<Organisation, ExpressionValue> expressionValues) {
		this.calculationValue = calculationValue;
		this.expressionValues = expressionValues;
	}

	public Calculation getCalculation() {
		return calculationValue.getCalculation();
	}
	
	public Value getCalculationValue() {
		return calculationValue;
	}
	
	public String getTemplate() {
		return "/info/calculationInfo";
	}
	
	public Map<Organisation, ExpressionValue> getExpressionValues() {
		return expressionValues;
	}
	
	@Override
	public String getValue() {
		if (calculationValue.getAverage() == null) return null;
		return String.valueOf(calculationValue.getAverage());
	}
	
}
