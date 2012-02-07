package org.chai.kevin.planning;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.chai.kevin.Translation;
import org.chai.kevin.data.DataService;
import org.chai.kevin.data.EnumOption;
import org.chai.kevin.data.Type.ValueType;
import org.chai.kevin.value.RawDataElementValue;
import org.chai.kevin.value.ValidatableValue;
import org.chai.kevin.value.Value;
import org.chai.kevin.value.ValueService;

public class PlanningLine {

	private RawDataElementValue dataElementValue;
	private Integer lineNumber;
	private PlanningType type;
	private ValidatableValue validatable;
	
	public PlanningLine(PlanningType type, RawDataElementValue value, Integer lineNumber) {
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
		getValidatable().mergeValue(params, "elements["+type.getId()+"].value");
	}
	
	private List<String> getLineNumbers() {
		List<String> result = new ArrayList<String>();
		Integer linesInValue = dataElementValue.getValue().isNull()?0:dataElementValue.getValue().getListValue().size();
		for (int i = 0; i <= Math.max(linesInValue-1, lineNumber); i++) {
			result.add("["+i+"]");
		}
		return result;
	}

	private String getDiscriminatorValue() {
		return type.getDataElement().getType().getValue(dataElementValue.getValue(), "["+lineNumber+"]"+type.getDiscriminator()).getStringValue();
	}
	
	public List<PlanningCost> getPlanningCosts() {
		return type.getPlanningCosts(getDiscriminatorValue());
	}
	
	public Translation getNames(DataService dataService) {
		if (type.getDiscriminatorType().getType() == ValueType.ENUM) {
			EnumOption option = dataService.findEnumByCode(type.getDiscriminatorType().getEnumCode()).getOptionForValue(getDiscriminatorValue());
			if (option != null) return option.getNames();
			return null;
		}
		else return null;
	}
	
}
