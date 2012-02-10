package org.chai.kevin.planning;

import java.util.ArrayList;
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
	private Enum enume;
	private ValidatableValue validatable;
	
	public PlanningEntry(PlanningType type, RawDataElementValue value, Integer lineNumber, Enum enume) {
		this.dataElementValue = value;
		this.type = type;
		this.lineNumber = lineNumber;
		this.enume = enume;
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
	
	private Value getValue() {
		return dataElementValue.getData().getType().getValue(dataElementValue.getValue(), getPrefix("[_]"));
	}
	
	public Value getValue(String prefix) {
		return dataElementValue.getData().getType().getValue(dataElementValue.getValue(), getPrefix(prefix));
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
		return type.getDataElement().getType().getValue(dataElementValue.getValue(), getPrefix(type.getDiscriminator())).getStringValue();
	}
	
	public List<PlanningCost> getPlanningCosts() {
		return type.getPlanningCosts(getDiscriminatorValue());
	}
	
	public Translation getNames() {
		if (enume.getOptionForValue(getDiscriminatorValue())!=null) {
			return enume.getOptionForValue(getDiscriminatorValue()).getNames();
		}
		return null;
	}

}
