package org.chai.kevin.survey

import org.chai.kevin.data.Type;

class SimpleQuestionControllerSpec extends SurveyIntegrationTests {

	def simpleQuestionController
	
	def "create question works"() {
		setup:
		def period = newPeriod()
		def survey = newSurvey(CODE(1), period)
		def program = newSurveyProgram(CODE(1), survey, 1, [(HEALTH_CENTER_GROUP)])
		def section = newSurveySection(CODE(1), program, 1, [(HEALTH_CENTER_GROUP)])
		def dataElement = newRawDataElement(CODE(1), Type.TYPE_LIST(Type.TYPE_MAP(["key":Type.TYPE_NUMBER()])))
		simpleQuestionController = new SimpleQuestionController()
		
		when:
		simpleQuestionController.params['code'] = code
		simpleQuestionController.params['section.id'] = section.id
		simpleQuestionController.params['surveyElement.dataElement.id'] = dataElement.id
		simpleQuestionController.params['order'] = 1
		simpleQuestionController.params['typeCodes'] = ['']
		simpleQuestionController.params['headerList'] = ['[_].key']
		simpleQuestionController.params['headerList[[_].key].en'] = "Header"
		simpleQuestionController.params['headerList[[_].key]'] = ["en": "Header"] // this is not used
		simpleQuestionController.saveWithoutTokenCheck()
		
		then:
		SurveySimpleQuestion.count() == 1
		SurveySimpleQuestion.list()[0].surveyElement.headers['[_].key'].en == "Header"
		SurveySimpleQuestion.list()[0].section == section
		SurveySimpleQuestion.list()[0].surveyElement.dataElement == dataElement
		
	}
	
}
