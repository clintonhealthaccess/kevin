package org.chai.kevin.chart;

import java.util.HashMap;
import java.util.Map;

import org.chai.kevin.Period;
import org.chai.kevin.PeriodService;
import org.chai.kevin.data.DataElement;
import org.chai.kevin.location.DataLocationEntity;
import org.chai.kevin.value.DataValue;
import org.chai.kevin.value.ValueService;

public class ChartService {

	public PeriodService periodService;
	public ValueService	valueService;

	public <T extends DataValue> Chart getChart(DataElement<T> element, DataLocationEntity entity) {
		Map<Period, DataValue> values = new HashMap<Period, DataValue>();
		for (Period period : periodService.getPeriods()) {
			DataValue value = valueService.getDataElementValue(element, entity, period);
			values.put(period, value);
		}
		return new Chart(entity, element, periodService.getPeriods(), values);
	}
	
	public void setValueService(ValueService valueService) {
		this.valueService = valueService;
	}
	
	public void setPeriodService(PeriodService periodService) {
		this.periodService = periodService;
	}
	
}
