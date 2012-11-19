package org.chai.kevin.survey

import org.chai.kevin.IntegrationTests;
import org.chai.kevin.data.Type;
import org.chai.kevin.form.FormEnteredValue;
import org.chai.kevin.value.Value;
import org.chai.location.DataLocation;

class TableQuestionControllerSpec extends SurveyIntegrationTests {

	def tableQuestionController
	def tableColumnController
	def tableRowController
	
	def "test table preview with enum"() {
		
	}
	
	def "test create table question"() {
		setup:
		def period = newPeriod()
		def survey = newSurvey(CODE(1), period)
		def program = newSurveyProgram(CODE(1), survey, 1, [(HEALTH_CENTER_GROUP)])
		def section = newSurveySection(CODE(1), program, 1, [(HEALTH_CENTER_GROUP)])
		tableQuestionController = new TableQuestionController()
		
		when:
		tableQuestionController.params['code'] = 'code'
		tableQuestionController.params['section.id'] = section.id
		tableQuestionController.params['order'] = 1
		tableQuestionController.params['typeCodes'] = ['']
		tableQuestionController.saveWithoutTokenCheck()
		
		then:
		SurveyTableQuestion.count() == 1
		SurveyTableQuestion.list()[0].section == section
	}
	
	def "test create table column"() {
		setup:
		def period = newPeriod()
		def survey = newSurvey(CODE(1), period)
		def program = newSurveyProgram(CODE(1), survey, 1, [(HEALTH_CENTER_GROUP)])
		def section = newSurveySection(CODE(1), program, 1, [(HEALTH_CENTER_GROUP)])
		def question = newTableQuestion(CODE(1), section, 1, [(HEALTH_CENTER_GROUP)])
		tableColumnController = new TableColumnController()
		
		when:
		tableColumnController.params['code'] = 'code'
		tableColumnController.params['question.id'] = question.id
		tableColumnController.params['order'] = 1
		tableColumnController.params['typeCodes'] = ['']
		tableColumnController.saveWithoutTokenCheck()
		
		then:
		SurveyTableColumn.count() == 1
		SurveyTableColumn.list()[0].question == question
	}
	
	def "test create table row"() {
		setup:
		def period = newPeriod()
		def survey = newSurvey(CODE(1), period)
		def program = newSurveyProgram(CODE(1), survey, 1, [(HEALTH_CENTER_GROUP)])
		def section = newSurveySection(CODE(1), program, 1, [(HEALTH_CENTER_GROUP)])
		def question = newTableQuestion(CODE(1), section, 1, [(HEALTH_CENTER_GROUP)])
		def dataElement = newRawDataElement(CODE(1), Type.TYPE_BOOL())
		def column = newTableColumn(CODE(1), question, 1, [(HEALTH_CENTER_GROUP)])
		tableRowController = new TableRowController()
		
		when:
		tableRowController.params['code'] = 'code'
		tableRowController.params['order'] = 1
		tableRowController.params['question.id'] = question.id
		tableRowController.params['surveyElement['+column.id+'].dataElement.id'] = dataElement.id
		tableRowController.params['surveyElement'] = column.id
		tableRowController.params['typeCodes'] = ['']
		tableRowController.saveWithoutTokenCheck()
		
		then:
		SurveyTableRow.count() == 1
		SurveyTableRow.list()[0].surveyElements[column].dataElement == dataElement
	}
	
	def "test edit table row"() {
		setup:
		def period = newPeriod()
		def survey = newSurvey(CODE(1), period)
		def program = newSurveyProgram(CODE(1), survey, 1, [(HEALTH_CENTER_GROUP)])
		def section = newSurveySection(CODE(1), program, 1, [(HEALTH_CENTER_GROUP)])
		def question = newTableQuestion(CODE(1), section, 1, [(HEALTH_CENTER_GROUP)])
		def dataElement = newRawDataElement(CODE(1), Type.TYPE_BOOL())
		def dataElement2 = newRawDataElement(CODE(2), Type.TYPE_BOOL())
		def column = newTableColumn(CODE(1), question, 1, [(HEALTH_CENTER_GROUP)])
		def element = newSurveyElement(question, dataElement)
		def row = newTableRow(CODE(1), question, 1, [(HEALTH_CENTER_GROUP)], [(column): element])
		tableRowController = new TableRowController()
		
		when:
		tableRowController.params['id'] = row.id
		tableRowController.params['code'] = 'code'
		tableRowController.params['order'] = 1
		tableRowController.params['question.id'] = question.id
		tableRowController.params['surveyElement['+column.id+'].dataElement.id'] = dataElement2.id
		tableRowController.params['surveyElement['+column.id+'].id'] = element.id
		tableRowController.params['surveyElement'] = column.id
		tableRowController.params['typeCodes'] = ['']
		tableRowController.saveWithoutTokenCheck()
		
		then:
		SurveyTableRow.count() == 1
		SurveyTableRow.list()[0].surveyElements[column].dataElement == dataElement2
	}
	
	def "test delete table row"() {
		setup:
		setupLocationTree()
		def period = newPeriod()
		def survey = newSurvey(CODE(1), period)
		def program = newSurveyProgram(CODE(1), survey, 1, [(HEALTH_CENTER_GROUP)])
		def section = newSurveySection(CODE(1), program, 1, [(HEALTH_CENTER_GROUP)])
		def question = newTableQuestion(CODE(1), section, 1, [(HEALTH_CENTER_GROUP)])
		def dataElement = newRawDataElement(CODE(1), Type.TYPE_BOOL())
		def column = newTableColumn(CODE(1), question, 1, [(HEALTH_CENTER_GROUP)])
		def element = newSurveyElement(question, dataElement)
		def row = newTableRow(CODE(1), question, 1, [(HEALTH_CENTER_GROUP)], [(column): element])
		newFormEnteredValue(element, period, DataLocation.findByCode(BUTARO), Value.VALUE_NUMBER(1))
		tableRowController = new TableRowController()
		
		when:
		tableRowController.params['id'] = row.id
		tableRowController.delete()
		
		then:
		SurveyTableColumn.count() == 1
		SurveyTableRow.count() == 0
		SurveyElement.count() == 0
		FormEnteredValue.count() == 0
	}
	
	def "test delete table column without other rows"() {
		setup:
		setupLocationTree()
		def period = newPeriod()
		def survey = newSurvey(CODE(1), period)
		def program = newSurveyProgram(CODE(1), survey, 1, [(HEALTH_CENTER_GROUP)])
		def section = newSurveySection(CODE(1), program, 1, [(HEALTH_CENTER_GROUP)])
		def question = newTableQuestion(CODE(1), section, 1, [(HEALTH_CENTER_GROUP)])
		def dataElement = newRawDataElement(CODE(1), Type.TYPE_BOOL())
		def column = newTableColumn(CODE(1), question, 1, [(HEALTH_CENTER_GROUP)])
		tableColumnController = new TableColumnController()
		
		when:
		tableColumnController.params['id'] = column.id
		tableColumnController.delete()
		
		then:
		SurveyTableColumn.count() == 0
		SurveyElement.count() == 0
		FormEnteredValue.count() == 0
	}
	
	def "test delete table column"() {
		setup:
		setupLocationTree()
		def period = newPeriod()
		def survey = newSurvey(CODE(1), period)
		def program = newSurveyProgram(CODE(1), survey, 1, [(HEALTH_CENTER_GROUP)])
		def section = newSurveySection(CODE(1), program, 1, [(HEALTH_CENTER_GROUP)])
		def question = newTableQuestion(CODE(1), section, 1, [(HEALTH_CENTER_GROUP)])
		def dataElement = newRawDataElement(CODE(1), Type.TYPE_BOOL())
		def column = newTableColumn(CODE(1), question, 1, [(HEALTH_CENTER_GROUP)])
		def element = newSurveyElement(question, dataElement)
		def row = newTableRow(CODE(1), question, 1, [(HEALTH_CENTER_GROUP)], [(column): element])
		newFormEnteredValue(element, period, DataLocation.findByCode(BUTARO), Value.VALUE_NUMBER(1))
		tableColumnController = new TableColumnController()
		
		when:
		tableColumnController.params['id'] = column.id
		tableColumnController.delete()
		
		then:
		SurveyTableRow.count() == 1
		SurveyTableColumn.count() == 0
		SurveyElement.count() == 0
		FormEnteredValue.count() == 0
	}
	
}
