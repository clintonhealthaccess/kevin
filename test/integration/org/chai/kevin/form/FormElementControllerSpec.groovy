package org.chai.kevin.form

import org.chai.kevin.data.Type;
import org.chai.kevin.survey.SurveyIntegrationTests;
import org.chai.kevin.util.JSONUtils;

class FormElementControllerSpec extends SurveyIntegrationTests {

	def formElementController
	def languageService
	
	def "test get html data"() {
		setup:
		def period = newPeriod()
		
		def survey = newSurvey(CODE(1), period)
		def program = newSurveyProgram(CODE(1), survey, 1, [(HEALTH_CENTER_GROUP)])
		def section = newSurveySection(CODE(1), program, 1, [(HEALTH_CENTER_GROUP)])
		def question = newSimpleQuestion(CODE(1), section, 1, [(HEALTH_CENTER_GROUP)])
		
		def dataElement1 = newRawDataElement(j(["en": "element"]), CODE(1), Type.TYPE_NUMBER())
		def dataElement2 = newRawDataElement(j(["en": "test"]), CODE(2), Type.TYPE_NUMBER())
		newSurveyElement(question, dataElement1)
		newSurveyElement(question, dataElement2)
		
		formElementController = new FormElementController()
		
		when:
		formElementController.params.searchText = 'ele'
		formElementController.getHtmlData()
		
		then:
		formElementController.response.contentAsString.contains('element')
		!formElementController.response.contentAsString.contains('test')
		
	}
	
	def "test get ajax data"() {
		setup:
		def period = newPeriod()
		
		def survey = newSurvey(CODE(1), period)
		def program = newSurveyProgram(CODE(1), survey, 1, [(HEALTH_CENTER_GROUP)])
		def section = newSurveySection(CODE(1), program, 1, [(HEALTH_CENTER_GROUP)])
		def question = newSimpleQuestion(CODE(1), section, 1, [(HEALTH_CENTER_GROUP)])
		
		def dataElement = newRawDataElement(j(["en": "element"]),CODE(1), Type.TYPE_NUMBER())
		def element = newSurveyElement(question, dataElement)
		
		formElementController = new FormElementController()
		
		when:
		formElementController.params.searchText = 'ele'
		formElementController.getAjaxData()
		def jsonResult = JSONUtils.getMapFromJSON(formElementController.response.contentAsString)
		
		then:
		jsonResult.elements.size() == 1
		jsonResult.elements[0].key == element.id
		jsonResult.elements[0].value == element.getLabel(languageService)+'['+element.id+']'
	}
	
	def "test get description for form element"() {
		setup:
		def period = newPeriod()
		
		def dataElement = newRawDataElement(j(["en": "element"]),CODE(1), Type.TYPE_NUMBER())
		def element = newFormElement(dataElement)
		
		formElementController = new FormElementController()
		
		when:
		formElementController.params.id = element.id
		formElementController.getDescription()
		def jsonResult = JSONUtils.getMapFromJSON(formElementController.response.contentAsString)
		
		then:
		jsonResult.result == 'success'
		jsonResult.html.contains('element')
	}
	
	def "test get description for survey element"() {
		setup:
		def period = newPeriod()
		
		def survey = newSurvey(CODE(1), period)
		def program = newSurveyProgram(CODE(1), survey, 1, [(HEALTH_CENTER_GROUP)])
		def section = newSurveySection(CODE(1), program, 1, [(HEALTH_CENTER_GROUP)])
		def question = newSimpleQuestion(CODE(1), section, 1, [(HEALTH_CENTER_GROUP)])
		
		def dataElement = newRawDataElement(j(["en": "element"]),CODE(1), Type.TYPE_NUMBER())
		def element = newSurveyElement(question, dataElement)
		
		formElementController = new FormElementController()
		
		when:
		formElementController.params.id = element.id
		formElementController.getDescription()
		def jsonResult = JSONUtils.getMapFromJSON(formElementController.response.contentAsString)
		
		then:
		jsonResult.result == 'success'
		jsonResult.html.contains('element')
	}
	
}
