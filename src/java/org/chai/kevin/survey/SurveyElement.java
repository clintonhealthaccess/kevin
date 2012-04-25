package org.chai.kevin.survey;

import java.util.Map.Entry;
import java.util.List;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.chai.kevin.LanguageService;
import org.chai.kevin.Translation;
import org.chai.kevin.form.FormCloner;
import org.chai.kevin.form.FormElement;
import org.chai.kevin.form.FormElementService;
import org.chai.kevin.form.FormEnteredValue;
import org.chai.kevin.form.FormValidationRule;
import org.chai.kevin.form.FormValidationService;
import org.chai.kevin.form.FormElement.ElementCalculator;
import org.chai.kevin.form.FormValidationService.ValidatableLocator;
import org.chai.kevin.location.DataLocation;
import org.chai.kevin.survey.validation.SurveyEnteredQuestion;
import org.chai.kevin.value.Value;
import org.chai.kevin.value.ValueService;

@Entity(name = "SurveyElement")
@Table(name = "dhsst_survey_element")
public class SurveyElement extends FormElement {

	private SurveyQuestion surveyQuestion;
	
	@ManyToOne(targetEntity=SurveyQuestion.class, fetch=FetchType.LAZY)
	@JoinColumn(nullable=false)
	public SurveyQuestion getSurveyQuestion() {
		return surveyQuestion;
	}
	
	public void setSurveyQuestion(SurveyQuestion surveyQuestion) {
		this.surveyQuestion = surveyQuestion;
	}
	
	@Transient
	public String getDescriptionTemplate() {
		return "/survey/admin/surveyElementDescription";
	}
	
	@Transient
	@Override
	public String getLabel(LanguageService languageService) {
		return languageService.getText(getDataElement().getNames()) 
			+ " - " + languageService.getText(getSurveyQuestion().getSection().getNames())
			+ " - " + languageService.getText(getSurvey().getNames());
	}
	
	@Transient
	public Survey getSurvey() {
		return surveyQuestion.getSurvey();
	}

	@Transient
	public Set<String> getTypeApplicable(){
		return this.surveyQuestion.getTypeApplicable(this);
	}

	@Transient
	@Override
	public void deepCopy(FormElement copy, FormCloner cloner) {
		super.deepCopy(copy, cloner);
		((SurveyElement)copy).setSurveyQuestion(((SurveyCloner)cloner).getQuestion(getSurveyQuestion()));
	}
	
	@Transient
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
		
		public SurveyElementCalculator(List<FormEnteredValue> affectedValues, List<SurveyEnteredQuestion> affectedQuestions, FormValidationService formValidationService, FormElementService formElementService, SurveyValueService surveyValueService, ValidatableLocator validatableLocator) {
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
}
