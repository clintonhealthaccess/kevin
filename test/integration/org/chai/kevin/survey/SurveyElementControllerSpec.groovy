package org.chai.kevin.survey

import org.chai.kevin.data.Type;

class SurveyElementControllerSpec extends SurveyIntegrationTests {

	def surveyElementController
	
	def "test get html data"() {
		setup:
		def period = newPeriod()
		
		def survey = newSurvey(period)
		def program = newSurveyProgram(survey, 1, [(HEALTH_CENTER_GROUP)])
		def section = newSurveySection(program, 1, [(HEALTH_CENTER_GROUP)])
		def question = newSimpleQuestion(section, 1, [(HEALTH_CENTER_GROUP)])
		
		def dataElement = newRawDataElement(j(["en": "element"]),CODE(1), Type.TYPE_NUMBER())
		def element = newSurveyElement(question, dataElement)
		
		surveyElementController = new SurveyElementController()
		
		when:
		surveyElementController.params.searchText = 'ele'
		surveyElementController.getHtmlData()
		
		then:
		surveyElementController.response.contentAsString.contains('element')
		
	}
	
	def "test get ajax data"() {
		setup:
		def period = newPeriod()
		
		def survey = newSurvey(period)
		def program = newSurveyProgram(survey, 1, [(HEALTH_CENTER_GROUP)])
		def section = newSurveySection(program, 1, [(HEALTH_CENTER_GROUP)])
		def question = newSimpleQuestion(section, 1, [(HEALTH_CENTER_GROUP)])
		
		def dataElement = newRawDataElement(j(["en": "element"]),CODE(1), Type.TYPE_NUMBER())
		def element = newSurveyElement(question, dataElement)
		
		surveyElementController = new SurveyElementController()
		
		when:
		surveyElementController.params.searchText = 'ele'
		surveyElementController.getAjaxData()
		
		then:
		surveyElementController.response.contentAsString.contains('element')
		
	}
	
}
