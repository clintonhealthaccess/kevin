package org.chai.kevin.survey

import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.chai.kevin.IntegrationTests
import org.chai.kevin.Period;
import org.chai.kevin.data.RawDataElement
import org.chai.kevin.data.Enum
import org.chai.kevin.data.EnumOption
import org.chai.kevin.data.Type;
import org.chai.kevin.form.FormElement;
import org.chai.kevin.form.FormValidationRule;
import org.chai.location.DataLocationType;
import org.chai.location.Location;
import org.chai.location.DataLocation;
	
class SurveyDomainSpec extends SurveyIntegrationTests {

	private static final Log log = LogFactory.getLog(SurveyDomainSpec.class)

	def "table question has data elements"() {
		setup:
		def period = newPeriod()
		def survey = newSurvey(CODE(1), period)
		def program = newSurveyProgram(CODE(1), survey, 1, [])
		def section = newSurveySection(CODE(1), program, 1, [])
		def question = newTableQuestion(CODE(1), section, 1, [])
		def dataElement = newRawDataElement(CODE(1), Type.TYPE_NUMBER())
		def element = newSurveyElement(question, dataElement)
		def column = newTableColumn(CODE(1), question, 1, [])
		def row = newTableRow(CODE(1), question, 1, [], [(column): element])
		
		when:
		def questionToTest = SurveyTableQuestion.list()[0]
		
		then:
		questionToTest.surveyElements.size() == 1
		questionToTest.allSurveyElements[0].equals(element)
	}

	def "save survey cascades skiprule"() {
		setup:
		def period = newPeriod()
		def survey = newSurvey(CODE(1), period)
		def program = newSurveyProgram(CODE(1), survey, 1, [])
		def section = newSurveySection(CODE(1), program, 1, [])
		def question = newSimpleQuestion(CODE(1), section, 1, [])
		def dataElement = newRawDataElement(CODE(1), Type.TYPE_NUMBER())
		def element = newSurveyElement(question, dataElement)
		
		when:
		def skipRule = new SurveySkipRule(code: CODE(1), survey: survey, expression: "\$"+element.id+" == 1", skippedFormElements: [:], skippedSurveyQuestions: [])
		
		then:
		skipRule.id == null
		
		when:
		survey.addToSkipRules(skipRule)
		survey.save(failOnError: true, flush: true)
		
		then:
		skipRule.id != null
	}
	
	def "test get sruvey elements on questions without elements"() {
		when:
		setupLocationTree()
		def period = newPeriod()
		def survey = newSurvey(CODE(1), period)
		def program = newSurveyProgram(CODE(1), survey, 1, [(DISTRICT_HOSPITAL_GROUP)])
		def section = newSurveySection(CODE(1), program, 1, [(DISTRICT_HOSPITAL_GROUP)])
		def question1 = newTableQuestion(CODE(1), section, 1, [(DISTRICT_HOSPITAL_GROUP)])
		def column = newTableColumn(CODE(1), question1, 1, [(DISTRICT_HOSPITAL_GROUP)])
		def row = newTableRow(CODE(1), question1, 1, [(DISTRICT_HOSPITAL_GROUP)], [(column): null])

		def question2 = newSimpleQuestion(CODE(2), section, 1, [(DISTRICT_HOSPITAL_GROUP)])

		def question3 = newCheckboxQuestion(CODE(3), section, 1, [(DISTRICT_HOSPITAL_GROUP)])
		def option = newCheckboxOption(CODE(1), question3, 1, [(DISTRICT_HOSPITAL_GROUP)], null)
		
		then:
		question1.getSurveyElements() == null
		question2.getSurveyElements() == null
		question3.getSurveyElements() == null
		
		question1.getSurveyElements(DataLocationType.findByCode(DISTRICT_HOSPITAL_GROUP)).equals([])
		question2.getSurveyElements(DataLocationType.findByCode(DISTRICT_HOSPITAL_GROUP)).equals([])
		question3.getSurveyElements(DataLocationType.findByCode(DISTRICT_HOSPITAL_GROUP)).equals([])
	}
	
	def "test question table number of location unit applicable"(){
		
		setup:
		def period = newPeriod()
		def survey = newSurvey(CODE(1), period)
		def program = newSurveyProgram(CODE(1), survey, 1, [(DISTRICT_HOSPITAL_GROUP)])
		def section = newSurveySection(CODE(1), program, 1, [(DISTRICT_HOSPITAL_GROUP)])
		def question = newTableQuestion(CODE(1), section, 1, [(DISTRICT_HOSPITAL_GROUP)])
		def dataElement = newRawDataElement(CODE(1), Type.TYPE_NUMBER())
		def element = newSurveyElement(question, dataElement)
		def column = newTableColumn(CODE(1), question, 1, [(DISTRICT_HOSPITAL_GROUP)])
		def row = newTableRow(CODE(1), question, 1, [(DISTRICT_HOSPITAL_GROUP)], [(column): element])
		
		when:
		def orgunitgroupList = question.getTypeApplicable(element)
		
		then:
		orgunitgroupList.size() == 1
	}

	def "survey elements can have 2 validation rules with the same prefix"() {
		setup:
		def period = newPeriod()
		def survey = newSurvey(CODE(1), period)
		def program = newSurveyProgram(CODE(1), survey, 1, [(DISTRICT_HOSPITAL_GROUP)])
		def section = newSurveySection(CODE(1), program, 1, [(DISTRICT_HOSPITAL_GROUP)])
		def question = newTableQuestion(CODE(1), section, 1, [(DISTRICT_HOSPITAL_GROUP)])
		def dataElement = newRawDataElement(CODE(1), Type.TYPE_NUMBER())
		def element = newSurveyElement(question, dataElement)
		
		when:
		newFormValidationRule(CODE(1), element, "", [], "true");
		newFormValidationRule(CODE(2), element, "", [], "true");
		
		then:
		SurveyElement.list()[0].validationRules.size() == 2
		FormValidationRule.count() == 2
	}
	
	def "delete question deletes survey element and validation rules"() {
		setup:
		setupLocationTree()
		def period = newPeriod()
		def survey = newSurvey(CODE(1), period)
		def program = newSurveyProgram(CODE(1), survey, 1, [(DISTRICT_HOSPITAL_GROUP)])
		def section = newSurveySection(CODE(1), program, 1, [(DISTRICT_HOSPITAL_GROUP)])
		def question = newSimpleQuestion(CODE(2), section, 1, [(DISTRICT_HOSPITAL_GROUP)])
		def element = newSurveyElement(question, newRawDataElement(CODE(1), Type.TYPE_NUMBER()))
		def validationRule = newFormValidationRule(CODE(1), element, '', [(DISTRICT_HOSPITAL_GROUP)], 'true')
		
		when:
		question.delete()
		section.removeFromQuestions(question).save()
		
		then:
		SurveyQuestion.count() == 0
		SurveyElement.count() == 0
		FormElement.count() == 0
		FormValidationRule.count() == 0
		// does not delete raw data element
		RawDataElement.count() == 1
	}
	
	def "delete table question deletes rows and columns"() {
		setup:
		setupLocationTree()
		def period = newPeriod()
		def survey = newSurvey(CODE(1), period)
		def program = newSurveyProgram(CODE(1), survey, 1, [(DISTRICT_HOSPITAL_GROUP)])
		def section = newSurveySection(CODE(1), program, 1, [(DISTRICT_HOSPITAL_GROUP)])
		def question = newTableQuestion(CODE(1), section, 1, [(DISTRICT_HOSPITAL_GROUP)])
		def column = newTableColumn(CODE(1), question, 1, [(DISTRICT_HOSPITAL_GROUP)])
		def row = newTableRow(CODE(1), question, 1, [(DISTRICT_HOSPITAL_GROUP)], [(column): null])

		when:
		question.delete()
		section.removeFromQuestions(question)
		
		then:
		SurveyQuestion.count() == 0
		SurveyElement.count() == 0
		SurveyTableRow.count() == 0
		SurveyTableColumn.count() == 0
	}
	
	def "delete checkbox question deletes options"() {
		setup:
		setupLocationTree()
		def period = newPeriod()
		def survey = newSurvey(CODE(1), period)
		def program = newSurveyProgram(CODE(1), survey, 1, [(DISTRICT_HOSPITAL_GROUP)])
		def section = newSurveySection(CODE(1), program, 1, [(DISTRICT_HOSPITAL_GROUP)])
		def question = newCheckboxQuestion(CODE(1), section, 1, [(DISTRICT_HOSPITAL_GROUP)])
		def option = newCheckboxOption(CODE(1), question, 1, [(DISTRICT_HOSPITAL_GROUP)])

		when:
		question.delete()
		section.removeFromQuestions(question)
		
		then:
		SurveyQuestion.count() == 0
		SurveyElement.count() == 0
		SurveyCheckboxOption.count() == 0
	}
	
	def "delete survey table row deletes row column map"() {
		setup:
		setupLocationTree()
		def period = newPeriod()
		def survey = newSurvey(CODE(1), period)
		def program = newSurveyProgram(CODE(1), survey, 1, [(DISTRICT_HOSPITAL_GROUP)])
		def section = newSurveySection(CODE(1), program, 1, [(DISTRICT_HOSPITAL_GROUP)])
		def question1 = newTableQuestion(CODE(1), section, 1, [(DISTRICT_HOSPITAL_GROUP)])
		def column = newTableColumn(CODE(1), question1, 1, [(DISTRICT_HOSPITAL_GROUP)])
		def row = newTableRow(CODE(1), question1, 1, [(DISTRICT_HOSPITAL_GROUP)], [(column): null])

		when:
		row.delete()
		question1.removeFromRows(row)
		
		then:
		SurveyTableRow.count() == 0
		SurveyTableRowColumnMap.count() == 0
	}
	
	def "delete section deletes questions"() {
		setup:
		setupLocationTree()
		def period = newPeriod()
		def survey = newSurvey(CODE(1), period)
		def program = newSurveyProgram(CODE(1), survey, 1, [(DISTRICT_HOSPITAL_GROUP)])
		def section = newSurveySection(CODE(1), program, 1, [(DISTRICT_HOSPITAL_GROUP)])
		def question = newSimpleQuestion(CODE(2), section, 1, [(DISTRICT_HOSPITAL_GROUP)])
		
		when:
		section.delete()
		program.removeFromSections(section)
		
		then:
		SurveySection.count() == 0
		SurveyQuestion.count() == 0
	}
	
	def "delete program deletes sections"() {
		setup:
		setupLocationTree()
		def period = newPeriod()
		def survey = newSurvey(CODE(1), period)
		def program = newSurveyProgram(CODE(1), survey, 1, [(DISTRICT_HOSPITAL_GROUP)])
		def section = newSurveySection(CODE(1), program, 1, [(DISTRICT_HOSPITAL_GROUP)])
		
		when:
		program.delete()
		survey.removeFromPrograms(program)
		
		then:
		SurveyProgram.count() == 0
		SurveySection.count() == 0
	}
	
	def "delete survey deletes programs and skip rules"() {
		setup:
		setupLocationTree()
		def period = newPeriod()
		def survey = newSurvey(CODE(1), period)
		def program = newSurveyProgram(CODE(1), survey, 1, [(DISTRICT_HOSPITAL_GROUP)])
		def skipRule = newSurveySkipRule(CODE(1), survey, "true", [:], [])
		
		when:
		survey.delete()
		
		then:
		Survey.count() == 0
		SurveySkipRule.count() == 0
	}
	
}
