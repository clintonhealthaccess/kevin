package org.chai.kevin.survey

import org.chai.kevin.data.Type;
import org.chai.kevin.form.FormElementHeadersMap;
import org.chai.kevin.form.FormEnteredValue;
import org.chai.kevin.value.Value;
import org.chai.location.DataLocation;

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
		simpleQuestionController.params['code'] = 'code'
		simpleQuestionController.params['section.id'] = section.id
		simpleQuestionController.params['surveyElements[0].dataElement.id'] = dataElement.id
		simpleQuestionController.params['order'] = 1
		simpleQuestionController.params['typeCodes'] = ['']
		simpleQuestionController.params['headerList'] = ['[_].key']
		simpleQuestionController.params['headerList[[_].key].en'] = "Header"
		simpleQuestionController.params['headerList[[_].key]'] = ["en": "Header"] // this is not used
		simpleQuestionController.saveWithoutTokenCheck()
		
		then:
		SurveySimpleQuestion.count() == 1
		SurveySimpleQuestion.list()[0].surveyElement.getHeaders('en')['[_].key'] == "Header"
		SurveySimpleQuestion.list()[0].section == section
		SurveySimpleQuestion.list()[0].surveyElement.dataElement == dataElement
	}
	
	def "change question data element"() {
		setup:
		def period = newPeriod()
		def survey = newSurvey(CODE(1), period)
		def program = newSurveyProgram(CODE(1), survey, 1, [(HEALTH_CENTER_GROUP)])
		def section = newSurveySection(CODE(1), program, 1, [(HEALTH_CENTER_GROUP)])
		def question = newSimpleQuestion(CODE(1), section, 1, [(DISTRICT_HOSPITAL_GROUP)])
		def dataElement = newRawDataElement(CODE(1), Type.TYPE_LIST(Type.TYPE_NUMBER()))
		def element = newSurveyElement(question, dataElement, ['[_]':['en': 'test']])
		simpleQuestionController = new SimpleQuestionController()
		
		when:
		def dataElement2 = newRawDataElement(CODE(2), Type.TYPE_LIST(Type.TYPE_NUMBER()))
		simpleQuestionController.params['code'] = 'code'
		simpleQuestionController.params['id'] = question.id
		simpleQuestionController.params['section.id'] = section.id
		simpleQuestionController.params['surveyElements[0].id'] = element.id
		simpleQuestionController.params['surveyElements[0].dataElement.id'] = dataElement2.id
		simpleQuestionController.params['order'] = 1
		simpleQuestionController.params['typeCodes'] = ['']
		simpleQuestionController.saveWithoutTokenCheck()
		
		then:
		SurveySimpleQuestion.count() == 1
		SurveySimpleQuestion.list()[0].section == section
		SurveySimpleQuestion.list()[0].surveyElement.dataElement == dataElement2
		SurveySimpleQuestion.list()[0].surveyElement.getHeaders('en').isEmpty()
		FormElementHeadersMap.count() == 0
	}
	
	def "change question data element header"() {
		setup:
		def period = newPeriod()
		def survey = newSurvey(CODE(1), period)
		def program = newSurveyProgram(CODE(1), survey, 1, [(HEALTH_CENTER_GROUP)])
		def section = newSurveySection(CODE(1), program, 1, [(HEALTH_CENTER_GROUP)])
		def question = newSimpleQuestion(CODE(1), section, 1, [(DISTRICT_HOSPITAL_GROUP)])
		def dataElement = newRawDataElement(CODE(1), Type.TYPE_LIST(Type.TYPE_NUMBER()))
		def element = newSurveyElement(question, dataElement, ['[_]':['en': 'test']])
		simpleQuestionController = new SimpleQuestionController()
		
		when:
		simpleQuestionController.params['code'] = 'code'
		simpleQuestionController.params['id'] = question.id
		simpleQuestionController.params['section.id'] = section.id
		simpleQuestionController.params['surveyElements[0].id'] = element.id
		simpleQuestionController.params['surveyElements[0].dataElement.id'] = dataElement.id
		simpleQuestionController.params['order'] = 1
		simpleQuestionController.params['typeCodes'] = ['']
		simpleQuestionController.params['headerList'] = ['[_]']
		simpleQuestionController.params['headerList[[_]].en'] = "Header"
		simpleQuestionController.saveWithoutTokenCheck()
		
		then:
		SurveySimpleQuestion.count() == 1
		SurveySimpleQuestion.list()[0].section == section
		SurveySimpleQuestion.list()[0].surveyElement.getHeaders('en')['[_]'] == "Header"
		SurveySimpleQuestion.list()[0].surveyElement.dataElement == dataElement
	}
	
	def "delete question deletes entered questions and values"() {
		setup:
		setupLocationTree()
		def period = newPeriod()
		def survey = newSurvey(CODE(1), period)
		def program = newSurveyProgram(CODE(1), survey, 1, [(DISTRICT_HOSPITAL_GROUP)])
		def section = newSurveySection(CODE(1), program, 1, [(DISTRICT_HOSPITAL_GROUP)])
		def question = newSimpleQuestion(CODE(1), section, 1, [(DISTRICT_HOSPITAL_GROUP)])
		def dataElement = newRawDataElement(CODE(1), Type.TYPE_NUMBER())
		def element = newSurveyElement(question, dataElement)
		newFormEnteredValue(element, period, DataLocation.findByCode(BUTARO), Value.VALUE_NUMBER(1))
		newSurveyEnteredQuestion(question, period, DataLocation.findByCode(BUTARO), false, false)
		simpleQuestionController = new SimpleQuestionController()
		
		when:
		simpleQuestionController.params.id = question.id
		simpleQuestionController.delete()
		
		then:
		SurveySimpleQuestion.count() == 0
		SurveyElement.count() == 0
		FormEnteredValue.count() == 0
		SurveyEnteredQuestion.count() == 0
	}
	
}
