package org.chai.kevin.survey

import org.chai.kevin.LanguageService
import org.chai.kevin.data.Type;
import org.chai.kevin.form.FormEnteredValue;
import org.chai.kevin.location.DataLocation;
import org.chai.kevin.survey.validation.SurveyEnteredProgram;
import org.chai.kevin.survey.validation.SurveyEnteredQuestion;
import org.chai.kevin.survey.validation.SurveyEnteredSection;
import org.chai.kevin.value.DataValue;
import org.chai.kevin.value.Value;
import org.chai.kevin.value.RawDataElementValue;

class SurveyPageServiceSpec extends SurveyIntegrationTests {

	def surveyPageService
	def languageService
	
	def sessionFactory
	
	protected void tearDown() {
		super.tearDown()
		surveyPageService.languageService = languageService
	}
	
	def "test submit program"() {
		setup:
		setupLocationTree()
		setupSecurityManager(newUser('test', 'uuid'))
		def period = newPeriod()
		def survey = newSurvey(period)
		newSurveyProgram(survey, 2, [(HEALTH_CENTER_GROUP)])
		def program = newSurveyProgram(survey, 1, [(HEALTH_CENTER_GROUP)])
		def section = newSurveySection(program, 1, [(HEALTH_CENTER_GROUP)])
		def question = newSimpleQuestion(section, 1, [(HEALTH_CENTER_GROUP)])
		def element = newSurveyElement(question, newRawDataElement(CODE(1), Type.TYPE_NUMBER()))
		
		when:
		newFormEnteredValue(element, period, DataLocation.findByCode(KIVUYE), v("1"))
		newSurveyEnteredQuestion(question, period, DataLocation.findByCode(KIVUYE), false, true)
		newSurveyEnteredSection(section, period, DataLocation.findByCode(KIVUYE), false, true)
		newSurveyEnteredProgram(program, period, DataLocation.findByCode(KIVUYE), false, true, false)
				
		then:
		surveyPageService.submit(DataLocation.findByCode(KIVUYE), program) == true
	}
	
	def "test submit program with skipped elemment"() {
		setup:
		setupLocationTree()
		setupSecurityManager(newUser('test', 'uuid'))
		def period = newPeriod()
		def survey = newSurvey(period)
		newSurveyProgram(survey, 2, [(HEALTH_CENTER_GROUP)])
		def program = newSurveyProgram(survey, 1, [(HEALTH_CENTER_GROUP)])
		def section = newSurveySection(program, 1, [(HEALTH_CENTER_GROUP)])
		def question = newSimpleQuestion(section, 1, [(HEALTH_CENTER_GROUP)])
		def element = newSurveyElement(question, newRawDataElement(CODE(1), Type.TYPE_MAP(["key1":Type.TYPE_NUMBER(),"key2":Type.TYPE_NUMBER()])))
		
		when:
		newFormEnteredValue(element, period, DataLocation.findByCode(KIVUYE), new Value("{\"value\":[{\"map_value\":{\"skipped\":\"33\",\"value\":null},\"map_key\":\"key1\"},{\"map_value\":{\"value\":10},\"map_key\":\"key2\"}]}"))
		newSurveyEnteredQuestion(question, period, DataLocation.findByCode(KIVUYE), false, true)
		newSurveyEnteredSection(section, period, DataLocation.findByCode(KIVUYE), false, true)
		newSurveyEnteredProgram(program, period, DataLocation.findByCode(KIVUYE), false, true, false)
				
		then:
		surveyPageService.submit(DataLocation.findByCode(KIVUYE), program) == true
		RawDataElementValue.count() == 1
	}
	
	def "test modify"() {
		setup:
		setupLocationTree()
		setupSecurityManager(newUser('test', 'uuid'))
		def period = newPeriod()
		def survey = newSurvey(period)
		newSurveyProgram(survey, 2, [(HEALTH_CENTER_GROUP)])
		def program = newSurveyProgram(survey, 1, [(HEALTH_CENTER_GROUP)])
		def section = newSurveySection(program, 1, [(HEALTH_CENTER_GROUP)])
		def question = newSimpleQuestion(section, 1, [(HEALTH_CENTER_GROUP)])
		
		def element = newSurveyElement(question, newRawDataElement(CODE(1), Type.TYPE_NUMBER()))
		
		when:
		surveyPageService.modify(DataLocation.findByCode(KIVUYE), program, [element], [("elements["+element.id+"].value"): "10"])
		
		then:
		FormEnteredValue.count() == 1
		FormEnteredValue.list()[0].value.numberValue == 10
	}
	
	def "test modify with skipped question"() {
		setup:
		setupLocationTree()
		setupSecurityManager(newUser('test', 'uuid'))
		def period = newPeriod()
		def survey = newSurvey(period)
		newSurveyProgram(survey, 2, [(HEALTH_CENTER_GROUP)])
		def program = newSurveyProgram(survey, 1, [(HEALTH_CENTER_GROUP)])
		def section = newSurveySection(program, 1, [(HEALTH_CENTER_GROUP)])
		def question1 = newSimpleQuestion(section, 1, [(HEALTH_CENTER_GROUP)])
		def question2 = newSimpleQuestion(section, 2, [(HEALTH_CENTER_GROUP)])
		
		def element1 = newSurveyElement(question1, newRawDataElement(CODE(1), Type.TYPE_NUMBER()))
		def element2 = newSurveyElement(question2, newRawDataElement(CODE(2), Type.TYPE_NUMBER()))
		def skipRule = newSurveySkipRule(survey, "\$"+element1.id+" == 1", [:], [question2])
		
		when:
		surveyPageService.modify(DataLocation.findByCode(KIVUYE), program, [element1], [("elements["+element1.id+"].value"): "1"])
		
		then:
		FormEnteredValue.count() == 1
		FormEnteredValue.list()[0].value.numberValue == 1
		SurveyEnteredQuestion.count() == 2
		SurveyEnteredQuestion.list().find {it.question.equals(question2)}.skippedRules.equals(new HashSet([skipRule]))
	}
	
	def "test modify with skipped question referring to non existing element"() {
		setup:
		setupLocationTree()
		setupSecurityManager(newUser('test', 'uuid'))
		def period = newPeriod()
		def survey = newSurvey(period)
		newSurveyProgram(survey, 2, [(HEALTH_CENTER_GROUP)])
		def program = newSurveyProgram(survey, 1, [(HEALTH_CENTER_GROUP)])
		def section = newSurveySection(program, 1, [(HEALTH_CENTER_GROUP)])
		def question1 = newSimpleQuestion(section, 1, [(HEALTH_CENTER_GROUP)])
		def question2 = newSimpleQuestion(section, 2, [(HEALTH_CENTER_GROUP)])
		
		def element1 = newSurveyElement(question1, newRawDataElement(CODE(1), Type.TYPE_NUMBER()))
		def element2 = newSurveyElement(question2, newRawDataElement(CODE(2), Type.TYPE_NUMBER()))
		def skipRule = newSurveySkipRule(survey, "\$"+element1.id+" == 1", [(element2):""], [])
		
		when:
		surveyPageService.modify(DataLocation.findByCode(KIVUYE), program, [element1], [("elements["+element1.id+"].value"): "1"])
		
		then:
		FormEnteredValue.count() == 2
		FormEnteredValue.list()[0].value.numberValue == 1
		FormEnteredValue.list()[1].validatable.isSkipped("") == true
		SurveyEnteredQuestion.count() == 2
	}
	
	
	def "test submit"() {
		setup:
		setupLocationTree()
		setupSecurityManager(newUser('test', 'uuid'))
		def period = newPeriod()
		def survey = newSurvey(period)
		newSurveyProgram(survey, 2, [(HEALTH_CENTER_GROUP)])
		def program = newSurveyProgram(survey, 1, [(HEALTH_CENTER_GROUP)])
		def section = newSurveySection(program, 1, [(HEALTH_CENTER_GROUP)])
		def question = newSimpleQuestion(section, 1, [(HEALTH_CENTER_GROUP)])
		
		def element = newSurveyElement(question, newRawDataElement(CODE(1), Type.TYPE_NUMBER()))
		
		when:
		newFormEnteredValue(element, period, DataLocation.findByCode(KIVUYE), v("1"))
		newSurveyEnteredQuestion(question, period, DataLocation.findByCode(KIVUYE), false, true);
		newSurveyEnteredSection(section, period, DataLocation.findByCode(KIVUYE), false, true);
		newSurveyEnteredProgram(program, period, DataLocation.findByCode(KIVUYE), false, true, false);
		def submitted = surveyPageService.submit(DataLocation.findByCode(KIVUYE), program)
		
		then:
		submitted == true
		SurveyEnteredProgram.list()[0].closed == true
		RawDataElementValue.count() == 1
		RawDataElementValue.list()[0].value.equals(v("1"))
	}
	
	def "test warning"() {
		setup:
		setupLocationTree()
		setupSecurityManager(newUser('test', 'uuid'))
		def period = newPeriod()
		def survey = newSurvey(period)
		newSurveyProgram(survey, 2, [(HEALTH_CENTER_GROUP)])
		def program = newSurveyProgram(survey, 1, [(HEALTH_CENTER_GROUP)])
		def section = newSurveySection(program, 1, [(HEALTH_CENTER_GROUP)])
		def question = newSimpleQuestion(section, 1, [(HEALTH_CENTER_GROUP)])
		
		def element = newSurveyElement(question, newRawDataElement(CODE(1), Type.TYPE_NUMBER()))
		def rule = newFormValidationRule(element, "", [(HEALTH_CENTER_GROUP)], "\$"+element.id+" > 10", true, [])
		
		when:
		surveyPageService.modify(DataLocation.findByCode(KIVUYE), program, [element], [("elements["+element.id+"].value"): "5"])
		
		then:
		FormEnteredValue.count() == 1
		FormEnteredValue.list()[0].value.numberValue == 5
		FormEnteredValue.list()[0].value.getAttribute("invalid") == rule.id+""
		
		when:
		surveyPageService.modify(DataLocation.findByCode(KIVUYE), program, [element], [("elements["+element.id+"].value"): "5", ("elements["+element.id+"].value[warning]"): ""+rule.id])
		
		then:
		FormEnteredValue.count() == 1
		FormEnteredValue.list()[0].value.numberValue == 5
		FormEnteredValue.list()[0].value.getAttribute("invalid") == rule.id+""
		FormEnteredValue.list()[0].value.getAttribute("warning") == rule.id+""
		
		when:
		surveyPageService.modify(DataLocation.findByCode(KIVUYE), program, [element], [("elements["+element.id+"].value"): "4", ("elements["+element.id+"].value[warning]"): ""+rule.id])
		
		then:
		FormEnteredValue.count() == 1
		FormEnteredValue.list()[0].value.numberValue == 4
		FormEnteredValue.list()[0].value.getAttribute("invalid") == rule.id+""
	}
	
	def "test warning and invalid values"() {
		setup:
		setupLocationTree()
		setupSecurityManager(newUser('test', 'uuid'))
		def period = newPeriod()
		def survey = newSurvey(period)
		newSurveyProgram(survey, 2, [(HEALTH_CENTER_GROUP)])
		def program = newSurveyProgram(survey, 1, [(HEALTH_CENTER_GROUP)])
		def section = newSurveySection(program, 1, [(HEALTH_CENTER_GROUP)])
		def question = newSimpleQuestion(section, 1, [(HEALTH_CENTER_GROUP)])
		
		def element = newSurveyElement(question, newRawDataElement(CODE(1), Type.TYPE_NUMBER()))
		def rule1 = newFormValidationRule(element, "", [(HEALTH_CENTER_GROUP)], "\$"+element.id+" > 10", true, [])
		def rule2 = newFormValidationRule(element, "", [(HEALTH_CENTER_GROUP)], "\$"+element.id+" > 100")
		
		when:
		surveyPageService.modify(DataLocation.findByCode(KIVUYE), program, [element], [("elements["+element.id+"].value"): "5"])
		
		then:
		FormEnteredValue.count() == 1
		FormEnteredValue.list()[0].value.numberValue == 5
		FormEnteredValue.list()[0].value.getAttribute("invalid").contains(rule1.id+"")
		FormEnteredValue.list()[0].value.getAttribute("invalid").contains(rule2.id+"")
	}
	
	def "test modify does not touch unmodified values"() {
		setup:
		setupLocationTree()
		setupSecurityManager(newUser('test', 'uuid'))
		def period = newPeriod()
		def survey = newSurvey(period)
		newSurveyProgram(survey, 2, [(HEALTH_CENTER_GROUP)])
		def program = newSurveyProgram(survey, 1, [(HEALTH_CENTER_GROUP)])
		def section = newSurveySection(program, 1, [(HEALTH_CENTER_GROUP)])
		def question = newSimpleQuestion(section, 1, [(HEALTH_CENTER_GROUP)])
		
		def element = newSurveyElement(question, newRawDataElement(CODE(1), Type.TYPE_LIST(Type.TYPE_NUMBER())))
		
		when:
		surveyPageService.modify(DataLocation.findByCode(KIVUYE), program, [element], [("elements["+element.id+"].value"): ["[0]", "[1]"]])
		
		then:
		FormEnteredValue.count() == 1
		FormEnteredValue.list()[0].value.listValue.size() == 2
		FormEnteredValue.list()[0].value.listValue[0].isNull()
		FormEnteredValue.list()[0].value.listValue[1].isNull()
		
		when:
		surveyPageService.modify(DataLocation.findByCode(KIVUYE), program, [element], [("elements["+element.id+"].value[0]"): "5", ("elements["+element.id+"].value.indexes"): ["[0]", "[1]"]])
		
		then:
		FormEnteredValue.count() == 1
		FormEnteredValue.list()[0].value.listValue.size() == 2
		FormEnteredValue.list()[0].value.listValue[0].numberValue == 5
		FormEnteredValue.list()[0].value.listValue[1].isNull()
		
		when:
		surveyPageService.modify(DataLocation.findByCode(KIVUYE), program, [element], [("elements["+element.id+"].value[1]"): "10", ("elements["+element.id+"].value.indexes"): ["[0]", "[1]"]])
		
		then:
		FormEnteredValue.count() == 1
		FormEnteredValue.list()[0].value.listValue.size() == 2
		FormEnteredValue.list()[0].value.listValue[0].numberValue == 5
		FormEnteredValue.list()[0].value.listValue[1].numberValue == 10
	}

	def "test refresh without surveyelement"() {
		setup:
		setupLocationTree()
		setupSecurityManager(newUser('test', 'uuid'))
		def period = newPeriod()
		def survey = newSurvey(period)
		newSurveyProgram(survey, 2, [(HEALTH_CENTER_GROUP)])
		def program = newSurveyProgram(survey, 1, [(HEALTH_CENTER_GROUP)])
		def section = newSurveySection(program, 1, [(HEALTH_CENTER_GROUP)])
		def question = newSimpleQuestion(section, 1, [(HEALTH_CENTER_GROUP)])
		
		when:
		surveyPageService.refreshSectionForDataLocation(DataLocation.findByCode(KIVUYE), section)
		
		then:
		FormEnteredValue.count() == 0
		SurveyEnteredQuestion.count() == 1
		SurveyEnteredSection.count() == 1
	}
	
	def "test refresh erases old values"() {
		setup:
		setupLocationTree()
		def period = newPeriod()
		def survey = newSurvey(period)
		def program = newSurveyProgram(survey, 1, [(HEALTH_CENTER_GROUP),(DISTRICT_HOSPITAL_GROUP)])
		def section = newSurveySection(program, 1, [(HEALTH_CENTER_GROUP),(DISTRICT_HOSPITAL_GROUP)])
		def question1 = newSimpleQuestion(section, 1, [(HEALTH_CENTER_GROUP)])
		def element1 = newSurveyElement(question1, newRawDataElement(CODE(1), Type.TYPE_NUMBER()))
		
		when:
		newFormEnteredValue(element1, period, DataLocation.findByCode(KIVUYE), v("1"))
		surveyPageService.refreshSectionForDataLocation(DataLocation.findByCode(KIVUYE), section)
		
		then:
		FormEnteredValue.list()[0].value.equals(Value.NULL_INSTANCE())
	}
	
	def "test refresh erases unused entered values"() {
		setup:
		setupLocationTree()
		def period = newPeriod()
		def survey = newSurvey(period)
		def program = newSurveyProgram(survey, 1, [(HEALTH_CENTER_GROUP)])
		def section = newSurveySection(program, 1, [(HEALTH_CENTER_GROUP)])
		def question1 = newSimpleQuestion(section, 1, [(HEALTH_CENTER_GROUP)])
		def element1 = newSurveyElement(question1, newRawDataElement(CODE(1), Type.TYPE_NUMBER()))
		
		when:
		newSurveyEnteredQuestion(question1, period, DataLocation.findByCode(BUTARO), false, true)
		newFormEnteredValue(element1, period, DataLocation.findByCode(BUTARO), v("1"))
		newSurveyEnteredSection(section, period, DataLocation.findByCode(BUTARO), false, true)
		newSurveyEnteredProgram(program, period, DataLocation.findByCode(BUTARO), false, true, false)
		surveyPageService.refreshSurveyForDataLocation(DataLocation.findByCode(BUTARO), survey, false)
		sessionFactory.currentSession.flush()
		
		then:
		SurveyEnteredQuestion.count() == 0
		FormEnteredValue.count() == 0
		SurveyEnteredSection.count() == 0
		SurveyEnteredProgram.count() == 0
		
	}
		
	def "test program order"() {
		setup:
		setupLocationTree()
		setupSecurityManager(newUser('test', 'uuid'))
		def period = newPeriod()
		def survey = newSurvey(period)
		def program1 = newSurveyProgram(survey, 2, [(HEALTH_CENTER_GROUP)])
		def program2 = newSurveyProgram(survey, 1, [(HEALTH_CENTER_GROUP)])
		
		when:
		def surveyPage = surveyPageService.getSurveyPage(DataLocation.findByCode(KIVUYE), survey)
		
		then:
		surveyPage.programs.equals(program2, program1)
	}
	
	def "test checkbox option order"() {
		setup:
		setupLocationTree()
		setupSecurityManager(newUser('test', 'uuid'))
		def period = newPeriod()
		def survey = newSurvey(period)
		def program = newSurveyProgram(survey, 1, [(HEALTH_CENTER_GROUP)])
		def section = newSurveySection(program, 1, [(HEALTH_CENTER_GROUP)])
		def question = newCheckboxQuestion(section, 1, [(HEALTH_CENTER_GROUP)])
		def option1 = newCheckboxOption(question, 2, [(HEALTH_CENTER_GROUP)], null)
		def option2 = newCheckboxOption(question, 1, [(HEALTH_CENTER_GROUP)], null)
		
		when:
		def surveyPage = surveyPageService.getSurveyPage(DataLocation.findByCode(KIVUYE), section)
		
		then:
		surveyPage.getOptions(question).equals([option2, option1])
	}
	
}
