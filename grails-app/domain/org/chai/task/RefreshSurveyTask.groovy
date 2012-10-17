package org.chai.task

import org.chai.task.Task.TaskStatus;
import org.chai.kevin.data.Calculation;
import org.chai.kevin.data.Data;
import org.chai.kevin.data.NormalizedDataElement;
import org.chai.location.Location;
import org.chai.kevin.security.User;
import org.chai.kevin.survey.Survey;

class RefreshSurveyTask extends Task {

	def surveyPageService
	
	Boolean closeIfComplete
	Boolean reset
	Integer locationId
	Integer surveyId
	
	public RefreshSurveyTask() {
		super();
	}
	
	def executeTask() {
		Task.withTransaction {
			def survey = Survey.get(surveyId)
			def location = Location.get(locationId)
			
			if (survey != null && location != null) {
				surveyPageService.refresh(
					location, survey, 
					closeIfComplete==null?false:closeIfComplete, 
					reset==null?false:reset, 
					this
				);
			}
		}
	}
	
	String getInformation() {
		return ''
	}
	
	boolean isUnique() {
		return true;
	}
	
	def cleanTask() {
		// nothing to do here
	}
	
	String getOutputFilename() {
		return null
	}
	
	String getFormView() {
		return null
	}
	
	Map getFormModel() {
		return null
	}
	
	static constraints = {
		reset(nullable: true)
		closeIfComplete(nullable: true)
	}
}
