package org.chai.kevin.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.chai.kevin.location.CalculationEntity;
import org.chai.kevin.location.DataEntity;
import org.chai.kevin.location.DataEntityType;
import org.chai.kevin.value.AggregationPartialValue;
import org.chai.kevin.value.AggregationValue;
import org.chai.kevin.value.ExpressionService;
import org.chai.kevin.value.ExpressionService.StatusValuePair;
import org.chai.kevin.value.Value;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hisp.dhis.period.Period;

@Entity(name="Aggregation")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Table(name="dhsst_data_calculation_aggregation")
public class Aggregation extends Calculation<AggregationPartialValue> {

	@Override
	public AggregationValue getCalculationValue(List<AggregationPartialValue> partialValues, Period period, CalculationEntity entity) {
		return new AggregationValue(partialValues, this, period, entity);
	}

	@Override
	@Transient
	public Class<AggregationPartialValue> getValueClass() {
		return AggregationPartialValue.class;
	}

	@Override
	public AggregationPartialValue getCalculationPartialValue(String expression, Map<DataEntity, StatusValuePair> values, CalculationEntity entity, Period period, DataEntityType type) {
		Value value = getValue(values.values());
		return new AggregationPartialValue(this, entity, period, type, expression, value);
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
		return "Aggregation [getExpression()=" + getExpression() + ", getId()="
				+ getId() + ", getCode()=" + getCode() + "]";
	}

}
