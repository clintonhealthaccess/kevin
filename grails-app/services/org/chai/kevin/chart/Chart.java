package org.chai.kevin.chart;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.chai.kevin.Organisation;
import org.chai.kevin.data.Data;
import org.chai.kevin.value.DataValue;
import org.chai.kevin.value.StoredValue;
import org.hisp.dhis.period.Period;

public class Chart {

	private Organisation organisation;
	private Data<?> data;
	private List<Period> periods;
	private Map<Period, DataValue> values;
	
	public Chart(Organisation organisation, Data<?> data, List<Period> periods, Map<Period, DataValue> values) {
		this.organisation = organisation;
		this.data = data;
		this.periods = periods;
		this.values = values;
	}

	public List<Period> getPeriods() {
		return periods;
	}
	
	public DataValue getValue(Period period) {
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
		for (Entry<Period, DataValue> entry : values.entrySet()) {
			result.append("\""+format.format(entry.getKey().getStartDate())+"\"");
			result.append(":");
			Double value = entry.getValue().getValue().getNumberValue()!=null?entry.getValue().getValue().getNumberValue().doubleValue():null;
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
