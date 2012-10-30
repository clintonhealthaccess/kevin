package org.chai.kevin.form;

import java.util.List
import java.util.Map
import java.util.Set
import java.util.Map.Entry

import org.chai.kevin.Exportable
import org.chai.kevin.Period
import org.chai.kevin.data.RawDataElement
import org.chai.kevin.data.Type
import org.chai.kevin.data.Type.ValuePredicate
import org.chai.kevin.form.FormValidationService.ValidatableLocator
import org.chai.kevin.util.Utils
import org.chai.kevin.value.RawDataElementValue
import org.chai.kevin.value.Value
import org.chai.kevin.value.ValueService
import org.chai.location.DataLocation

class FormElement implements Exportable {

	public static String FIELD_DELIMITER = ",";
	
	Long id;
	// TODO get rid of this
	String code;
	RawDataElement dataElement;

	static transients = ['headers']
	
	static hasMany = [
		validationRules: FormValidationRule,
		formElementHeadersMaps: FormElementHeadersMap
	]
	
	static mapping = {
		table 'dhsst_form_element'
		tablePerHierarchy false
		formElementHeadersMaps cascade: "all-delete-orphan"
		dataElement column: 'dataElement'
	}
	
	static constraints = {
		dataElement (nullable: false)
		code (nullable: true)
	}
	
	public FormElement() {
		super();
	}

	Map<String, Map<String, String>> getHeaders() {
		Map result = [:]
		formElementHeadersMaps?.each {
			result.put(it.header, it.getNamesMap())
		}
		return result;
	}

	void setHeaders(Map<String, Map<String, String>> headers) {
		formElementHeadersMaps?.clear()
		headers.each {
			def headerMap = new FormElementHeadersMap(header: it.key)
			headerMap.setNamesMap(it.value)
			addToFormElementHeadersMaps(headerMap)
		}
	}
	
	public String getDescriptionTemplate() {
		return "/entity/form/formElementDescription";
	}
	
	String getLabel() {
		dataElement.names
	}
	
	public <T extends FormElement> void deepCopy(T copy, FormCloner cloner) {
		copy.setDataElement(getDataElement());
	}	
	
	public void copyRules(FormElement copy, FormCloner cloner) {
		for (FormValidationRule rule : getValidationRules()) {
			copy.getValidationRules().add(cloner.getValidationRule(rule));
		}
		for (Entry<String, Map<String, String>> entry : getHeaders().entrySet()) {
			copy.getHeaders().put(entry.getKey(), entry.getValue());
		}
	}

	public void validate(DataLocation dataLocation, ElementCalculator calculator) {
		Set<FormValidationRule> validationRules = calculator.getFormElementService().searchValidationRules(this, dataLocation.getType());
		
		for (FormValidationRule validationRule : validationRules) {
			validationRule.evaluate(dataLocation, calculator);
		}
	}
	
	public void executeSkip(DataLocation dataLocation, ElementCalculator calculator) {
		Set<FormSkipRule> skipRules = calculator.getFormElementService().searchSkipRules(this);
		
		for (FormSkipRule skipRule: skipRules) {
			skipRule.evaluate(dataLocation, calculator);
		}
	}
	
	protected Value getValue(DataLocation dataLocation, final ElementSubmitter submitter) {
		FormEnteredValue enteredValue = submitter.getFormElementService().getOrCreateFormEnteredValue(dataLocation, this);
		final Type type = enteredValue.getType();
		Value valueToSave = new Value(enteredValue.getValue().getJsonValue());
		type.transformValue(valueToSave, new ValuePredicate() {
			@Override
			public boolean transformValue(Value currentValue, Type currentType, String currentPrefix) {
				return submitter.transformValue(currentValue, currentType, currentPrefix);
			}
		});
		return valueToSave;
	}
	
	public void submit(DataLocation dataLocation, Period period, ElementSubmitter submitter) {
		Value valueToSave = getValue(dataLocation, submitter);
		
		// TODO this value should be evicted at some point
		RawDataElementValue rawDataElementValue = submitter.getValueService().getDataElementValue(getDataElement(), dataLocation, period);
		if (rawDataElementValue == null) {
			rawDataElementValue = new RawDataElementValue(getDataElement(), dataLocation, period, null);
		}
		rawDataElementValue.setValue(valueToSave);
		
		submitter.getValueService().save(rawDataElementValue);
	}

	public static class ElementSubmitter {
		private FormElementService formElementService;
		private ValueService valueService;
		
		public ElementSubmitter(FormElementService formElementService, ValueService valueService) {
			this.formElementService = formElementService;
			this.valueService = valueService;
		}
		
		public boolean transformValue(Value currentValue, Type currentType, String currentPrefix) {
			// if it is skipped, we return null
			if (currentValue.getAttribute("skipped") != null){
				currentValue.setJsonValue(Value.NULL_INSTANCE().getJsonValue());
			}
			
			// if it is not skipped, we keep these attributes
//			currentValue.setAttribute("invalid", null);
//			currentValue.setAttribute("warning", null); //for when the user overrides an outlier value
			
			return true;
		}	
		
		public ValueService getValueService() {
			return valueService;
		}
		
		public FormElementService getFormElementService() {
			return formElementService;
		}
	}
	
	public static class ElementCalculator {
		private FormValidationService formValidationService;
		private FormElementService formElementService;
		private ValidatableLocator validatableLocator;
		private List<FormEnteredValue> affectedValues;
		
		public ElementCalculator(List affectedValues, FormValidationService formValidationService, FormElementService formElementService, ValidatableLocator validatableLocator) {
			this.formElementService = formElementService;
			this.formValidationService = formValidationService;
			this.validatableLocator = validatableLocator;
			this.affectedValues = affectedValues;
		}
		
		public List<FormEnteredValue> getAffectedValues() {
			return affectedValues;
		}
		
		public ValidatableLocator getValidatableLocator() {
			return validatableLocator;
		}
		
		public FormElementService getFormElementService() {
			return formElementService;
		}
		
		public FormValidationService getFormValidationService() {
			return formValidationService;
		}
		
		public void addAffectedValue(FormEnteredValue value) {
			this.affectedValues.add(value);
		}
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this.is(obj))
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof FormElement))
			return false;
		FormElement other = (FormElement) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	public String toString(){
		return "FormElement[getId()=" + getId() + ", getDataElement()=" + getDataElement() + "]";
	}

	@Override
	public String toExportString() {
		return "[" + Utils.formatExportCode(getCode()) + ", " + getDataElement().toExportString() + "]";
	}
}