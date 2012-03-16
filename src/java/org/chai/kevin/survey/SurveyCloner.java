package org.chai.kevin.survey;

import org.chai.kevin.form.FormElement;
import org.chai.kevin.form.FormSkipRule;
import org.chai.kevin.form.FormValidationRule;


public abstract class SurveyCloner {

	public void addUnchangedValidationRule(FormValidationRule rule, Long id){}

	public void addUnchangedSkipRule(SurveySkipRule rule, Long id){}

	public String getExpression(String expression, FormValidationRule rule) {return expression;}

	public String getExpression(String expression, FormSkipRule rule) {return expression;}

	public Survey getSurvey(Survey survey) {return survey;}

	public SurveyProgram getProgram(SurveyProgram program) {return program;}

	public SurveySection getSection(SurveySection section) {return section;}

	public SurveyQuestion getQuestion(SurveyQuestion question) {return question;}

	public <T extends FormElement> T getElement(T element) {return element;}

	public <T extends FormSkipRule> T getSkipRule(T skipRule) {return skipRule;}

	public <T extends FormValidationRule> T getValidationRule(T validationRule) {return validationRule;}

}