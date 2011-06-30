package org.chai.kevin.survey

import org.chai.kevin.AbstractReportController;

class SurveyStrategicObjectiveController  extends AbstractReportController {
	def organisationService
	
	def getEntity(def id) {
		return SurveyStrategicObjective.get(id)
	}

	def createEntity() {
		return new SurveyStrategicObjective()
	}

	def getTemplate() {
		return "/survey/createSurveyStrategicObjective"
	}
}
