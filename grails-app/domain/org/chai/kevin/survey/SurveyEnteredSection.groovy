package org.chai.kevin.survey;

import java.util.Date;

import org.chai.location.DataLocation;

class SurveyEnteredSection {
	
	SurveySection section;
	DataLocation dataLocation;
	Boolean invalid;
	Boolean complete;
	
	Integer totalQuestions;
	Integer completedQuestions;
	
	String userUuid
	Date timestamp
	
	static mapping = {
		table 'dhsst_survey_entered_section'
		
		section column: 'section'
		dataLocation column: 'dataLocation'
	}
	
	static constraints = {
		section (nullable: false, unique: ['section', 'dataLocation'])
		dataLocation (nullable: false)
		complete (nullable: false)
		invalid (nullable: false)
		totalQuestions (nullable: false)
		completedQuestions (nullable: false)
		
		userUuid (nullable: true)
		timestamp (nullable: true)
	}
	
	public SurveyEnteredSection() {}
	
	public SurveyEnteredSection(SurveySection section, DataLocation dataLocation, Boolean invalid, Boolean complete) {
		super();
		this.section = section;
		this.dataLocation = dataLocation;
		this.invalid = invalid;
		this.complete = complete;
		this.totalQuestions = 0
		this.completedQuestions = 0
	}

	public String getDisplayedStatus() {
		String status = null;
		if (invalid) status = "invalid";
		else if (!complete) status = "incomplete";
		else status = "complete";
		return status;
	}
	
	@Override
	public String toString() {
		return "SurveyEnteredSection [invalid=" + invalid + ", complete=" + complete + "]";
	}

}
