package org.chai.kevin.form;

public abstract class FormCloner {

	public void addUnchangedValidationRule(FormValidationRule rule, Long id){}

	public void addUnchangedSkipRule(FormSkipRule rule, Long id){}

	public String getExpression(String expression, FormValidationRule rule) {return expression;}

	public String getExpression(String expression, FormSkipRule rule) {return expression;}

	public <T extends FormElement> T getElement(T element) {return element;}

	public <T extends FormSkipRule> T getSkipRule(T skipRule) {return skipRule;}

	public <T extends FormValidationRule> T getValidationRule(T validationRule) {return validationRule;}

}