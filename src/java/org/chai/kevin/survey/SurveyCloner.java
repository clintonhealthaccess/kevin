package org.chai.kevin.survey;


abstract class SurveyCloner {

	public void addUnchangedValidationRule(SurveyValidationRule rule, Long id){}

	public void addUnchangedSkipRule(SurveySkipRule rule, Long id){}

	public String getExpression(String expression, SurveyValidationRule rule) {return expression;}

	public String getExpression(String expression, SurveySkipRule rule) {return expression;}

	public Survey getSurvey(Survey survey) {return survey;}

	public SurveyProgram getProgram(SurveyProgram program) {return program;}

	public SurveySection getSection(SurveySection section) {return section;}

	public SurveyQuestion getQuestion(SurveyQuestion question) {return question;}

	public SurveyElement getElement(SurveyElement element) {return element;}

	public SurveySkipRule getSkipRule(SurveySkipRule skipRule) {return skipRule;}

	public SurveyValidationRule getValidationRule(SurveyValidationRule validationRule) {return validationRule;}

}