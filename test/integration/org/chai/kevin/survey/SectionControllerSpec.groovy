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
		def survey = newSurvey(CODE(1), period)
		def program = newSurveyProgram(CODE(1), survey, 1, [])
		def section = newSurveySection(CODE(1), program, 1, [])
		sectionController = new SectionController()
		
		when:
		sectionController.params['program.id'] = program.id
		sectionController.list()
		
		then:
		sectionController.modelAndView.model.entities.equals([section])
	}
	
}
