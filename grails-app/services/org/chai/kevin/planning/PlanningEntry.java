package org.chai.kevin.planning;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.chai.kevin.data.Enum;
import org.chai.kevin.form.FormElement.ElementCalculator;
import org.chai.kevin.location.DataLocation;
import org.chai.kevin.value.ValidatableValue;
import org.chai.kevin.value.Value;

public class PlanningEntry {

	protected PlanningType type;
	private DataLocation dataLocation;
	private Integer lineNumber;
	private Map<String, Enum>  enums;
	private ValidatableValue validatable;
	
	public PlanningEntry() {
		// allows mocking
	}
	
	public PlanningEntry(ValidatableValue validatable, Integer lineNumber) {
		this.validatable = validatable;
		this.lineNumber = lineNumber;
		this.type = null;
		this.enums = null;
		this.dataLocation = null;
	}
	
	public PlanningEntry(DataLocation dataLocation, PlanningType type, ValidatableValue validatable, Integer lineNumber, Map<String, Enum> enums) {
		this.dataLocation = dataLocation;
		this.validatable = validatable;
		this.type = type;
		this.lineNumber = lineNumber;
		this.enums = enums;
	}

//	public String getUuid() {
//		return getValue().getAttribute(UUID);
//	}
//	
//	protected void setUuid(String uuid) {
//		if (getValue().getAttribute(UUID) != null) throw new IllegalStateException("uuid already set");
//		setAttribute(UUID, uuid);
//	}
	
	private void setAttribute(String attribute, String value) {
		validatable.getType().setAttribute(validatable.getValue(), PlanningUtils.getPrefix("[_]", lineNumber), attribute, value);
	}
	
	public Set<String> getInvalidSections() {
		Set<String> result = new HashSet<String>();
		for (String section : type.getSections()) {
			if (!getValidatable().isTreeValid(PlanningUtils.getPrefix(section, lineNumber))) result.add(section);
		}
		return result;
	}
	
	public Set<String> getIncompleteSections() {
		Set<String> result = new HashSet<String>();
		for (String section : type.getSections()) {
			if (!getValidatable().isTreeComplete(PlanningUtils.getPrefix(section, lineNumber))) result.add(section);
		}
		return result;
	}
	
	public String getLineSuffix(String section) {
		return "";
	}
	
	public Integer getLineNumber() {
		return lineNumber;
	}
	
	public ValidatableValue getValidatable() {
		return validatable;
	}
	
	public String getPrefix(String prefix) {
		return PlanningUtils.getPrefix(prefix, lineNumber);
	}
	
	public Value getValue(String prefix) {
		return validatable.getType().getValue(validatable.getValue(), PlanningUtils.getPrefix(prefix, lineNumber));
	}
	
	public Value getFixedHeaderValue() {
		return getValue(type.getFixedHeader());
	}
	
	private Value getValue() {
		return getValue("[_]");
	}

	public void mergeValues(Map<String, Object> params) {
		params.put("elements["+type.getFormElement().getId()+"].value", getLineNumbers(null));
		getValidatable().mergeValue(params, "elements["+type.getFormElement().getId()+"].value", new HashSet<String>());
	}

	public void delete() {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("elements["+type.getFormElement().getId()+"].value", getLineNumbers(lineNumber));
		getValidatable().mergeValue(params, "elements["+type.getFormElement().getId()+"].value", new HashSet<String>());
	}
	
	private List<String> getLineNumbers(Integer skip) {
		List<String> result = new ArrayList<String>();
		Integer linesInValue = validatable.getValue().isNull()?0:validatable.getValue().getListValue().size();
		for (int i = 0; i <= Math.max(linesInValue-1, lineNumber); i++) {
			if (skip == null || i != skip) result.add("["+i+"]");
		}
		return result;
	}
	
	public Map<String, Enum> getEnums() {
		return enums;
	}

	public void evaluateRules(ElementCalculator elementCalculator) {
		type.getFormElement().validate(dataLocation, elementCalculator);
		type.getFormElement().executeSkip(dataLocation, elementCalculator);
	}

}
