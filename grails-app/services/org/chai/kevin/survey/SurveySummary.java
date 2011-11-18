package org.chai.kevin.survey;

import org.chai.kevin.Organisation;

public class SurveySummary implements Comparable<SurveySummary> {
	
	private Organisation organisation;
	
	private int objectives;
	private int submittedObjectives;
	private int questions;
	private int completedQuestions;

	
	public SurveySummary(Organisation organisation, 
			Integer submittedObjectives, Integer objectives,
			Integer completedQuestions, Integer questions) {
		super();
		this.organisation = organisation;
		this.objectives = objectives;
		this.submittedObjectives = submittedObjectives;
		this.questions = questions;
		this.completedQuestions = completedQuestions;
	}

	public Organisation getOrganisation() {
		return organisation;
	}
	
	public Integer getObjectives() {
		return objectives;
	}
	
	public Integer getSubmittedObjectives() {
		return submittedObjectives;
	}
	
	public Integer getQuestions() {
		return questions;
	}
	
	public Integer getCompletedQuestions() {
		return completedQuestions;
	}

	@Override
	public int compareTo(SurveySummary o) {
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