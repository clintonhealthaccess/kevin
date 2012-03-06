package org.chai.kevin.survey

import org.chai.kevin.IntegrationTests;

class ProgramControllerSpec extends SurveyIntegrationTests {

	def programController
	
	def "program list 404 when no survey"() {
		setup:
		programController = new ProgramController()
		
		when:
		programController.list()
		
		then:
		programController.modelAndView == null
	}
	
	def "program list"() {
		setup:
		def period = newPeriod()
		def survey = newSurvey(period)
		def program = newSurveyProgram(survey, 1, [])
		programController = new ProgramController()
		
		when:
		programController.params['survey.id'] = survey.id
		programController.list()
		
		then:
		programController.modelAndView.model.entities.equals([program])
	}
	
}
