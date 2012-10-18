package org.chai.task

import org.chai.task.Task.TaskStatus;
import org.chai.kevin.data.Calculation;
import org.chai.kevin.data.Data;
import org.chai.kevin.data.NormalizedDataElement;
import org.chai.kevin.location.Location;
import org.chai.kevin.security.User;
import org.chai.kevin.survey.Survey;
import org.chai.kevin.survey.SurveyProgram;
import org.chai.kevin.survey.SurveySection;

class RefreshSurveyTask extends Task {

	def surveyPageService
	
	Boolean closeIfComplete
	Boolean reset
	Integer locationId
	Integer surveyId
	Integer programId
	Integer sectionId
	
	public RefreshSurveyTask() {
		super();
	}
	
	def executeTask() {
		Task.withTransaction {
			def survey = Survey.get(surveyId)
			def program = SurveyProgram.get(programId)
			def section = SurveySection.get(sectionId)
			def location = Location.get(locationId)
			
			if(location != null && survey != null){
				if(section != null && program != null){
					surveyPageService.refresh(
						location, survey, program, section,
						closeIfComplete==null?false:closeIfComplete,
						reset==null?false:reset,
						this
					);
				}
				else if(program != null){
					surveyPageService.refresh(
						location, survey, program,
						closeIfComplete==null?false:closeIfComplete,
						reset==null?false:reset,
						this
					);
				}
				else {
					surveyPageService.refresh(
						location, survey,
						closeIfComplete==null?false:closeIfComplete,
						reset==null?false:reset,
						this
					);
				}
				
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
