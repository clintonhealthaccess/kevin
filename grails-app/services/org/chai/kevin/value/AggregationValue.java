package org.chai.kevin.value;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang.math.NumberUtils;
import org.chai.kevin.JaqlService;
import org.chai.kevin.Period;
import org.chai.kevin.data.Aggregation;
import org.chai.kevin.location.CalculationEntity;
import org.chai.kevin.value.AggregationPartialValue;
import org.chai.kevin.value.Value;

import com.ibm.jaql.json.type.JsonValue;

public class AggregationValue extends CalculationValue<AggregationPartialValue> {

	public AggregationValue(Set<AggregationPartialValue> calculationPartialValues, Aggregation calculation, Period period, CalculationEntity entity) {
		super(new ArrayList<AggregationPartialValue>(calculationPartialValues), calculation, period, entity);
	}
	
	public AggregationValue(List<AggregationPartialValue> calculationPartialValues, Aggregation calculation, Period period, CalculationEntity entity) {
		super(calculationPartialValues, calculation, period, entity);
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
		for (Entry<String, Double> entry : values.entrySet()) {
			stringValues.put(entry.getKey(), entry.getValue().toString());
		}
		JsonValue value = JaqlService.jsonValue(getData().getExpression(), stringValues);
		
		String stringValue = null;
		if (NumberUtils.isNumber(value.toString())) stringValue = value.toString();
		return getData().getType().getValueFromJaql(stringValue);
	}

	@Override
	public String toString() {
		return "AggregationValue [getValue()=" + getValue() + "]";
	}
	
}
