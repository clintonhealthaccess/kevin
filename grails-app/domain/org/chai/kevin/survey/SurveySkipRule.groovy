package org.chai.kevin.survey;

import org.chai.kevin.form.FormCloner;
import org.chai.kevin.form.FormElement.ElementCalculator;
import org.chai.kevin.form.FormSkipRule;
import org.chai.kevin.survey.SurveyElement.SurveyElementCalculator;
import org.chai.location.DataLocation;


class SurveySkipRule extends FormSkipRule {

	static belongsTo = [survey: Survey]
	static hasMany = [skippedSurveyQuestions: SurveyQuestion]
	
	static mapping = {
		table 'dhsst_survey_skip_rule'
		skippedSurveyQuestions joinTable: [
			name: 'dhsst_survey_skipped_survey_questions',
			key: 'dhsst_survey_skip_rule',
			column: 'skippedSurveyQuestions'
		], cascade: 'save-update'
		survey column: 'survey'
	}
	
	@Override
	public void deepCopy(FormSkipRule copy, FormCloner surveyCloner) {
		super.deepCopy(copy, surveyCloner);
		
		SurveySkipRule surveyCopy = (SurveySkipRule)copy;
		surveyCopy.setSurvey(((SurveyCloner)surveyCloner).getSurvey(getSurvey()));
		for (SurveyQuestion question : getSkippedSurveyQuestions()) {
			surveyCopy.addToSkippedSurveyQuestions(((SurveyCloner)surveyCloner).getQuestion(question));
		}
	}
	
	@Override
	public void evaluate(DataLocation dataLocation, ElementCalculator calculator) {
		super.evaluate(dataLocation, calculator);
		
		SurveyElementCalculator surveyCalculator = (SurveyElementCalculator)calculator;
		boolean skipped = surveyCalculator.getFormValidationService().isSkipped(this, dataLocation, calculator.getValidatableLocator());
		for (SurveyQuestion question : getSkippedSurveyQuestions()) {

			SurveyEnteredQuestion enteredQuestion = surveyCalculator.getSurveyValueService().getOrCreateSurveyEnteredQuestion(dataLocation, question);
			if (skipped) enteredQuestion.addToSkippedRules(this);
			else enteredQuestion.removeFromSkippedRules(this);
			
			surveyCalculator.addAffectedQuestion(enteredQuestion);
		}
	}

	@Override
	public String toString() {
		return "SurveySkipRule[getId()=" + getId() + ", getExpression()='" + getExpression() + "']";
	}

//	@Override
//	public String toExportString() {
//		return "[" + Utils.formatExportCode(getCode()) + "]";
//	}
}
