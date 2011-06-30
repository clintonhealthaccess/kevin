package org.chai.kevin.chart;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.chai.kevin.Organisation;
import org.chai.kevin.data.Data;
import org.chai.kevin.value.Value;
import org.hisp.dhis.period.Period;

public class Chart {

	private Organisation organisation;
	private Data<?> data;
	private List<Period> periods;
	private Map<Period, Value> values;
	
	public Chart(Organisation organisation, Data<?> data, List<Period> periods, Map<Period, Value> values) {
		this.organisation = organisation;
		this.data = data;
		this.periods = periods;
		this.values = values;
	}

	public List<Period> getPeriods() {
		return periods;
	}
	
	public Value getValue(Period period) {
		return values.get(period);
	}
	
	public Organisation getOrganisation() {
		return organisation;
	}
	
	public Data<?> getData() {
		return data;
	}
	
	public String toJson() {
		DateFormat format = new SimpleDateFormat("yyyy");

		StringBuilder result = new StringBuilder();
		result.append("{");
		result.append("\"organisation\":");
		result.append("\""+organisation.getName()+"\"");
		result.append(",\"values\":{");
		for (Entry<Period, Value> entry : values.entrySet()) {
			result.append("\""+format.format(entry.getKey().getStartDate())+"\"");
			result.append(":");
			Double value = entry.getValue().getNumberValue();
			if (value != null && !value.isNaN() && !value.isInfinite()) result.append(value);
			else result.append("\"null\"");
			result.append(",");
		}
		if (values.size() != 0) result.deleteCharAt(result.length()-1);
		result.append("}");
		result.append("}");
		return result.toString();
	}
	
}
