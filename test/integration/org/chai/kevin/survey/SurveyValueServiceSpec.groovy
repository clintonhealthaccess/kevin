package org.chai.kevin.survey

import org.apache.catalina.security.SecurityUtil;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.chai.kevin.data.Type;
import org.chai.kevin.security.User;
import org.chai.kevin.survey.validation.SurveyEnteredValue;
import org.chai.kevin.value.Value;
import org.hisp.dhis.organisationunit.OrganisationUnit;

class SurveyValueServiceSpec extends SurveyIntegrationTests {

	def surveyValueService
	
	def "saving entered entities saves user and timestamp"() {
		setup:
		setupOrganisationUnitTree()
		setupSecurityManager(newUser('test', 'uuid'))
		def period = newPeriod()
		def survey = newSurvey(period)
		def objective = newSurveyObjective(survey, 1, [(HEALTH_CENTER_GROUP),(DISTRICT_HOSPITAL_GROUP)])
		def section = newSurveySection(objective, 1, [(HEALTH_CENTER_GROUP),(DISTRICT_HOSPITAL_GROUP)])
		def question1 = newSimpleQuestion(section, 1, [(HEALTH_CENTER_GROUP),(DISTRICT_HOSPITAL_GROUP)])
		def element1 = newSurveyElement(question1, newRawDataElement(CODE(1), Type.TYPE_NUMBER()))
		
		when:
		def surveyEnteredValue = newSurveyEnteredValue(element1, period, OrganisationUnit.findByName(KIVUYE), v("1"))
		
		then:
		surveyEnteredValue.userUuid == null
		surveyEnteredValue.timestamp == null
		
		when:
		surveyValueService.save(surveyEnteredValue)
		
		then:
		surveyEnteredValue.userUuid == 'uuid'
		surveyEnteredValue.timestamp != null
		
		when:
		def surveyEnteredQuestion = newSurveyEnteredQuestion(question1, period, OrganisationUnit.findByName(KIVUYE), false, true)
		
		then:
		surveyEnteredQuestion.userUuid == null
		surveyEnteredQuestion.timestamp == null
		
		when:
		surveyValueService.save(surveyEnteredQuestion)
		
		then:
		surveyEnteredQuestion.userUuid == 'uuid'
		surveyEnteredQuestion.timestamp != null
	}
	
	def "get number of survey entered value does not take into account organisation unit group"() {
		setup:
		setupOrganisationUnitTree()
		setupSecurityManager(newUser('test', 'uuid'))
		def period = newPeriod()
		def survey = newSurvey(period)
		def objective = newSurveyObjective(survey, 1, [(HEALTH_CENTER_GROUP)])
		def section = newSurveySection(objective, 1, [(HEALTH_CENTER_GROUP)])
		def question1 = newSimpleQuestion(section, 1, [(HEALTH_CENTER_GROUP)])
		def element1 = newSurveyElement(question1, newRawDataElement(CODE(1), Type.TYPE_NUMBER()))
		
		when:
		def surveyEnteredQuestion = newSurveyEnteredQuestion(question1, period, OrganisationUnit.findByName(BUTARO), false, true)
		
		then:
		surveyValueService.getNumberOfSurveyEnteredQuestions(survey, OrganisationUnit.findByName(BUTARO), objective, null, true, false, false) == 1
	}
	
	def "get number of survey entered questions with skip"() {
		setup:
		setupOrganisationUnitTree()
		setupSecurityManager(newUser('test', 'uuid'))
		def period = newPeriod()
		def survey = newSurvey(period)
		def objective = newSurveyObjective(survey, 1, [(HEALTH_CENTER_GROUP)])
		def section = newSurveySection(objective, 1, [(HEALTH_CENTER_GROUP)])
		def question1 = newSimpleQuestion(section, 1, [(HEALTH_CENTER_GROUP)])
		def element1 = newSurveyElement(question1, newRawDataElement(CODE(1), Type.TYPE_NUMBER()))
		
		when:
		def surveyEnteredQuestion = newSurveyEnteredQuestion(question1, period, OrganisationUnit.findByName(BUTARO), false, true)
		
		then:
		surveyValueService.getNumberOfSurveyEnteredQuestions(survey, OrganisationUnit.findByName(BUTARO), objective, null, true, false, false) == 1
		surveyValueService.getNumberOfSurveyEnteredQuestions(survey, OrganisationUnit.findByName(BUTARO), objective, null, true, false, true) == 1
		
		when:
		def skipRule = newSkipRule(survey, "1", [:], [])
		surveyEnteredQuestion.complete = false
		surveyEnteredQuestion.skippedRules = new HashSet([skipRule])
		surveyEnteredQuestion.save(failOnError: true, flush: true)
		
		then:
		surveyValueService.getNumberOfSurveyEnteredQuestions(survey, OrganisationUnit.findByName(BUTARO), objective, null, true, false, false) == 0
		surveyValueService.getNumberOfSurveyEnteredQuestions(survey, OrganisationUnit.findByName(BUTARO), objective, null, true, false, true) == 1
	}
	
	def "delete survey entered values for survey element"() {
		setup:
		setupOrganisationUnitTree()
		setupSecurityManager(newUser('test', 'uuid'))
		def period = newPeriod()
		def survey = newSurvey(period)
		def objective = newSurveyObjective(survey, 1, [(HEALTH_CENTER_GROUP),(DISTRICT_HOSPITAL_GROUP)])
		def section = newSurveySection(objective, 1, [(HEALTH_CENTER_GROUP),(DISTRICT_HOSPITAL_GROUP)])
		def question1 = newSimpleQuestion(section, 1, [(HEALTH_CENTER_GROUP),(DISTRICT_HOSPITAL_GROUP)])
		def element1 = newSurveyElement(question1, newRawDataElement(CODE(1), Type.TYPE_NUMBER()))
		def surveyEnteredValue = newSurveyEnteredValue(element1, period, OrganisationUnit.findByName(KIVUYE), v("1"))
		
		expect:
		SurveyEnteredValue.count() == 1
		
		when:
		surveyValueService.deleteEnteredValues(element1)
		
		then:
		SurveyEnteredValue.count() == 0
	}
	
	def "get survey entered values for organisation - by section"() {
		setup:
		setupOrganisationUnitTree()
		setupSecurityManager(newUser('test', 'uuid'))
		def period = newPeriod()
		def survey = newSurvey(period)
		def objective = newSurveyObjective(survey, 1, [(HEALTH_CENTER_GROUP)])
		
		def section1 = newSurveySection(objective, 1, [(HEALTH_CENTER_GROUP)])
		def question1 = newSimpleQuestion(section1, 1, [(HEALTH_CENTER_GROUP)])
		def element1 = newSurveyElement(question1, newRawDataElement(CODE(1), Type.TYPE_NUMBER()))
		
		def section2 = newSurveySection(objective, 1, [(HEALTH_CENTER_GROUP)])
		def question2 = newSimpleQuestion(section2, 1, [(HEALTH_CENTER_GROUP)])
		def element2 = newSurveyElement(question2, newRawDataElement(CODE(2), Type.TYPE_NUMBER()))

		expect:
		surveyValueService.getSurveyEnteredValues(OrganisationUnit.findByName(KIVUYE), null, null, null).isEmpty()
		
		when:
		def value1 = newSurveyEnteredValue(element1, period, OrganisationUnit.findByName(KIVUYE), Value.NULL)
		
		then:
		surveyValueService.getSurveyEnteredValues(OrganisationUnit.findByName(KIVUYE), null, null, null).equals([value1])
		surveyValueService.getSurveyEnteredValues(OrganisationUnit.findByName(KIVUYE), section1, null, null).equals([value1])
		surveyValueService.getSurveyEnteredValues(OrganisationUnit.findByName(KIVUYE), section2, null, null).isEmpty()
		
		when:
		def value2 = newSurveyEnteredValue(element2, period, OrganisationUnit.findByName(KIVUYE), Value.NULL)
		
		then:
		surveyValueService.getSurveyEnteredValues(OrganisationUnit.findByName(KIVUYE), null, null, null).equals([value1, value2])
		surveyValueService.getSurveyEnteredValues(OrganisationUnit.findByName(KIVUYE), section1, null, null).equals([value1])
		surveyValueService.getSurveyEnteredValues(OrganisationUnit.findByName(KIVUYE), section2, null, null).equals([value2])
	}
	
	
	def "get survey entered values for organisation - by objective"() {
		setup:
		setupOrganisationUnitTree()
		setupSecurityManager(newUser('test', 'uuid'))
		def period = newPeriod()
		def survey = newSurvey(period)
		
		def objective1 = newSurveyObjective(survey, 1, [(HEALTH_CENTER_GROUP)])		
		def section1 = newSurveySection(objective1, 1, [(HEALTH_CENTER_GROUP)])
		def question1 = newSimpleQuestion(section1, 1, [(HEALTH_CENTER_GROUP)])
		def element1 = newSurveyElement(question1, newRawDataElement(CODE(1), Type.TYPE_NUMBER()))
		
		def objective2 = newSurveyObjective(survey, 1, [(HEALTH_CENTER_GROUP)])
		def section2 = newSurveySection(objective2, 1, [(HEALTH_CENTER_GROUP)])
		def question2 = newSimpleQuestion(section2, 1, [(HEALTH_CENTER_GROUP)])
		def element2 = newSurveyElement(question2, newRawDataElement(CODE(2), Type.TYPE_NUMBER()))

		expect:
		surveyValueService.getSurveyEnteredValues(OrganisationUnit.findByName(KIVUYE), null, null, null).isEmpty()
		
		when:
		def value1 = newSurveyEnteredValue(element1, period, OrganisationUnit.findByName(KIVUYE), Value.NULL)
		
		then:
		surveyValueService.getSurveyEnteredValues(OrganisationUnit.findByName(KIVUYE), null, null, null).equals([value1])
		surveyValueService.getSurveyEnteredValues(OrganisationUnit.findByName(KIVUYE), null, objective1, null).equals([value1])
		surveyValueService.getSurveyEnteredValues(OrganisationUnit.findByName(KIVUYE), null, objective2, null).isEmpty()
		
		when:
		def value2 = newSurveyEnteredValue(element2, period, OrganisationUnit.findByName(KIVUYE), Value.NULL)
		
		then:
		surveyValueService.getSurveyEnteredValues(OrganisationUnit.findByName(KIVUYE), null, null, null).equals([value1, value2])
		surveyValueService.getSurveyEnteredValues(OrganisationUnit.findByName(KIVUYE), null, objective1, null).equals([value1])
		surveyValueService.getSurveyEnteredValues(OrganisationUnit.findByName(KIVUYE), null, objective2, null).equals([value2])
	}
	
	def "get survey entered values for organisation - by survey"() {
		setup:
		setupOrganisationUnitTree()
		setupSecurityManager(newUser('test', 'uuid'))
		def period1 = newPeriod()
		def period2 = newPeriod()
		
		def survey1 = newSurvey(period1)
		def objective1 = newSurveyObjective(survey1, 1, [(HEALTH_CENTER_GROUP)])
		def section1 = newSurveySection(objective1, 1, [(HEALTH_CENTER_GROUP)])
		def question1 = newSimpleQuestion(section1, 1, [(HEALTH_CENTER_GROUP)])
		def element1 = newSurveyElement(question1, newRawDataElement(CODE(1), Type.TYPE_NUMBER()))		
		
		def survey2 = newSurvey(period2)
		def objective2 = newSurveyObjective(survey2, 1, [(HEALTH_CENTER_GROUP)])
		def section2 = newSurveySection(objective2, 1, [(HEALTH_CENTER_GROUP)])
		def question2 = newSimpleQuestion(section2, 1, [(HEALTH_CENTER_GROUP)])
		def element2 = newSurveyElement(question2, newRawDataElement(CODE(2), Type.TYPE_NUMBER()))

		expect:
		surveyValueService.getSurveyEnteredValues(OrganisationUnit.findByName(KIVUYE), null, null, null).isEmpty()
		
		when:
		def value1 = newSurveyEnteredValue(element1, period1, OrganisationUnit.findByName(KIVUYE), Value.NULL)
		
		then:
		surveyValueService.getSurveyEnteredValues(OrganisationUnit.findByName(KIVUYE), null, null, null).equals([value1])
		surveyValueService.getSurveyEnteredValues(OrganisationUnit.findByName(KIVUYE), null, null, survey1).equals([value1])
		surveyValueService.getSurveyEnteredValues(OrganisationUnit.findByName(KIVUYE), null, null, survey2).isEmpty()
		
		when:
		def value2 = newSurveyEnteredValue(element2, period2, OrganisationUnit.findByName(KIVUYE), Value.NULL)
		
		then:
		surveyValueService.getSurveyEnteredValues(OrganisationUnit.findByName(KIVUYE), null, null, null).equals([value1, value2])
		surveyValueService.getSurveyEnteredValues(OrganisationUnit.findByName(KIVUYE), null, null, survey1).equals([value1])
		surveyValueService.getSurveyEnteredValues(OrganisationUnit.findByName(KIVUYE), null, null, survey2).equals([value2])
	}
}
