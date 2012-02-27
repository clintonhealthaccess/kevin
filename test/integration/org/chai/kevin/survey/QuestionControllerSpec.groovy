package org.chai.kevin.survey

import org.chai.kevin.IntegrationTests;
import org.chai.kevin.data.Type;

class QuestionControllerSpec extends SurveyIntegrationTests {

	def questionController
	
	def "test ajax data"() {
		setup:
		def period = newPeriod()
		def survey = newSurvey(period)
		def objective = newSurveyObjective(survey, 1, [])
		def section = newSurveySection(objective, 1, [])
		def question = newSimpleQuestion(j(['en':'element']), section, 1, [])
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
	
	def "list when no objective 404"() {
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
		def survey = newSurvey(period)
		def objective = newSurveyObjective(survey, 1, [])
		def section = newSurveySection(objective, 1, [])
		def question = newSimpleQuestion(section, 1, [])
		questionController = new QuestionController()
		
		when:
		questionController.params['section.id'] = section.id
		questionController.list()
		
		then:
		questionController.modelAndView.model.entities.equals([question])
	}
	
}
