package org.chai.kevin.survey

import org.apache.catalina.security.SecurityUtil;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.chai.kevin.data.Type;
import org.chai.kevin.form.FormEnteredValue;
import org.chai.kevin.location.DataLocation;
import org.chai.kevin.security.User;
import org.chai.kevin.value.Value;

class SurveyValueServiceSpec extends SurveyIntegrationTests {

	def surveyValueService
	
//	def "get number of survey entered questions does not take into account location unit group"() {
//		setup:
//		setupLocationTree()
//		setupSecurityManager(newUser('test', 'uuid'))
//		def period = newPeriod()
//		def survey = newSurvey(CODE(1), period)
//		def program = newSurveyProgram(CODE(1), survey, 1, [(HEALTH_CENTER_GROUP)])
//		def section = newSurveySection(CODE(1), program, 1, [(HEALTH_CENTER_GROUP)])
//		def question1 = newSimpleQuestion(CODE(1), section, 1, [(HEALTH_CENTER_GROUP)])
//		def element1 = newSurveyElement(question1, newRawDataElement(CODE(1), Type.TYPE_NUMBER()))
//		
//		when:
//		def surveyEnteredQuestion = newSurveyEnteredQuestion(question1, period, DataLocation.findByCode(BUTARO), false, true)
//		
//		then:
//		surveyValueService.getNumberOfSurveyEnteredQuestions(survey, DataLocation.findByCode(BUTARO), program, null, true, false, false) == 1
//	}
	
//	def "get number of survey entered questions with skip"() {
//		setup:
//		setupLocationTree()
//		setupSecurityManager(newUser('test', 'uuid'))
//		def period = newPeriod()
//		def survey = newSurvey(CODE(1), period)
//		def program = newSurveyProgram(CODE(1), survey, 1, [(HEALTH_CENTER_GROUP)])
//		def section = newSurveySection(CODE(1), program, 1, [(HEALTH_CENTER_GROUP)])
//		def question1 = newSimpleQuestion(CODE(1), section, 1, [(HEALTH_CENTER_GROUP)])
//		def element1 = newSurveyElement(question1, newRawDataElement(CODE(1), Type.TYPE_NUMBER()))
//		
//		when:
//		def surveyEnteredQuestion = newSurveyEnteredQuestion(question1, period, DataLocation.findByCode(BUTARO), false, true)
//		
//		then:
//		surveyValueService.getNumberOfSurveyEnteredQuestions(survey, DataLocation.findByCode(BUTARO), program, null, true, false, false) == 1
//		surveyValueService.getNumberOfSurveyEnteredQuestions(survey, DataLocation.findByCode(BUTARO), program, null, true, false, true) == 1
//		
//		when:
//		def skipRule = newSurveySkipRule(CODE(1), survey, "1", [:], [])
//		surveyEnteredQuestion.complete = false
//		surveyEnteredQuestion.skippedRules = new HashSet([skipRule])
//		surveyEnteredQuestion.save(failOnError: true, flush: true)
//		
//		then:
//		surveyValueService.getNumberOfSurveyEnteredQuestions(survey, DataLocation.findByCode(BUTARO), program, null, true, false, false) == 0
//		surveyValueService.getNumberOfSurveyEnteredQuestions(survey, DataLocation.findByCode(BUTARO), program, null, true, false, true) == 1
//	}
	
	def "delete survey entered values for survey element"() {
		setup:
		setupLocationTree()
		setupSecurityManager(newUser('test', 'uuid'))
		def period = newPeriod()
		def survey = newSurvey(CODE(1), period)
		def program = newSurveyProgram(CODE(1), survey, 1, [(HEALTH_CENTER_GROUP),(DISTRICT_HOSPITAL_GROUP)])
		def section = newSurveySection(CODE(1), program, 1, [(HEALTH_CENTER_GROUP),(DISTRICT_HOSPITAL_GROUP)])
		def question1 = newSimpleQuestion(CODE(1), section, 1, [(HEALTH_CENTER_GROUP),(DISTRICT_HOSPITAL_GROUP)])
		def element1 = newSurveyElement(question1, newRawDataElement(CODE(1), Type.TYPE_NUMBER()))
		def formEnteredValue = newFormEnteredValue(element1, period, DataLocation.findByCode(KIVUYE), v("1"))
		
		expect:
		FormEnteredValue.count() == 1
		
		when:
		surveyValueService.deleteEnteredValues(element1)
		
		then:
		FormEnteredValue.count() == 0
	}
	
	def "get survey entered values for location - by section"() {
		setup:
		setupLocationTree()
		setupSecurityManager(newUser('test', 'uuid'))
		def period = newPeriod()
		def survey = newSurvey(CODE(1), period)
		def program = newSurveyProgram(CODE(1), survey, 1, [(HEALTH_CENTER_GROUP)])
		
		def section1 = newSurveySection(CODE(1), program, 1, [(HEALTH_CENTER_GROUP)])
		def question1 = newSimpleQuestion(CODE(1), section1, 1, [(HEALTH_CENTER_GROUP)])
		def element1 = newSurveyElement(question1, newRawDataElement(CODE(1), Type.TYPE_NUMBER()))
		
		def section2 = newSurveySection(CODE(2), program, 1, [(HEALTH_CENTER_GROUP)])
		def question2 = newSimpleQuestion(CODE(2), section2, 1, [(HEALTH_CENTER_GROUP)])
		def element2 = newSurveyElement(question2, newRawDataElement(CODE(2), Type.TYPE_NUMBER()))

		expect:
		surveyValueService.getFormEnteredValues(DataLocation.findByCode(KIVUYE), null, null, null).isEmpty()
		
		when:
		def value1 = newFormEnteredValue(element1, period, DataLocation.findByCode(KIVUYE), Value.NULL_INSTANCE())
		
		then:
		surveyValueService.getFormEnteredValues(DataLocation.findByCode(KIVUYE), null, null, null).equals([value1])
		surveyValueService.getFormEnteredValues(DataLocation.findByCode(KIVUYE), section1, null, null).equals([value1])
		surveyValueService.getFormEnteredValues(DataLocation.findByCode(KIVUYE), section2, null, null).isEmpty()
		
		when:
		def value2 = newFormEnteredValue(element2, period, DataLocation.findByCode(KIVUYE), Value.NULL_INSTANCE())
		
		then:
		surveyValueService.getFormEnteredValues(DataLocation.findByCode(KIVUYE), null, null, null).equals([value1, value2])
		surveyValueService.getFormEnteredValues(DataLocation.findByCode(KIVUYE), section1, null, null).equals([value1])
		surveyValueService.getFormEnteredValues(DataLocation.findByCode(KIVUYE), section2, null, null).equals([value2])
	}
	
	
	def "get survey entered values for location - by program"() {
		setup:
		setupLocationTree()
		setupSecurityManager(newUser('test', 'uuid'))
		def period = newPeriod()
		def survey = newSurvey(CODE(1), period)
		
		def program1 = newSurveyProgram(CODE(1), survey, 1, [(HEALTH_CENTER_GROUP)])		
		def section1 = newSurveySection(CODE(1), program1, 1, [(HEALTH_CENTER_GROUP)])
		def question1 = newSimpleQuestion(CODE(1), section1, 1, [(HEALTH_CENTER_GROUP)])
		def element1 = newSurveyElement(question1, newRawDataElement(CODE(1), Type.TYPE_NUMBER()))
		
		def program2 = newSurveyProgram(CODE(2), survey, 1, [(HEALTH_CENTER_GROUP)])
		def section2 = newSurveySection(CODE(2), program2, 1, [(HEALTH_CENTER_GROUP)])
		def question2 = newSimpleQuestion(CODE(2), section2, 1, [(HEALTH_CENTER_GROUP)])
		def element2 = newSurveyElement(question2, newRawDataElement(CODE(2), Type.TYPE_NUMBER()))

		expect:
		surveyValueService.getFormEnteredValues(DataLocation.findByCode(KIVUYE), null, null, null).isEmpty()
		
		when:
		def value1 = newFormEnteredValue(element1, period, DataLocation.findByCode(KIVUYE), Value.NULL_INSTANCE())
		
		then:
		surveyValueService.getFormEnteredValues(DataLocation.findByCode(KIVUYE), null, null, null).equals([value1])
		surveyValueService.getFormEnteredValues(DataLocation.findByCode(KIVUYE), null, program1, null).equals([value1])
		surveyValueService.getFormEnteredValues(DataLocation.findByCode(KIVUYE), null, program2, null).isEmpty()
		
		when:
		def value2 = newFormEnteredValue(element2, period, DataLocation.findByCode(KIVUYE), Value.NULL_INSTANCE())
		
		then:
		surveyValueService.getFormEnteredValues(DataLocation.findByCode(KIVUYE), null, null, null).equals([value1, value2])
		surveyValueService.getFormEnteredValues(DataLocation.findByCode(KIVUYE), null, program1, null).equals([value1])
		surveyValueService.getFormEnteredValues(DataLocation.findByCode(KIVUYE), null, program2, null).equals([value2])
	}
	
	def "get survey entered values for location - by survey"() {
		setup:
		setupLocationTree()
		setupSecurityManager(newUser('test', 'uuid'))
		def period1 = newPeriod()
		def period2 = newPeriod()
		
		def survey1 = newSurvey(CODE(1), period1)
		def program1 = newSurveyProgram(CODE(1), survey1, 1, [(HEALTH_CENTER_GROUP)])
		def section1 = newSurveySection(CODE(1), program1, 1, [(HEALTH_CENTER_GROUP)])
		def question1 = newSimpleQuestion(CODE(1), section1, 1, [(HEALTH_CENTER_GROUP)])
		def element1 = newSurveyElement(question1, newRawDataElement(CODE(1), Type.TYPE_NUMBER()))		
		
		def survey2 = newSurvey(CODE(2), period2)
		def program2 = newSurveyProgram(CODE(2), survey2, 1, [(HEALTH_CENTER_GROUP)])
		def section2 = newSurveySection(CODE(2), program2, 1, [(HEALTH_CENTER_GROUP)])
		def question2 = newSimpleQuestion(CODE(2), section2, 1, [(HEALTH_CENTER_GROUP)])
		def element2 = newSurveyElement(question2, newRawDataElement(CODE(2), Type.TYPE_NUMBER()))

		expect:
		surveyValueService.getFormEnteredValues(DataLocation.findByCode(KIVUYE), null, null, null).isEmpty()
		
		when:
		def value1 = newFormEnteredValue(element1, period1, DataLocation.findByCode(KIVUYE), Value.NULL_INSTANCE())
		
		then:
		surveyValueService.getFormEnteredValues(DataLocation.findByCode(KIVUYE), null, null, null).equals([value1])
		surveyValueService.getFormEnteredValues(DataLocation.findByCode(KIVUYE), null, null, survey1).equals([value1])
		surveyValueService.getFormEnteredValues(DataLocation.findByCode(KIVUYE), null, null, survey2).isEmpty()
		
		when:
		def value2 = newFormEnteredValue(element2, period2, DataLocation.findByCode(KIVUYE), Value.NULL_INSTANCE())
		
		then:
		surveyValueService.getFormEnteredValues(DataLocation.findByCode(KIVUYE), null, null, null).equals([value1, value2])
		surveyValueService.getFormEnteredValues(DataLocation.findByCode(KIVUYE), null, null, survey1).equals([value1])
		surveyValueService.getFormEnteredValues(DataLocation.findByCode(KIVUYE), null, null, survey2).equals([value2])
	}
}
