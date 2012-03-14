package org.chai.kevin.survey

import org.apache.catalina.security.SecurityUtil;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.chai.kevin.data.Type;
import org.chai.kevin.form.FormEnteredValue;
import org.chai.kevin.location.DataLocationEntity;
import org.chai.kevin.security.User;
import org.chai.kevin.value.Value;

class SurveyValueServiceSpec extends SurveyIntegrationTests {

	def surveyValueService
	
	def "saving entered entities saves user and timestamp"() {
		setup:
		setupLocationTree()
		setupSecurityManager(newUser('test', 'uuid'))
		def period = newPeriod()
		def survey = newSurvey(period)
		def program = newSurveyProgram(survey, 1, [(HEALTH_CENTER_GROUP),(DISTRICT_HOSPITAL_GROUP)])
		def section = newSurveySection(program, 1, [(HEALTH_CENTER_GROUP),(DISTRICT_HOSPITAL_GROUP)])
		def question1 = newSimpleQuestion(section, 1, [(HEALTH_CENTER_GROUP),(DISTRICT_HOSPITAL_GROUP)])
		def element1 = newSurveyElement(question1, newRawDataElement(CODE(1), Type.TYPE_NUMBER()))
		
		when:
		def formEnteredValue = newFormEnteredValue(element1, period, DataLocationEntity.findByCode(KIVUYE), v("1"))
		
		then:
		formEnteredValue.userUuid == null
		formEnteredValue.timestamp == null
		
		when:
		surveyValueService.save(formEnteredValue)
		
		then:
		formEnteredValue.userUuid == 'uuid'
		formEnteredValue.timestamp != null
		
		when:
		def surveyEnteredQuestion = newSurveyEnteredQuestion(question1, period, DataLocationEntity.findByCode(KIVUYE), false, true)
		
		then:
		surveyEnteredQuestion.userUuid == null
		surveyEnteredQuestion.timestamp == null
		
		when:
		surveyValueService.save(surveyEnteredQuestion)
		
		then:
		surveyEnteredQuestion.userUuid == 'uuid'
		surveyEnteredQuestion.timestamp != null
	}
	
	def "get number of survey entered value does not take into account location unit group"() {
		setup:
		setupLocationTree()
		setupSecurityManager(newUser('test', 'uuid'))
		def period = newPeriod()
		def survey = newSurvey(period)
		def program = newSurveyProgram(survey, 1, [(HEALTH_CENTER_GROUP)])
		def section = newSurveySection(program, 1, [(HEALTH_CENTER_GROUP)])
		def question1 = newSimpleQuestion(section, 1, [(HEALTH_CENTER_GROUP)])
		def element1 = newSurveyElement(question1, newRawDataElement(CODE(1), Type.TYPE_NUMBER()))
		
		when:
		def surveyEnteredQuestion = newSurveyEnteredQuestion(question1, period, DataLocationEntity.findByCode(BUTARO), false, true)
		
		then:
		surveyValueService.getNumberOfSurveyEnteredQuestions(survey, DataLocationEntity.findByCode(BUTARO), program, null, true, false, false) == 1
	}
	
	def "get number of survey entered questions with skip"() {
		setup:
		setupLocationTree()
		setupSecurityManager(newUser('test', 'uuid'))
		def period = newPeriod()
		def survey = newSurvey(period)
		def program = newSurveyProgram(survey, 1, [(HEALTH_CENTER_GROUP)])
		def section = newSurveySection(program, 1, [(HEALTH_CENTER_GROUP)])
		def question1 = newSimpleQuestion(section, 1, [(HEALTH_CENTER_GROUP)])
		def element1 = newSurveyElement(question1, newRawDataElement(CODE(1), Type.TYPE_NUMBER()))
		
		when:
		def surveyEnteredQuestion = newSurveyEnteredQuestion(question1, period, DataLocationEntity.findByCode(BUTARO), false, true)
		
		then:
		surveyValueService.getNumberOfSurveyEnteredQuestions(survey, DataLocationEntity.findByCode(BUTARO), program, null, true, false, false) == 1
		surveyValueService.getNumberOfSurveyEnteredQuestions(survey, DataLocationEntity.findByCode(BUTARO), program, null, true, false, true) == 1
		
		when:
		def skipRule = newSurveySkipRule(survey, "1", [:], [])
		surveyEnteredQuestion.complete = false
		surveyEnteredQuestion.skippedRules = new HashSet([skipRule])
		surveyEnteredQuestion.save(failOnError: true, flush: true)
		
		then:
		surveyValueService.getNumberOfSurveyEnteredQuestions(survey, DataLocationEntity.findByCode(BUTARO), program, null, true, false, false) == 0
		surveyValueService.getNumberOfSurveyEnteredQuestions(survey, DataLocationEntity.findByCode(BUTARO), program, null, true, false, true) == 1
	}
	
	def "delete survey entered values for survey element"() {
		setup:
		setupLocationTree()
		setupSecurityManager(newUser('test', 'uuid'))
		def period = newPeriod()
		def survey = newSurvey(period)
		def program = newSurveyProgram(survey, 1, [(HEALTH_CENTER_GROUP),(DISTRICT_HOSPITAL_GROUP)])
		def section = newSurveySection(program, 1, [(HEALTH_CENTER_GROUP),(DISTRICT_HOSPITAL_GROUP)])
		def question1 = newSimpleQuestion(section, 1, [(HEALTH_CENTER_GROUP),(DISTRICT_HOSPITAL_GROUP)])
		def element1 = newSurveyElement(question1, newRawDataElement(CODE(1), Type.TYPE_NUMBER()))
		def formEnteredValue = newFormEnteredValue(element1, period, DataLocationEntity.findByCode(KIVUYE), v("1"))
		
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
		def survey = newSurvey(period)
		def program = newSurveyProgram(survey, 1, [(HEALTH_CENTER_GROUP)])
		
		def section1 = newSurveySection(program, 1, [(HEALTH_CENTER_GROUP)])
		def question1 = newSimpleQuestion(section1, 1, [(HEALTH_CENTER_GROUP)])
		def element1 = newSurveyElement(question1, newRawDataElement(CODE(1), Type.TYPE_NUMBER()))
		
		def section2 = newSurveySection(program, 1, [(HEALTH_CENTER_GROUP)])
		def question2 = newSimpleQuestion(section2, 1, [(HEALTH_CENTER_GROUP)])
		def element2 = newSurveyElement(question2, newRawDataElement(CODE(2), Type.TYPE_NUMBER()))

		expect:
		surveyValueService.getFormEnteredValues(DataLocationEntity.findByCode(KIVUYE), null, null, null).isEmpty()
		
		when:
		def value1 = newFormEnteredValue(element1, period, DataLocationEntity.findByCode(KIVUYE), Value.NULL_INSTANCE())
		
		then:
		surveyValueService.getFormEnteredValues(DataLocationEntity.findByCode(KIVUYE), null, null, null).equals([value1])
		surveyValueService.getFormEnteredValues(DataLocationEntity.findByCode(KIVUYE), section1, null, null).equals([value1])
		surveyValueService.getFormEnteredValues(DataLocationEntity.findByCode(KIVUYE), section2, null, null).isEmpty()
		
		when:
		def value2 = newFormEnteredValue(element2, period, DataLocationEntity.findByCode(KIVUYE), Value.NULL_INSTANCE())
		
		then:
		surveyValueService.getFormEnteredValues(DataLocationEntity.findByCode(KIVUYE), null, null, null).equals([value1, value2])
		surveyValueService.getFormEnteredValues(DataLocationEntity.findByCode(KIVUYE), section1, null, null).equals([value1])
		surveyValueService.getFormEnteredValues(DataLocationEntity.findByCode(KIVUYE), section2, null, null).equals([value2])
	}
	
	
	def "get survey entered values for location - by program"() {
		setup:
		setupLocationTree()
		setupSecurityManager(newUser('test', 'uuid'))
		def period = newPeriod()
		def survey = newSurvey(period)
		
		def program1 = newSurveyProgram(survey, 1, [(HEALTH_CENTER_GROUP)])		
		def section1 = newSurveySection(program1, 1, [(HEALTH_CENTER_GROUP)])
		def question1 = newSimpleQuestion(section1, 1, [(HEALTH_CENTER_GROUP)])
		def element1 = newSurveyElement(question1, newRawDataElement(CODE(1), Type.TYPE_NUMBER()))
		
		def program2 = newSurveyProgram(survey, 1, [(HEALTH_CENTER_GROUP)])
		def section2 = newSurveySection(program2, 1, [(HEALTH_CENTER_GROUP)])
		def question2 = newSimpleQuestion(section2, 1, [(HEALTH_CENTER_GROUP)])
		def element2 = newSurveyElement(question2, newRawDataElement(CODE(2), Type.TYPE_NUMBER()))

		expect:
		surveyValueService.getFormEnteredValues(DataLocationEntity.findByCode(KIVUYE), null, null, null).isEmpty()
		
		when:
		def value1 = newFormEnteredValue(element1, period, DataLocationEntity.findByCode(KIVUYE), Value.NULL_INSTANCE())
		
		then:
		surveyValueService.getFormEnteredValues(DataLocationEntity.findByCode(KIVUYE), null, null, null).equals([value1])
		surveyValueService.getFormEnteredValues(DataLocationEntity.findByCode(KIVUYE), null, program1, null).equals([value1])
		surveyValueService.getFormEnteredValues(DataLocationEntity.findByCode(KIVUYE), null, program2, null).isEmpty()
		
		when:
		def value2 = newFormEnteredValue(element2, period, DataLocationEntity.findByCode(KIVUYE), Value.NULL_INSTANCE())
		
		then:
		surveyValueService.getFormEnteredValues(DataLocationEntity.findByCode(KIVUYE), null, null, null).equals([value1, value2])
		surveyValueService.getFormEnteredValues(DataLocationEntity.findByCode(KIVUYE), null, program1, null).equals([value1])
		surveyValueService.getFormEnteredValues(DataLocationEntity.findByCode(KIVUYE), null, program2, null).equals([value2])
	}
	
	def "get survey entered values for location - by survey"() {
		setup:
		setupLocationTree()
		setupSecurityManager(newUser('test', 'uuid'))
		def period1 = newPeriod()
		def period2 = newPeriod()
		
		def survey1 = newSurvey(period1)
		def program1 = newSurveyProgram(survey1, 1, [(HEALTH_CENTER_GROUP)])
		def section1 = newSurveySection(program1, 1, [(HEALTH_CENTER_GROUP)])
		def question1 = newSimpleQuestion(section1, 1, [(HEALTH_CENTER_GROUP)])
		def element1 = newSurveyElement(question1, newRawDataElement(CODE(1), Type.TYPE_NUMBER()))		
		
		def survey2 = newSurvey(period2)
		def program2 = newSurveyProgram(survey2, 1, [(HEALTH_CENTER_GROUP)])
		def section2 = newSurveySection(program2, 1, [(HEALTH_CENTER_GROUP)])
		def question2 = newSimpleQuestion(section2, 1, [(HEALTH_CENTER_GROUP)])
		def element2 = newSurveyElement(question2, newRawDataElement(CODE(2), Type.TYPE_NUMBER()))

		expect:
		surveyValueService.getFormEnteredValues(DataLocationEntity.findByCode(KIVUYE), null, null, null).isEmpty()
		
		when:
		def value1 = newFormEnteredValue(element1, period1, DataLocationEntity.findByCode(KIVUYE), Value.NULL_INSTANCE())
		
		then:
		surveyValueService.getFormEnteredValues(DataLocationEntity.findByCode(KIVUYE), null, null, null).equals([value1])
		surveyValueService.getFormEnteredValues(DataLocationEntity.findByCode(KIVUYE), null, null, survey1).equals([value1])
		surveyValueService.getFormEnteredValues(DataLocationEntity.findByCode(KIVUYE), null, null, survey2).isEmpty()
		
		when:
		def value2 = newFormEnteredValue(element2, period2, DataLocationEntity.findByCode(KIVUYE), Value.NULL_INSTANCE())
		
		then:
		surveyValueService.getFormEnteredValues(DataLocationEntity.findByCode(KIVUYE), null, null, null).equals([value1, value2])
		surveyValueService.getFormEnteredValues(DataLocationEntity.findByCode(KIVUYE), null, null, survey1).equals([value1])
		surveyValueService.getFormEnteredValues(DataLocationEntity.findByCode(KIVUYE), null, null, survey2).equals([value2])
	}
}
