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
		def dataElement = newDataElement(CODE(1), Type.TYPE_NUMBER())
		def element = newSurveyElement(question, dataElement)
		questionController = new QuestionController()
		
		when:
		questionController.params.surveyId = survey.id
		questionController.params.term = 'ele'
		questionController.getAjaxData()
		
		then:
		questionController.response.contentAsString.contains('element')
		
	}
	
}
