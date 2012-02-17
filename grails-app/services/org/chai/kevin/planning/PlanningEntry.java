package org.chai.kevin.planning;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.chai.kevin.data.Enum;
import org.chai.kevin.value.ValidatableValue;
import org.chai.kevin.value.Value;

public class PlanningEntry {

	private static final String BUDGET_UPDATED = "budget_updated";
	private static final String SUBMITTED = "submitted";
	private static final Set<String> ATTRIBUTES = new HashSet<String>();
			
	static {
		ATTRIBUTES.add(BUDGET_UPDATED);
		ATTRIBUTES.add(SUBMITTED);
	}
	
	private Integer lineNumber;
	private PlanningType type;
	private Map<String, Enum>  enums;
	private ValidatableValue validatable;
	
	public PlanningEntry() {}
	
	public PlanningEntry(ValidatableValue validatable, Integer lineNumber) {
		this.validatable = validatable;
		this.lineNumber = lineNumber;
		this.type = null;
		this.enums = null;
	}
	
	public PlanningEntry(PlanningType type, ValidatableValue validatable, Integer lineNumber, Map<String, Enum> enums) {
		this.validatable = validatable;
		this.type = type;
		this.lineNumber = lineNumber;
		this.enums = enums;
	}

	public boolean isBudgetUpdated() {
		return isAttributeSet(BUDGET_UPDATED);
	}

	public void setBudgetUpdated(Boolean value) {
		setAttribute(BUDGET_UPDATED, value);
	}

	public boolean isSubmitted() {
		return isAttributeSet(SUBMITTED);
	}
	
	public void setSubmitted(Boolean value) {
		setAttribute(SUBMITTED, value);
	}

	public Set<String> getInvalidSections() {
		Set<String> result = new HashSet<String>();
		for (String section : type.getSections()) {
			if (!getValidatable().isTreeValid(getPrefix(section))) result.add(section);
		}
		return result;
	}
	
	public Set<String> getIncompleteSections() {
		Set<String> result = new HashSet<String>();
		for (String section : type.getSections()) {
			if (!getValidatable().isTreeComplete(getPrefix(section))) result.add(section);
		}
		return result;
	}
	
//	public boolean isValid() {
//		return getValidatable().isTreeValid(getPrefix("[_]"));
//	}
//	
//	public boolean isComplete() {
//		return getValidatable().isTreeComplete(getPrefix("[_]"));
//	}
	
	private boolean isAttributeSet(String attribute) {
		if (getValue().getAttribute(attribute) == null) return false;
		return getValue().getAttribute(attribute).equals(Boolean.TRUE.toString());
	}
	
	private void setAttribute(String attribute, Boolean value) {
		validatable.getType().setAttribute(validatable.getValue(), getPrefix("[_]"), attribute, value.toString());
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
		return prefix.replaceFirst("^\\[_\\]", "["+lineNumber+"]");
	}
	
	public Value getValue(String prefix) {
		return validatable.getType().getValue(validatable.getValue(), getPrefix(prefix));
	}
	
	public Value getDiscriminatorValue() {
		return getValue(type.getDiscriminator());
	}
	
	private Value getValue() {
		return getValue("[_]");
	}

	public void mergeValues(Map<String, Object> params) {
		params.put("elements["+type.getId()+"].value", getLineNumbers(null));
		getValidatable().mergeValue(params, "elements["+type.getId()+"].value", ATTRIBUTES);
	}

	public void delete() {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("elements["+type.getId()+"].value", getLineNumbers(lineNumber));
		getValidatable().mergeValue(params, "elements["+type.getId()+"].value", ATTRIBUTES);
	}
	
	private List<String> getLineNumbers(Integer skip) {
		List<String> result = new ArrayList<String>();
		Integer linesInValue = validatable.getValue().isNull()?0:validatable.getValue().getListValue().size();
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
	
}
