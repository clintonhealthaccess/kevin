package org.chai.kevin.chart;

import java.util.HashMap;
import java.util.Map;

import org.chai.kevin.Organisation;
import org.chai.kevin.PeriodService;
import org.chai.kevin.ValueService;
import org.chai.kevin.data.Data;
import org.chai.kevin.value.StoredValue;
import org.hisp.dhis.period.Period;

public class ChartService {

	public PeriodService periodService;
	public ValueService	valueService;

	public <T extends StoredValue> Chart getChart(Data<T> element, Organisation organisation) {
		Map<Period, StoredValue> values = new HashMap<Period, StoredValue>();
		for (Period period : periodService.getPeriods()) {
			StoredValue value = valueService.getValue(element, organisation.getOrganisationUnit(), period);
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
