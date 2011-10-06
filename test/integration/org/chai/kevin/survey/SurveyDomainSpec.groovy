package org.chai.kevin.survey

import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.chai.kevin.IntegrationTests
import org.chai.kevin.data.DataElement
import org.chai.kevin.data.Enum
import org.chai.kevin.data.EnumOption
import org.chai.kevin.data.Type;
import org.chai.kevin.survey.validation.SurveyLog;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup
import org.hisp.dhis.period.Period


class SurveyDomainSpec extends SurveyIntegrationTests {

	private static final Log log = LogFactory.getLog(SurveyDomainSpec.class)

	
	def "table question has data elements"() {
		setup:
		def period = newPeriod()
		def survey = newSurvey(period)
		def objective = newSurveyObjective(survey, 1, [])
		def section = newSurveySection(objective, 1, [])
		def question = newTableQuestion(section, 1, [])
		def dataElement = newDataElement(CODE(1), Type.TYPE_NUMBER())
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
		def objective = newSurveyObjective(survey, 1, [])
		def section = newSurveySection(objective, 1, [])
		def question = newSimpleQuestion(section, 1, [])
		def dataElement = newDataElement(CODE(1), Type.TYPE_NUMBER())
		def element = newSurveyElement(question, dataElement)
		
		when:
		def skipRule = new SurveySkipRule(survey: survey, expression: "\$"+element.id+" == 1", skippedSurveyElements: [:], skippedSurveyQuestions: [])
		
		then:
		skipRule.id == null
		
		when:
		survey.addSkipRule(skipRule)
		survey.save(failOnError: true, flush: true)
		
		then:
		skipRule.id != null
	}
	
	
	def "test question table number of organisation unit applicable"(){
		
		setup:
		def period = newPeriod()
		def survey = newSurvey(period)
		def objective = newSurveyObjective(survey, 1, [(DISTRICT_HOSPITAL_GROUP)])
		def section = newSurveySection(objective, 1, [(DISTRICT_HOSPITAL_GROUP)])
		def question = newTableQuestion(section, 1, [(DISTRICT_HOSPITAL_GROUP)])
		def dataElement = newDataElement(CODE(1), Type.TYPE_NUMBER())
		def element = newSurveyElement(question, dataElement)
		def column = newTableColumn(question, 1, [(DISTRICT_HOSPITAL_GROUP)])
		def row = newTableRow(question, 1, [(DISTRICT_HOSPITAL_GROUP)], [(column): element])
		
		when:
		def orgunitgroupList = question.getOrganisationUnitGroupApplicable(element)
		
		then:
		orgunitgroupList.size() == 1
	}

	def "survey elements can have 2 validation rules with the same prefix"() {
		setup:
		def period = newPeriod()
		def survey = newSurvey(period)
		def objective = newSurveyObjective(survey, 1, [(DISTRICT_HOSPITAL_GROUP)])
		def section = newSurveySection(objective, 1, [(DISTRICT_HOSPITAL_GROUP)])
		def question = newTableQuestion(section, 1, [(DISTRICT_HOSPITAL_GROUP)])
		def dataElement = newDataElement(CODE(1), Type.TYPE_NUMBER())
		def element = newSurveyElement(question, dataElement)
		
		when:
		newSurveyValidationRule(element, "", [], "true");
		newSurveyValidationRule(element, "", [], "true");
		
		then:
		SurveyElement.list()[0].validationRules.size() == 2
		SurveyValidationRule.count() == 2
	}
	
	def "save surveylog"() {
		setup:
		def period = newPeriod()
		def survey = newSurvey(period)
		newSurveyObjective(survey, 2, [(DISTRICT_HOSPITAL_GROUP)])
		def objective = newSurveyObjective(survey, 1, [(DISTRICT_HOSPITAL_GROUP)])
		def kivuye = newOrganisationUnit(KIVUYE)
		
		when:
		new SurveyLog(event: "test", organisationUnit: kivuye, timestamp: new Date(), survey: survey, objective: objective).save(failOnError: true)
		
		then:
		SurveyLog.count() == 1
	}
	
}
