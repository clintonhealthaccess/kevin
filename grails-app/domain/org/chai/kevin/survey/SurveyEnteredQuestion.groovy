package org.chai.kevin.survey;

import java.util.Date;

import org.chai.location.DataLocation;

class SurveyEnteredQuestion {
	
	SurveyQuestion question;
	DataLocation dataLocation;
	
	Boolean complete;
	Boolean invalid;
	
	String userUuid
	Date timestamp
	
	static hasMany = [skippedRules: SurveySkipRule]
	
	static mapping = {
		table 'dhsst_survey_entered_question'
		
//		question index: 'Value_Index', column: 'question'
//		dataLocation index: 'Value_Index', column: 'dataLocation'
		
		skippedRules joinTable: [
			name: 'dhsst_survey_question_skipped',
			key: 'dhsst_survey_entered_question',
			column: 'skippedRules'
		]
	}
	
	static constraints = {
		question (nullable: false, unique: ['question', 'dataLocation'])
		dataLocation (nullable: false)
		complete (nullable: false)
		invalid (nullable: false)
		
		userUuid (nullable: true)
		timestamp (nullable: true)
	}
	
	public SurveyEnteredQuestion() {}
	
	public SurveyEnteredQuestion(SurveyQuestion question, DataLocation dataLocation, Boolean invalid, Boolean complete) {
		super();
		this.question = question;
		this.dataLocation = dataLocation;
		this.complete = complete;
		this.invalid = invalid;
	}

	public boolean isSkipped() {
		return skippedRules != null && !skippedRules.empty;
	}
	
	@Override
	public String toString() {
		return "SurveyEnteredQuestion [complete=" + complete + ", invalid=" + invalid + ", skipped=" + skipped + "]";
	}
	
}
