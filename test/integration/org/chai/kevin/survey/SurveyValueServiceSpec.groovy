package org.chai.kevin.survey

import org.apache.catalina.security.SecurityUtil;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.chai.kevin.data.Type;
import org.chai.kevin.security.User;
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
		def element1 = newSurveyElement(question1, newDataElement(CODE(1), Type.TYPE_NUMBER()))
		
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
		def element1 = newSurveyElement(question1, newDataElement(CODE(1), Type.TYPE_NUMBER()))
		
		when:
		def surveyEnteredValue = newSurveyEnteredQuestion(question1, period, OrganisationUnit.findByName(BUTARO), false, true)
		
		then:
		surveyValueService.getNumberOfSurveyEnteredQuestions(survey, OrganisationUnit.findByName(BUTARO), objective, null, true, false) == 1
	}
	
}
