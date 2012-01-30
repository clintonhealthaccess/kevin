package org.chai.kevin.planning;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.chai.kevin.location.DataLocationEntity;
import org.chai.kevin.value.RawDataElementValue;
import org.chai.kevin.value.ValidatableValue;
import org.chai.kevin.value.Value;
import org.chai.kevin.value.ValueService;
import org.hisp.dhis.period.Period;

public class ActivityService {

	private ValueService valueService;
	
	public List<Activity> getActivities(ActivityType type, DataLocationEntity location, Period period) {
		RawDataElementValue dataElementValue = valueService.getDataElementValue(type.getDataElement(), location, period);
		List<Activity> result = new ArrayList<Activity>();
		if (dataElementValue != null && !dataElementValue.getValue().isNull()) {
			for (int i = 0; i < dataElementValue.getValue().getListValue().size(); i++) {
				result.add(new Activity(type, dataElementValue, i));
			}
		}
		return result;
	}
	
	public Activity getActivity(ActivityType type, DataLocationEntity location, Period period, Integer lineNumber) {
		RawDataElementValue dataElementValue = getDataElementValue(type, location, period);
		
		return new Activity(type, dataElementValue, lineNumber);
	}
	
	private RawDataElementValue getDataElementValue(ActivityType type, DataLocationEntity location, Period period) {
		RawDataElementValue dataElementValue = valueService.getDataElementValue(type.getDataElement(), location, period);
		if (dataElementValue == null) {
			dataElementValue = new RawDataElementValue(type.getDataElement(), location, period, Value.NULL_INSTANCE());
			valueService.save(dataElementValue);
		}
		return dataElementValue;
	}
	
	public void modify(DataLocationEntity location, Period period, ActivityType type, Integer lineNumber, Map<String, Object> params) {
		Activity activity = getActivity(type, location, period, lineNumber);
		ValidatableValue validatable = activity.getValidatable();
		
		// first we merge the values to create a new value
		activity.mergeValues(params);
		
		// second we run the validation rules
		// TODO
		
		// third we set and save the value
		activity.save(valueService);
	}

	public void setValueService(ValueService valueService) {
		this.valueService = valueService;
	}
	
}
