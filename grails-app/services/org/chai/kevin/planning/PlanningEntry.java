package org.chai.kevin.planning;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.chai.kevin.Translation;
import org.chai.kevin.data.DataService;
import org.chai.kevin.data.Enum;
import org.chai.kevin.data.EnumOption;
import org.chai.kevin.data.Type.ValueType;
import org.chai.kevin.value.RawDataElementValue;
import org.chai.kevin.value.ValidatableValue;
import org.chai.kevin.value.Value;
import org.chai.kevin.value.ValueService;

public class PlanningEntry {

	private static final String BUDGET_UPDATED = "budget_updated";
	
	private RawDataElementValue dataElementValue;
	private Integer lineNumber;
	private PlanningType type;
	private Map<String, Enum>  enums;
	private ValidatableValue validatable;
	
	public PlanningEntry(RawDataElementValue value, Integer lineNumber) {
		this.dataElementValue = value;
		this.lineNumber = lineNumber;
	}
	
	public PlanningEntry(PlanningType type, RawDataElementValue value, Integer lineNumber, Map<String, Enum> enums) {
		this.dataElementValue = value;
		this.type = type;
		this.lineNumber = lineNumber;
		this.enums = enums;
	}

	public boolean isBudgetUpdated() {
		return getValue().getAttribute(BUDGET_UPDATED).equals(Boolean.TRUE.toString());
	}

	public void setBudgetUpdated(Boolean value) {
		getValue().setAttribute(BUDGET_UPDATED, value.toString());
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
	
	public String getPrefix(String prefix) {
		return prefix.replaceFirst("^\\[_\\]", "["+lineNumber+"]");
	}
	
	public Value getValue(String prefix) {
		return dataElementValue.getData().getType().getValue(dataElementValue.getValue(), getPrefix(prefix));
	}
	
	public Value getDiscriminatorValue() {
		return getValue(type.getDiscriminator());
	}
	
	private Value getValue() {
		return getValue("[_]");
	}

	public void mergeValues(Map<String, Object> params) {
		params.put("elements["+type.getId()+"].value", getLineNumbers(null));
		getValidatable().mergeValue(params, "elements["+type.getId()+"].value");
	}
	
	public void delete() {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("elements["+type.getId()+"].value", getLineNumbers(lineNumber));
		getValidatable().mergeValue(params, "elements["+type.getId()+"].value");
	}
	
	private List<String> getLineNumbers(Integer skip) {
		List<String> result = new ArrayList<String>();
		Integer linesInValue = dataElementValue.getValue().isNull()?0:dataElementValue.getValue().getListValue().size();
		for (int i = 0; i <= Math.max(linesInValue-1, lineNumber); i++) {
			if (skip == null || i != skip) result.add("["+i+"]");
		}
		return result;
	}
	
	public List<PlanningCost> getPlanningCosts() {
		return type.getPlanningCosts(getDiscriminatorValue().getStringValue());
	}

	public Map<String, Enum> getEnums() {
		return enums;
	}
	
	// TODO move to PlanningList
	public void save(ValueService valueService) {
		valueService.save(dataElementValue);
	}
}
