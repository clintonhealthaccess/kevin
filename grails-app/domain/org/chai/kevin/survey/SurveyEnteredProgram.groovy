package org.chai.kevin.survey;

import java.util.Date;

import org.chai.location.DataLocation;

class SurveyEnteredProgram {

	SurveyProgram program;
	DataLocation dataLocation;
	
	Boolean complete;
	Boolean invalid;
	Boolean closed;
	
	Integer totalQuestions;
	Integer completedQuestions;
	
	String userUuid
	Date timestamp
	
	static mapping = {
		table 'dhsst_survey_entered_program'
		
		program column: 'program'
		dataLocation column: 'dataLocation'
	}
	
	static constraints = {
		program (nullable: false, unique: ['program', 'dataLocation'])
		dataLocation (nullable: false)
		complete (nullable: false)
		invalid (nullable: false)
		closed (nullable: false)
		
		totalQuestions (nullable: false)
		completedQuestions (nullable: false)
		
		userUuid (nullable: true)
		timestamp (nullable: true)
	}
	
	public SurveyEnteredProgram() {}
	
	public SurveyEnteredProgram(SurveyProgram program, DataLocation dataLocation, Boolean invalid, Boolean complete, Boolean closed) {
		super();
		this.program = program;
		this.dataLocation = dataLocation;
		this.complete = complete;
		this.invalid = invalid;
		this.closed = closed;
		this.totalQuestions = 0
		this.completedQuestions = 0
	}

	public String getDisplayedStatus() {
		String status = null;
		if (closed) status = "closed";
		else if (invalid) status = "invalid";
		else if (!complete) status = "incomplete";
		else status = "complete";
		return status;
	}

	@Override
	public String toString() {
		return "SurveyEnteredProgram [complete=" + complete + ", invalid=" + invalid + ", closed=" + closed + "]";
	}
	
}
