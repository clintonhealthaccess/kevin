package org.chai.kevin.survey

import org.chai.kevin.IntegrationTests;

class ObjectiveControllerSpec extends SurveyIntegrationTests {

	def objectiveController
	
	def "objective list 404 when no survey"() {
		setup:
		objectiveController = new ObjectiveController()
		
		when:
		objectiveController.list()
		
		then:
		objectiveController.modelAndView == null
	}
	
	def "objective list"() {
		setup:
		def period = newPeriod()
		def survey = newSurvey(period)
		def objective = newSurveyObjective(survey, 1, [])
		objectiveController = new ObjectiveController()
		
		when:
		objectiveController.params['survey.id'] = survey.id
		objectiveController.list()
		
		then:
		objectiveController.modelAndView.model.entities.equals([objective])
	}
	
}
