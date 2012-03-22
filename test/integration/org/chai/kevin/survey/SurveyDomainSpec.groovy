package org.chai.kevin.survey

import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.chai.kevin.IntegrationTests
import org.chai.kevin.data.RawDataElement
import org.chai.kevin.data.Enum
import org.chai.kevin.data.EnumOption
import org.chai.kevin.data.Type;
import org.chai.kevin.form.FormValidationRule;
import org.chai.kevin.location.DataEntityType;
import org.chai.kevin.location.LocationEntity;
import org.chai.kevin.survey.validation.SurveyLog;
import org.hisp.dhis.period.Period
import org.chai.kevin.location.DataLocationEntity;


class SurveyDomainSpec extends SurveyIntegrationTests {

	private static final Log log = LogFactory.getLog(SurveyDomainSpec.class)

	
	def "table question has data elements"() {
		setup:
		def period = newPeriod()
		def survey = newSurvey(period)
		def program = newSurveyProgram(survey, 1, [])
		def section = newSurveySection(program, 1, [])
		def question = newTableQuestion(section, 1, [])
		def dataElement = newRawDataElement(CODE(1), Type.TYPE_NUMBER())
		def element = newSurveyElement(question, dataElement)
		def column = newTableColumn(question, 1, [])
		def row = newTableRow(question, 1, [], [(column): element])
		
		when:
		def questionToTest = SurveyTableQuestion.list()[0]
		
		then:
		questionToTest.surveyElements.size() == 1
		questionToTest.surveyElements[0].equals(element)
	}

	def "save survey cascades skiprule"() {
		setup:
		def period = newPeriod()
		def survey = newSurvey(period)
		def program = newSurveyProgram(survey, 1, [])
		def section = newSurveySection(program, 1, [])
		def question = newSimpleQuestion(section, 1, [])
		def dataElement = newRawDataElement(CODE(1), Type.TYPE_NUMBER())
		def element = newSurveyElement(question, dataElement)
		
		when:
		def skipRule = new SurveySkipRule(survey: survey, expression: "\$"+element.id+" == 1", skippedFormElements: [:], skippedSurveyQuestions: [])
		
		then:
		skipRule.id == null
		
		when:
		survey.addSkipRule(skipRule)
		survey.save(failOnError: true, flush: true)
		
		then:
		skipRule.id != null
	}
	
	
	def "test get sruvey elements on questions without elements"() {
		when:
		setupLocationTree()
		def period = newPeriod()
		def survey = newSurvey(period)
		def program = newSurveyProgram(survey, 1, [(DISTRICT_HOSPITAL_GROUP)])
		def section = newSurveySection(program, 1, [(DISTRICT_HOSPITAL_GROUP)])
		def question1 = newTableQuestion(section, 1, [(DISTRICT_HOSPITAL_GROUP)])
		def column = newTableColumn(question1, 1, [(DISTRICT_HOSPITAL_GROUP)])
		def row = newTableRow(question1, 1, [(DISTRICT_HOSPITAL_GROUP)], [(column): null])

		def question2 = newSimpleQuestion(section, 1, [(DISTRICT_HOSPITAL_GROUP)])

		def question3 = newCheckboxQuestion(section, 1, [(DISTRICT_HOSPITAL_GROUP)])
		def option = newCheckboxOption(question3, 1, [(DISTRICT_HOSPITAL_GROUP)], null)
		
		then:
		question1.getSurveyElements().equals([])
		question2.getSurveyElements().equals([])
		question3.getSurveyElements().equals([])
		
		question1.getSurveyElements(DataEntityType.findByCode(DISTRICT_HOSPITAL_GROUP)).equals([])
		question2.getSurveyElements(DataEntityType.findByCode(DISTRICT_HOSPITAL_GROUP)).equals([])
		question3.getSurveyElements(DataEntityType.findByCode(DISTRICT_HOSPITAL_GROUP)).equals([])
	}
	
	def "test question table number of location unit applicable"(){
		
		setup:
		def period = newPeriod()
		def survey = newSurvey(period)
		def program = newSurveyProgram(survey, 1, [(DISTRICT_HOSPITAL_GROUP)])
		def section = newSurveySection(program, 1, [(DISTRICT_HOSPITAL_GROUP)])
		def question = newTableQuestion(section, 1, [(DISTRICT_HOSPITAL_GROUP)])
		def dataElement = newRawDataElement(CODE(1), Type.TYPE_NUMBER())
		def element = newSurveyElement(question, dataElement)
		def column = newTableColumn(question, 1, [(DISTRICT_HOSPITAL_GROUP)])
		def row = newTableRow(question, 1, [(DISTRICT_HOSPITAL_GROUP)], [(column): element])
		
		when:
		def orgunitgroupList = question.getTypeApplicable(element)
		
		then:
		orgunitgroupList.size() == 1
	}

	def "survey elements can have 2 validation rules with the same prefix"() {
		setup:
		def period = newPeriod()
		def survey = newSurvey(period)
		def program = newSurveyProgram(survey, 1, [(DISTRICT_HOSPITAL_GROUP)])
		def section = newSurveySection(program, 1, [(DISTRICT_HOSPITAL_GROUP)])
		def question = newTableQuestion(section, 1, [(DISTRICT_HOSPITAL_GROUP)])
		def dataElement = newRawDataElement(CODE(1), Type.TYPE_NUMBER())
		def element = newSurveyElement(question, dataElement)
		
		when:
		newFormValidationRule(element, "", [], "true");
		newFormValidationRule(element, "", [], "true");
		
		then:
		SurveyElement.list()[0].validationRules.size() == 2
		FormValidationRule.count() == 2
	}
	
	def "save surveylog"() {
		setup:
		setupLocationTree()
		def period = newPeriod()
		def survey = newSurvey(period)
		newSurveyProgram(survey, 2, [(DISTRICT_HOSPITAL_GROUP)])
		def program = newSurveyProgram(survey, 1, [(DISTRICT_HOSPITAL_GROUP)])
		def type = newDataEntityType(HEALTH_CENTER_GROUP)
		
		
		when:
		new SurveyLog(event: "test", entity: DataLocationEntity.findByCode(KIVUYE), timestamp: new Date(), survey: survey, program: program).save(failOnError: true)		
		then:
		SurveyLog.count() == 1
	}
	
}
