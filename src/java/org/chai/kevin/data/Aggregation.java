package org.chai.kevin.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.chai.kevin.AggregationValue;
import org.chai.kevin.ExpressionService;
import org.chai.kevin.ExpressionService.StatusValuePair;
import org.chai.kevin.Organisation;
import org.chai.kevin.value.AggregationPartialValue;
import org.chai.kevin.value.Value;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hisp.dhis.period.Period;

@Entity(name="Aggregation")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Table(name="dhsst_calculation_aggregation")
public class Aggregation extends Calculation<AggregationPartialValue> {

	@Override
	public AggregationValue getCalculationValue(List<AggregationPartialValue> partialValues) {
		return new AggregationValue(partialValues, this);
	}

	@Override
	public Class<AggregationPartialValue> getValueClass() {
		return AggregationPartialValue.class;
	}

	@Override
	public AggregationPartialValue getCalculationPartialValue(String expression, Map<Organisation, StatusValuePair> values, Organisation organisation, Period period, String groupUuid) {
		Value value = getValue(values.values());
		return new AggregationPartialValue(this, organisation.getOrganisationUnit(), period, groupUuid, expression, value);
	}

	@Override
	public List<String> getPartialExpressions() {
		List<String> result = new ArrayList<String>();
		result.addAll(ExpressionService.getVariables(getExpression()));
		return result;
	}

}
