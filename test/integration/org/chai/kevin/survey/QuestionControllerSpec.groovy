package org.chai.kevin.survey

import org.chai.kevin.IntegrationTests;
import org.chai.kevin.data.Type;

class QuestionControllerSpec extends SurveyIntegrationTests {

	def questionController
	
	def "test ajax data"() {
		setup:
		def period = newPeriod()
		def survey = newSurvey(CODE(1), period)
		def program = newSurveyProgram(CODE(1), survey, 1, [])
		def section = newSurveySection(CODE(1), program, 1, [])
		def question = newSimpleQuestion(CODE(1), ['en':'element'], section, 1, [])
		def dataElement = newRawDataElement(CODE(1), Type.TYPE_NUMBER())
		def element = newSurveyElement(question, dataElement)
		questionController = new QuestionController()
		
		when:
		questionController.params.survey = survey.id
		questionController.params.term = 'ele'
		questionController.getAjaxData()
		
		then:
		questionController.response.contentAsString.contains('value')
	}
	
	def "list when no program 404"() {
		setup:
		questionController = new QuestionController()
		
		when:
		questionController.list()
		
		then:
		questionController.modelAndView == null
	}
	
	def "question list"() {
		setup:
		def period = newPeriod()
		def survey = newSurvey(CODE(1), period)
		def program = newSurveyProgram(CODE(1), survey, 1, [])
		def section = newSurveySection(CODE(1), program, 1, [])
		def question = newSimpleQuestion(CODE(1), section, 1, [])
		questionController = new QuestionController()
		
		when:
		questionController.params['section.id'] = section.id
		questionController.list()
		
		then:
		questionController.modelAndView.model.entities.equals([question])
	}
	
	def "question search"() {
		setup:
		def period = newPeriod()
		def survey = newSurvey(CODE(1), period)
		def program = newSurveyProgram(CODE(1), survey, 1, [])
		def section = newSurveySection(CODE(1), program, 1, [])
		def question = newSimpleQuestion(CODE(1), section, 1, [])
		questionController = new QuestionController()
		
		when:
		questionController.params['survey'] = survey.id
		questionController.params['q'] = 'code'
		questionController.search()
		
		then:
		questionController.modelAndView.model.entities.equals([question])
		questionController.modelAndView.model.entityCount == 1
	}
	
}
