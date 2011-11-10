package org.chai.kevin.survey;

public class ObjectiveSummary {

	private int objectives;
	private int submittedObjectives;
	
	public ObjectiveSummary(int objectives, int submittedObjectives) {
		super();
		this.objectives = objectives;
		this.submittedObjectives = submittedObjectives;
	}

	public Integer getObjectives() {
		return objectives;
	}
	
	public Integer getSubmittedObjectives() {
		return submittedObjectives;
	}
	
}
