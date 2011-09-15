package org.chai.kevin.survey;

import org.chai.kevin.survey.validation.SurveyEnteredObjective;

public class ObjectiveSummary implements Comparable<ObjectiveSummary> {

	private SurveyObjective objective;
	private SurveyEnteredObjective enteredObjective;
	
	private int questions;
	private int completedQuestions;
	
	public ObjectiveSummary(SurveyObjective objective,
			SurveyEnteredObjective enteredObjective, Integer questions,
			Integer completedQuestions) {
		super();
		this.objective = objective;
		this.enteredObjective = enteredObjective;
		this.questions = questions;
		this.completedQuestions = completedQuestions;
	}

	public SurveyObjective getObjective() {
		return objective;
	}
	
	public SurveyEnteredObjective getEnteredObjective() {
		return enteredObjective;
	}
	
	public Integer getQuestions() {
		return questions;
	}
	
	public Integer getCompletedQuestions() {
		return completedQuestions;
	}

	@Override
	public int compareTo(ObjectiveSummary o) {
		if (questions == 0 && o.getQuestions() == 0) return 0;
		if (questions == 0) return -1;
		if (o.getQuestions() == 0) return 1;
		
		double d0 = (double)completedQuestions/(double)questions;
		double d1 = (double)o.getCompletedQuestions()/(double)o.getQuestions();
		
		if (d0 > d1) return 1;
		else if (d1 > d0) return -1;
		return 0;
	}
}
