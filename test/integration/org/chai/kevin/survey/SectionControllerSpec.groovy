package org.chai.kevin.survey

import org.chai.kevin.IntegrationTests;

class SectionControllerSpec extends SurveyIntegrationTests {

	def sectionController
	
	def "section list 404 when no survey"() {
		setup:
		sectionController = new SectionController()
		
		when:
		sectionController.list()
		
		then:
		sectionController.modelAndView == null
	}
	
	def "section list"() {
		setup:
		def period = newPeriod()
		def survey = newSurvey(period)
		def objective = newSurveyObjective(survey, 1, [])
		def section = newSurveySection(objective, 1, [])
		sectionController = new SectionController()
		
		when:
		sectionController.params['objective.id'] = objective.id
		sectionController.list()
		
		then:
		sectionController.modelAndView.model.entities.equals([section])
	}
	
}
