package org.chai.kevin.chart;

import java.util.HashMap;
import java.util.Map;

import org.chai.kevin.Organisation;
import org.chai.kevin.PeriodService;
import org.chai.kevin.ValueService;
import org.chai.kevin.data.Calculation;
import org.chai.kevin.data.Data;
import org.chai.kevin.data.DataElement;
import org.chai.kevin.data.Expression;
import org.chai.kevin.value.CalculationValue;
import org.chai.kevin.value.DataValue;
import org.chai.kevin.value.ExpressionValue;
import org.chai.kevin.value.Value;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.period.Period;

public class ChartService {

	public PeriodService periodService;
	public ValueService	valueService;

	public <T extends Value> Chart getChart(Data<T> element, Organisation organisation) {
		Map<Period, Value> values = new HashMap<Period, Value>();
		for (Period period : periodService.getPeriods()) {
			Value value = valueService.getValue(element, organisation.getOrganisationUnit(), period);
			values.put(period, value);
		}
		return new Chart(organisation, element, periodService.getPeriods(), values);
	}
	
	public void setValueService(ValueService valueService) {
		this.valueService = valueService;
	}
	
	public void setPeriodService(PeriodService periodService) {
		this.periodService = periodService;
	}
	
}
