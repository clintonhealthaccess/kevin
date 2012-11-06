package org.chai.kevin.form;

import groovy.transform.EqualsAndHashCode;
import i18nfields.I18nFields;

import java.util.Set

import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.chai.kevin.form.FormElement.ElementCalculator
import org.chai.kevin.util.Utils
import org.chai.location.DataLocation

@I18nFields
@EqualsAndHashCode(includes='code')
class FormValidationRule {

	private final static Log log = LogFactory.getLog(FormValidationRule.class);
	
	Long id;
	String code;
	
	String prefix = "";
	
	String expression;
	Boolean allowOutlier;

	String typeCodeString;
	
	String messages 
	
	static i18nFields = ['messages']
		
	FormElement formElement
	static belongsTo = [formElement: FormElement]
	
	static hasMany = [validationRuleDependencies: FormValidationRuleDependency]
	
	static mapping = {
		table 'dhsst_form_validation_rule'
		code unique: true
		formElement column: 'formElement'
		validationRuleDependencies cascade: "all-delete-orphan"
	}
	
	static constraints = {
		code (nullable: false, blank: false, unique: true)
		typeCodeString (nullable:false /*, blank:false*/)
		expression (nullable: false)
		prefix (nullable: false, blank: true)
		allowOutlier(nullable: false)
		messages (nullable: true)
	}
	
	List<FormElement> getDependencies() {
		def result = []
		validationRuleDependencies?.each {result.add (it.formElement)}
		return result
	}
	
	void setDependencies(List<FormElement> dependencies) {
		validationRuleDependencies?.clear()
		dependencies.each {
			addToValidationRuleDependencies(new FormValidationRuleDependency(formElement: it))
		}
	}
	
	public Set<String> getTypeCodes() {
		return Utils.split(typeCodeString, Utils.DEFAULT_TYPE_CODE_DELIMITER);
	}
	
	public void setTypeCodes(Set<String> typeCodes) {
		this.typeCodeString = Utils.unsplit(typeCodes, Utils.DEFAULT_TYPE_CODE_DELIMITER);
	}
	
	public void deepCopy(FormValidationRule copy, FormCloner cloner) {
		copy.setCode(getCode() + ' clone');
		copy.setAllowOutlier(getAllowOutlier());
		copy.setPrefix(getPrefix());
		copy.setExpression(cloner.getExpression(getExpression(), copy));
		copy.setFormElement(cloner.getElement(getFormElement()));
		Utils.copyI18nField(this, copy, "Messages")
		copy.setTypeCodeString(getTypeCodeString());
		def dependenciesCopy = []
		for (FormElement element : getDependencies()) {
			FormElement newElement = cloner.getElement(element);
			if (newElement.equals(element)) {
				cloner.addUnchangedValidationRule(this, element.getId());
			}
			dependenciesCopy.add(newElement);
		}
		copy.setDependencies(dependenciesCopy)
	}
	
	public void evaluate(DataLocation dataLocation, ElementCalculator calculator) {
		if (log.isDebugEnabled()) log.debug("evaluate(location="+dataLocation+") on "+this);
		Set<String> prefixes = calculator.getFormValidationService().getInvalidPrefix(this, dataLocation, calculator.getValidatableLocator());

		FormEnteredValue enteredValue = calculator.getFormElementService().getOrCreateFormEnteredValue(dataLocation, this.getFormElement());
		enteredValue.getValidatable().setInvalid(this, prefixes);
		
		calculator.addAffectedValue(enteredValue);
	}
	
	public String toString() {
		return "FormValidationRule[getId()=" + getId() + ", getExpression()='" + getExpression() + "']";
	}

//	@Override
//	public String toExportString() {
//		return "[" + Utils.formatExportCode(getCode()) + "]";
//	}
}
