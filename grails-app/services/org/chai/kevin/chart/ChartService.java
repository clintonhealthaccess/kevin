package org.chai.kevin.chart;

import java.util.HashMap;
import java.util.Map;

import org.chai.kevin.Period;
import org.chai.kevin.PeriodService;
import org.chai.kevin.data.DataElement;
import org.chai.kevin.location.DataLocation;
import org.chai.kevin.value.DataValue;
import org.chai.kevin.value.ValueService;

public class ChartService {

	public PeriodService periodService;
	public ValueService	valueService;

	public <T extends DataValue> Chart getChart(DataElement<T> element, DataLocation dataLocation) {
		Map<Period, DataValue> values = new HashMap<Period, DataValue>();
		for (Period period : periodService.listPeriods()) {
			DataValue value = valueService.getDataElementValue(element, dataLocation, period);
			values.put(period, value);
		}
		return new Chart(dataLocation, element, periodService.listPeriods(), values);
	}
	
	public void setValueService(ValueService valueService) {
		this.valueService = valueService;
	}
	
	public void setPeriodService(PeriodService periodService) {
		this.periodService = periodService;
	}
	
}
