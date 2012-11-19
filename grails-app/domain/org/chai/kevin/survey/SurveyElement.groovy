package org.chai.kevin.survey;

import java.util.List
import java.util.Set

import org.chai.kevin.Exportable
import org.chai.kevin.form.FormCloner
import org.chai.kevin.form.FormElement
import org.chai.kevin.form.FormElementService
import org.chai.kevin.form.FormValidationService
import org.chai.kevin.form.FormElement.ElementCalculator
import org.chai.kevin.form.FormElement.ElementSubmitter
import org.chai.kevin.form.FormValidationService.ValidatableLocator
import org.chai.kevin.util.Utils
import org.chai.kevin.value.Value
import org.chai.kevin.value.ValueService
import org.chai.location.DataLocation

class SurveyElement extends FormElement implements Exportable {

	static belongsTo = [question: SurveyQuestion]
	
	static mapping = {
		table 'dhsst_survey_element'
		question column: 'surveyQuestion'
	}
	
	public String getDescriptionTemplate() {
		return "/survey/admin/surveyElementDescription";
	}
	
	@Override
	public String getLabel() {
		return getDataElement().getNames() + " - " + getSurveyQuestion().getSection().getNames() + " - " + getSurvey().getNames();
	}
	
	public Survey getSurvey() {
		return surveyQuestion.getSurvey();
	}

	public Set<String> getTypeApplicable(){
		return this.surveyQuestion.getTypeApplicable(this);
	}
	
	SurveyQuestion getSurveyQuestion() {
		return question
	}

	@Override
	public void deepCopy(FormElement copy, FormCloner cloner) {
		super.deepCopy(copy, cloner);
		((SurveyElement)copy).setQuestion(((SurveyCloner)cloner).getQuestion(getQuestion()));
	}
	
	@Override
	public Value getValue(DataLocation dataLocation, ElementSubmitter submitter) {
		SurveyEnteredQuestion enteredQuestion = ((SurveyElementSubmitter)submitter).getSurveyValueService().getOrCreateSurveyEnteredQuestion(dataLocation, this.getSurveyQuestion());
		if (enteredQuestion.isSkipped()) {
			// if the question is skipped we save NULL
			return Value.NULL_INSTANCE();
		}
		else {
			// else we get the value
			return super.getValue(dataLocation, submitter);
		}
	}
	
	public static class SurveyElementSubmitter extends ElementSubmitter {
		
		private SurveyValueService surveyValueService;
	
		public SurveyElementSubmitter(SurveyValueService surveyValueService, FormElementService formElementService, ValueService valueService) {
			super(formElementService, valueService);
			
			this.surveyValueService = surveyValueService;
		}

		public SurveyValueService getSurveyValueService() {
			return surveyValueService;
		}
		
	}
	
	public static class SurveyElementCalculator extends ElementCalculator {

		private SurveyValueService surveyValueService;
		private List<SurveyEnteredQuestion> affectedQuestions;
		
		public SurveyElementCalculator(List affectedValues, List<SurveyEnteredQuestion> affectedQuestions, FormValidationService formValidationService, FormElementService formElementService, SurveyValueService surveyValueService, ValidatableLocator validatableLocator) {
			super(affectedValues, formValidationService, formElementService, validatableLocator);
			this.surveyValueService = surveyValueService;
			this.affectedQuestions = affectedQuestions;
		}
		
		public void addAffectedQuestion(SurveyEnteredQuestion question) {
			this.affectedQuestions.add(question);
		}
		
		public List<SurveyEnteredQuestion> getAffectedQuestions() {
			return affectedQuestions;
		}
		
		public SurveyValueService getSurveyValueService() {
			return surveyValueService;
		}
		
	}

	@Override
	public String toString() {
		return "SurveyElement[getId()=" + getId() + ", getDataElement()=" + getDataElement() + "]";
	}

	@Override
	public String toExportString() {
		return "[" + Utils.formatExportCode(getCode()) + ", " + getDataElement().toExportString() + "]";
	}

}
