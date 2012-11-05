package org.chai.kevin.survey

import org.chai.kevin.data.Type;
import org.chai.kevin.form.FormEnteredValue;
import org.chai.kevin.value.Value;
import org.chai.location.DataLocation;

class CheckboxQuestionControllerSpec extends SurveyIntegrationTests {

	def checkboxQuestionController
	def checkboxOptionController
	
	def "create question works"() {
		setup:
		def period = newPeriod()
		def survey = newSurvey(CODE(1), period)
		def program = newSurveyProgram(CODE(1), survey, 1, [(HEALTH_CENTER_GROUP)])
		def section = newSurveySection(CODE(1), program, 1, [(HEALTH_CENTER_GROUP)])
		checkboxQuestionController = new CheckboxQuestionController()
		
		when:
		checkboxQuestionController.params['code'] = 'code'
		checkboxQuestionController.params['section.id'] = section.id
		checkboxQuestionController.params['order'] = 1
		checkboxQuestionController.params['typeCodes'] = ['']
		checkboxQuestionController.saveWithoutTokenCheck()
		
		then:
		SurveyCheckboxQuestion.count() == 1
	}
	
	def "delete question"() {
		setup:
		def period = newPeriod()
		def survey = newSurvey(CODE(1), period)
		def program = newSurveyProgram(CODE(1), survey, 1, [(DISTRICT_HOSPITAL_GROUP)])
		def section = newSurveySection(CODE(1), program, 1, [(DISTRICT_HOSPITAL_GROUP)])
		def question = newCheckboxQuestion(CODE(1), section, 1, [(DISTRICT_HOSPITAL_GROUP)])
		def dataElement = newRawDataElement(CODE(1), Type.TYPE_BOOL())
		def element = newSurveyElement(question, dataElement).save(flush: true)
		def option = newCheckboxOption(CODE(1), question, 1, [(DISTRICT_HOSPITAL_GROUP)], element)
		checkboxQuestionController = new CheckboxQuestionController()
		
		when:
		checkboxQuestionController.params.id = question.id
		checkboxQuestionController.delete()
		
		then:
		SurveyCheckboxQuestion.count() == 0
		SurveyCheckboxOption.count() == 0
		SurveyElement.count() == 0
	}
	
	def "create checkbox option"() {
		setup:
		def period = newPeriod()
		def survey = newSurvey(CODE(1), period)
		def program = newSurveyProgram(CODE(1), survey, 1, [(DISTRICT_HOSPITAL_GROUP)])
		def section = newSurveySection(CODE(1), program, 1, [(DISTRICT_HOSPITAL_GROUP)])
		def question = newCheckboxQuestion(CODE(1), section, 1, [(DISTRICT_HOSPITAL_GROUP)])
		def dataElement = newRawDataElement(CODE(1), Type.TYPE_BOOL())
		checkboxOptionController = new CheckboxOptionController()
		
		when:
		checkboxOptionController.params['code'] = 'code'
		checkboxOptionController.params['question.id'] = question.id
		checkboxOptionController.params['surveyElement.dataElement.id'] = dataElement.id
		checkboxOptionController.params['order'] = 1
		checkboxOptionController.params['typeCodes'] = ['']
		checkboxOptionController.saveWithoutTokenCheck()
		
		then:
		SurveyCheckboxQuestion.count() == 1
		SurveyCheckboxOption.count() == 1
		SurveyElement.count() == 1
		SurveyElement.list()[0].dataElement.equals(dataElement)
	}
	
	def "change checkbox option data element"() {
		setup:
		def period = newPeriod()
		def survey = newSurvey(CODE(1), period)
		def program = newSurveyProgram(CODE(1), survey, 1, [(DISTRICT_HOSPITAL_GROUP)])
		def section = newSurveySection(CODE(1), program, 1, [(DISTRICT_HOSPITAL_GROUP)])
		def question = newCheckboxQuestion(CODE(1), section, 1, [(DISTRICT_HOSPITAL_GROUP)])
		def dataElement = newRawDataElement(CODE(1), Type.TYPE_BOOL())
		def dataElement2 = newRawDataElement(CODE(2), Type.TYPE_BOOL())
		def element = newSurveyElement(question, dataElement).save(flush: true)
		def option = newCheckboxOption(CODE(1), question, 1, [(DISTRICT_HOSPITAL_GROUP)], element)
		checkboxOptionController = new CheckboxOptionController()
		
		when:
		checkboxOptionController.params['id'] = option.id
		checkboxOptionController.params['code'] = 'code'
		checkboxOptionController.params['question.id'] = question.id
		checkboxOptionController.params['surveyElement.id'] = element.id
		checkboxOptionController.params['surveyElement.dataElement.id'] = dataElement2.id
		checkboxOptionController.params['order'] = 1
		checkboxOptionController.params['typeCodes'] = ['']
		checkboxOptionController.saveWithoutTokenCheck()
		
		then:
		SurveyCheckboxQuestion.count() == 1
		SurveyCheckboxOption.count() == 1
		SurveyElement.count() == 1
		SurveyElement.list()[0].dataElement.equals(dataElement2)
	}
	
	def "delete checkbox option"() {
		setup:
		setupLocationTree()
		def period = newPeriod()
		def survey = newSurvey(CODE(1), period)
		def program = newSurveyProgram(CODE(1), survey, 1, [(DISTRICT_HOSPITAL_GROUP)])
		def section = newSurveySection(CODE(1), program, 1, [(DISTRICT_HOSPITAL_GROUP)])
		def question = newCheckboxQuestion(CODE(1), section, 1, [(DISTRICT_HOSPITAL_GROUP)])
		def dataElement = newRawDataElement(CODE(1), Type.TYPE_BOOL())
		def element = newSurveyElement(question, dataElement)
		def option = newCheckboxOption(CODE(1), question, 1, [(DISTRICT_HOSPITAL_GROUP)], element)
		newFormEnteredValue(element, period, DataLocation.findByCode(BUTARO), Value.VALUE_NUMBER(1))
		checkboxOptionController = new CheckboxOptionController()
		
		when:
		checkboxOptionController.params.id = option.id
		checkboxOptionController.delete()
		
		then:
		SurveyCheckboxQuestion.count() == 1
		SurveyCheckboxOption.count() == 0
		SurveyElement.count() == 0
		FormEnteredValue.count() == 0
	}
	
}
