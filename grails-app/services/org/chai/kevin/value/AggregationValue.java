package org.chai.kevin.value;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.math.NumberUtils;
import org.chai.kevin.JaqlService;
import org.chai.kevin.Period;
import org.chai.kevin.data.Aggregation;
import org.chai.location.CalculationLocation;

import com.ibm.jaql.json.type.JsonValue;

public class AggregationValue extends CalculationValue<AggregationPartialValue> {

	public AggregationValue(Set<AggregationPartialValue> calculationPartialValues, Aggregation calculation, Period period, CalculationLocation location) {
		super(new ArrayList<AggregationPartialValue>(calculationPartialValues), calculation, period, location);
	}
	
	public AggregationValue(List<AggregationPartialValue> calculationPartialValues, Aggregation calculation, Period period, CalculationLocation location) {
		super(calculationPartialValues, calculation, period, location);
	}

	@Override
	public boolean isNull(){
		return getValue().isNull();
	}
	
	@Override
	public Value getValue() {
		Map<String, Double> values = new HashMap<String, Double>();
		for (AggregationPartialValue aggregationPartialValue : getCalculationPartialValues()) {
			if (!aggregationPartialValue.getValue().isNull()) {
				if (!values.containsKey(aggregationPartialValue.getExpressionData())) {
					values.put(aggregationPartialValue.getExpressionData(), 0d);
				}
				Double value = values.get(aggregationPartialValue.getExpressionData());
				value += aggregationPartialValue.getValue().getNumberValue().doubleValue();
				values.put(aggregationPartialValue.getExpressionData(), value);
			}
		}
		Map<String, String> stringValues = new HashMap<String, String>();
		for (String expression : getData().getPartialExpressions()) {
			if (values.containsKey(expression)) stringValues.put(expression, values.get(expression).toString());
			else {
				if (getLocation().collectsData()) return Value.NULL_INSTANCE();
				else stringValues.put(expression, "0");
			}
		}
		
		JsonValue value = JaqlService.jsonValue(getData().getExpression(), stringValues);
		
		String stringValue = null;
		if (NumberUtils.isNumber(value.toString())) stringValue = value.toString();
		return getData().getType().getValueFromJaql(stringValue);
	}

	@Override
	public Value getAverage(){
		return getValue();
	}
	
	@Override
	public String toString() {
		return "AggregationValue [getValue()=" + getValue() + "]";
	}

	@Override
	public Date getTimestamp() {
		return null;
	}
	
}
