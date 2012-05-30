package org.chai.kevin.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.chai.kevin.Period;
import org.chai.kevin.entity.export.Exportable;
import org.chai.kevin.location.CalculationLocation;
import org.chai.kevin.location.DataLocation;
import org.chai.kevin.location.DataLocationType;
import org.chai.kevin.util.Utils;
import org.chai.kevin.value.AggregationPartialValue;
import org.chai.kevin.value.AggregationValue;
import org.chai.kevin.value.ExpressionService;
import org.chai.kevin.value.ExpressionService.StatusValuePair;
import org.chai.kevin.value.Value;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity(name = "Aggregation")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Table(name = "dhsst_data_calculation_aggregation")
public class Aggregation extends Calculation<AggregationPartialValue> {

	@Override
	public AggregationValue getCalculationValue(
			List<AggregationPartialValue> partialValues, Period period,
			CalculationLocation location) {
		return new AggregationValue(partialValues, this, period, location);
	}

	@Override
	@Transient
	public Class<AggregationPartialValue> getValueClass() {
		return AggregationPartialValue.class;
	}

	@Override
	public AggregationPartialValue getCalculationPartialValue(
			String expression, Map<DataLocation, StatusValuePair> values,
			CalculationLocation location, Period period, DataLocationType type) {
		Value value = getValue(values.values());
		return new AggregationPartialValue(this, location, period, type,
				expression, value);
	}

	@Override
	@Transient
	public List<String> getPartialExpressions() {
		List<String> result = new ArrayList<String>();
		result.addAll(ExpressionService.getVariables(getExpression()));
		return result;
	}

	@Override
	public String toString() {
		return "Aggregation[getId()=" + getId() + ", getCode()="
				+ getCode() + ", getExpression()='" + getExpression() + "']";
	}

	@Override
	public String toExportString() {
		return "[" + Utils.formatExportCode(getCode()) + "]";
	}

}
