package org.chai.kevin.survey;

import org.chai.kevin.form.FormCloner;

public abstract class SurveyCloner extends FormCloner {

	public Survey getSurvey(Survey survey) {return survey;}

	public SurveyProgram getProgram(SurveyProgram program) {return program;}

	public SurveySection getSection(SurveySection section) {return section;}

	public SurveyQuestion getQuestion(SurveyQuestion question) {return question;}

}