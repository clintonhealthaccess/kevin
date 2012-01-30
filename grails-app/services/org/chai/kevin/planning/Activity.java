package org.chai.kevin.planning;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.chai.kevin.value.RawDataElementValue;
import org.chai.kevin.value.ValidatableValue;
import org.chai.kevin.value.Value;
import org.chai.kevin.value.ValueService;

public class Activity {

	private RawDataElementValue dataElementValue;
	private Integer lineNumber;
	private ActivityType type;
	private ValidatableValue validatable;
	
	public Activity(ActivityType type, RawDataElementValue value, Integer lineNumber) {
		this.dataElementValue = value;
		this.type = type;
		this.lineNumber = lineNumber;
	}

	public String getLineSuffix(String section) {
		return "";
	}
	
	public Integer getLineNumber() {
		return lineNumber;
	}
	
	public ValidatableValue getValidatable() {
		if (validatable == null) validatable = new ValidatableValue(dataElementValue.getValue(), type.getDataElement().getType());
		return validatable;
	}
	
	public Value getValue(String section) {
		return dataElementValue.getData().getType().getValue(dataElementValue.getValue(), "["+lineNumber+"]"+section);
	}
	
	public void save(ValueService valueService) {
		valueService.save(dataElementValue);
	}

	public void mergeValues(Map<String, Object> params) {
		params.put("elements["+type.getId()+"].value", getLineNumbers());
		validatable.mergeValue(params, "elements["+type.getId()+"].value");
	}
	
	private List<String> getLineNumbers() {
		List<String> result = new ArrayList<String>();
		Integer linesInValue = dataElementValue.getValue().isNull()?0:dataElementValue.getValue().getListValue().size();
		for (int i = 0; i <= Math.max(linesInValue, lineNumber); i++) {
			result.add("["+i+"]");
		}
		return result;
	}
	
}
