package org.chai.kevin.survey

import org.chai.kevin.LanguageService
import org.chai.kevin.data.Type;
import org.chai.kevin.location.DataLocationEntity;
import org.chai.kevin.survey.validation.SurveyEnteredObjective;
import org.chai.kevin.survey.validation.SurveyEnteredQuestion;
import org.chai.kevin.survey.validation.SurveyEnteredSection;
import org.chai.kevin.survey.validation.SurveyEnteredValue;
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
	
	def "test submit objective"() {
		setup:
		setupLocationTree()
		setupSecurityManager(newUser('test', 'uuid'))
		def period = newPeriod()
		def survey = newSurvey(period)
		newSurveyObjective(survey, 2, [(HEALTH_CENTER_GROUP)])
		def objective = newSurveyObjective(survey, 1, [(HEALTH_CENTER_GROUP)])
		def section = newSurveySection(objective, 1, [(HEALTH_CENTER_GROUP)])
		def question = newSimpleQuestion(section, 1, [(HEALTH_CENTER_GROUP)])
		def element = newSurveyElement(question, newRawDataElement(CODE(1), Type.TYPE_NUMBER()))
		
		when:
		newSurveyEnteredValue(element, period, DataLocationEntity.findByCode(KIVUYE), v("1"))
		newSurveyEnteredQuestion(question, period, DataLocationEntity.findByCode(KIVUYE), false, true)
		newSurveyEnteredSection(section, period, DataLocationEntity.findByCode(KIVUYE), false, true)
		newSurveyEnteredObjective(objective, period, DataLocationEntity.findByCode(KIVUYE), false, true, false)
				
		then:
		surveyPageService.submit(DataLocationEntity.findByCode(KIVUYE), objective) == true
	}
	
	def "test submit objective with skipped elemment"() {
		setup:
		setupLocationTree()
		setupSecurityManager(newUser('test', 'uuid'))
		def period = newPeriod()
		def survey = newSurvey(period)
		newSurveyObjective(survey, 2, [(HEALTH_CENTER_GROUP)])
		def objective = newSurveyObjective(survey, 1, [(HEALTH_CENTER_GROUP)])
		def section = newSurveySection(objective, 1, [(HEALTH_CENTER_GROUP)])
		def question = newSimpleQuestion(section, 1, [(HEALTH_CENTER_GROUP)])
		def element = newSurveyElement(question, newRawDataElement(CODE(1), Type.TYPE_MAP(["key1":Type.TYPE_NUMBER(),"key2":Type.TYPE_NUMBER()])))
		
		when:
		newSurveyEnteredValue(element, period, DataLocationEntity.findByCode(KIVUYE), new Value("{\"value\":[{\"map_value\":{\"skipped\":\"33\",\"value\":null},\"map_key\":\"key1\"},{\"map_value\":{\"value\":10},\"map_key\":\"key2\"}]}"))
		newSurveyEnteredQuestion(question, period, DataLocationEntity.findByCode(KIVUYE), false, true)
		newSurveyEnteredSection(section, period, DataLocationEntity.findByCode(KIVUYE), false, true)
		newSurveyEnteredObjective(objective, period, DataLocationEntity.findByCode(KIVUYE), false, true, false)
				
		then:
		surveyPageService.submit(DataLocationEntity.findByCode(KIVUYE), objective) == true
		RawDataElementValue.count() == 1
	}
	
	def "test modify"() {
		setup:
		setupLocationTree()
		setupSecurityManager(newUser('test', 'uuid'))
		def period = newPeriod()
		def survey = newSurvey(period)
		newSurveyObjective(survey, 2, [(HEALTH_CENTER_GROUP)])
		def objective = newSurveyObjective(survey, 1, [(HEALTH_CENTER_GROUP)])
		def section = newSurveySection(objective, 1, [(HEALTH_CENTER_GROUP)])
		def question = newSimpleQuestion(section, 1, [(HEALTH_CENTER_GROUP)])
		
		def element = newSurveyElement(question, newRawDataElement(CODE(1), Type.TYPE_NUMBER()))
		
		when:
		surveyPageService.modify(DataLocationEntity.findByCode(KIVUYE), objective, [element], [("elements["+element.id+"].value"): "10"])
		
		then:
		SurveyEnteredValue.count() == 1
		SurveyEnteredValue.list()[0].value.numberValue == 10
	}
	
	def "test modify with skipped question"() {
		setup:
		setupLocationTree()
		setupSecurityManager(newUser('test', 'uuid'))
		def period = newPeriod()
		def survey = newSurvey(period)
		newSurveyObjective(survey, 2, [(HEALTH_CENTER_GROUP)])
		def objective = newSurveyObjective(survey, 1, [(HEALTH_CENTER_GROUP)])
		def section = newSurveySection(objective, 1, [(HEALTH_CENTER_GROUP)])
		def question1 = newSimpleQuestion(section, 1, [(HEALTH_CENTER_GROUP)])
		def question2 = newSimpleQuestion(section, 2, [(HEALTH_CENTER_GROUP)])
		
		def element1 = newSurveyElement(question1, newRawDataElement(CODE(1), Type.TYPE_NUMBER()))
		def element2 = newSurveyElement(question2, newRawDataElement(CODE(2), Type.TYPE_NUMBER()))
		def skipRule = newSkipRule(survey, "\$"+element1.id+" == 1", [:], [question2])
		
		when:
		surveyPageService.modify(DataLocationEntity.findByCode(KIVUYE), objective, [element1], [("elements["+element1.id+"].value"): "1"])
		
		then:
		SurveyEnteredValue.count() == 2
		SurveyEnteredValue.list()[0].value.numberValue == 1
		SurveyEnteredQuestion.count() == 2
		SurveyEnteredQuestion.list()[0].getSkippedRules().equals(new HashSet([skipRule]))
	}
	
	
	def "test submit"() {
		setup:
		setupLocationTree()
		setupSecurityManager(newUser('test', 'uuid'))
		def period = newPeriod()
		def survey = newSurvey(period)
		newSurveyObjective(survey, 2, [(HEALTH_CENTER_GROUP)])
		def objective = newSurveyObjective(survey, 1, [(HEALTH_CENTER_GROUP)])
		def section = newSurveySection(objective, 1, [(HEALTH_CENTER_GROUP)])
		def question = newSimpleQuestion(section, 1, [(HEALTH_CENTER_GROUP)])
		
		def element = newSurveyElement(question, newRawDataElement(CODE(1), Type.TYPE_NUMBER()))
		
		when:
		newSurveyEnteredValue(element, period, DataLocationEntity.findByCode(KIVUYE), v("1"))
		newSurveyEnteredQuestion(question, period, DataLocationEntity.findByCode(KIVUYE), false, true);
		newSurveyEnteredSection(section, period, DataLocationEntity.findByCode(KIVUYE), false, true);
		newSurveyEnteredObjective(objective, period, DataLocationEntity.findByCode(KIVUYE), false, true, false);
		def submitted = surveyPageService.submit(DataLocationEntity.findByCode(KIVUYE), objective)
		
		then:
		submitted == true
		SurveyEnteredObjective.list()[0].closed == true
		RawDataElementValue.count() == 1
		RawDataElementValue.list()[0].value.equals(v("1"))
	}
	
	def "test warning"() {
		setup:
		setupLocationTree()
		setupSecurityManager(newUser('test', 'uuid'))
		def period = newPeriod()
		def survey = newSurvey(period)
		newSurveyObjective(survey, 2, [(HEALTH_CENTER_GROUP)])
		def objective = newSurveyObjective(survey, 1, [(HEALTH_CENTER_GROUP)])
		def section = newSurveySection(objective, 1, [(HEALTH_CENTER_GROUP)])
		def question = newSimpleQuestion(section, 1, [(HEALTH_CENTER_GROUP)])
		
		def element = newSurveyElement(question, newRawDataElement(CODE(1), Type.TYPE_NUMBER()))
		def rule = newSurveyValidationRule(element, "", [(HEALTH_CENTER_GROUP)], "\$"+element.id+" > 10", true, [])
		
		when:
		surveyPageService.modify(DataLocationEntity.findByCode(KIVUYE), objective, [element], [("elements["+element.id+"].value"): "5"])
		
		then:
		SurveyEnteredValue.count() == 1
		SurveyEnteredValue.list()[0].value.numberValue == 5
		SurveyEnteredValue.list()[0].value.getAttribute("invalid") == rule.id+""
		
		when:
		surveyPageService.modify(DataLocationEntity.findByCode(KIVUYE), objective, [element], [("elements["+element.id+"].value"): "5", ("elements["+element.id+"].value[warning]"): ""+rule.id])
		
		then:
		SurveyEnteredValue.count() == 1
		SurveyEnteredValue.list()[0].value.numberValue == 5
		SurveyEnteredValue.list()[0].value.getAttribute("invalid") == rule.id+""
		SurveyEnteredValue.list()[0].value.getAttribute("warning") == rule.id+""
		
		when:
		surveyPageService.modify(DataLocationEntity.findByCode(KIVUYE), objective, [element], [("elements["+element.id+"].value"): "4", ("elements["+element.id+"].value[warning]"): ""+rule.id])
		
		then:
		SurveyEnteredValue.count() == 1
		SurveyEnteredValue.list()[0].value.numberValue == 4
		SurveyEnteredValue.list()[0].value.getAttribute("invalid") == rule.id+""
	}
	
	def "test warning and invalid values"() {
		setup:
		setupLocationTree()
		setupSecurityManager(newUser('test', 'uuid'))
		def period = newPeriod()
		def survey = newSurvey(period)
		newSurveyObjective(survey, 2, [(HEALTH_CENTER_GROUP)])
		def objective = newSurveyObjective(survey, 1, [(HEALTH_CENTER_GROUP)])
		def section = newSurveySection(objective, 1, [(HEALTH_CENTER_GROUP)])
		def question = newSimpleQuestion(section, 1, [(HEALTH_CENTER_GROUP)])
		
		def element = newSurveyElement(question, newRawDataElement(CODE(1), Type.TYPE_NUMBER()))
		def rule1 = newSurveyValidationRule(element, "", [(HEALTH_CENTER_GROUP)], "\$"+element.id+" > 10", true, [])
		def rule2 = newSurveyValidationRule(element, "", [(HEALTH_CENTER_GROUP)], "\$"+element.id+" > 100")
		
		when:
		surveyPageService.modify(DataLocationEntity.findByCode(KIVUYE), objective, [element], [("elements["+element.id+"].value"): "5"])
		
		then:
		SurveyEnteredValue.count() == 1
		SurveyEnteredValue.list()[0].value.numberValue == 5
		SurveyEnteredValue.list()[0].value.getAttribute("invalid").contains(rule1.id+"")
		SurveyEnteredValue.list()[0].value.getAttribute("invalid").contains(rule2.id+"")
	}
	
	def "test modify does not touch unmodified values"() {
		setup:
		setupLocationTree()
		setupSecurityManager(newUser('test', 'uuid'))
		def period = newPeriod()
		def survey = newSurvey(period)
		newSurveyObjective(survey, 2, [(HEALTH_CENTER_GROUP)])
		def objective = newSurveyObjective(survey, 1, [(HEALTH_CENTER_GROUP)])
		def section = newSurveySection(objective, 1, [(HEALTH_CENTER_GROUP)])
		def question = newSimpleQuestion(section, 1, [(HEALTH_CENTER_GROUP)])
		
		def element = newSurveyElement(question, newRawDataElement(CODE(1), Type.TYPE_LIST(Type.TYPE_NUMBER())))
		
		when:
		surveyPageService.modify(DataLocationEntity.findByCode(KIVUYE), objective, [element], [("elements["+element.id+"].value"): ["[0]", "[1]"]])
		
		then:
		SurveyEnteredValue.count() == 1
		SurveyEnteredValue.list()[0].value.listValue.size() == 2
		SurveyEnteredValue.list()[0].value.listValue[0].isNull()
		SurveyEnteredValue.list()[0].value.listValue[1].isNull()
		
		when:
		surveyPageService.modify(DataLocationEntity.findByCode(KIVUYE), objective, [element], [("elements["+element.id+"].value[0]"): "5", ("elements["+element.id+"].value.indexes"): ["[0]", "[1]"]])
		
		then:
		SurveyEnteredValue.count() == 1
		SurveyEnteredValue.list()[0].value.listValue.size() == 2
		SurveyEnteredValue.list()[0].value.listValue[0].numberValue == 5
		SurveyEnteredValue.list()[0].value.listValue[1].isNull()
		
		when:
		surveyPageService.modify(DataLocationEntity.findByCode(KIVUYE), objective, [element], [("elements["+element.id+"].value[1]"): "10", ("elements["+element.id+"].value.indexes"): ["[0]", "[1]"]])
		
		then:
		SurveyEnteredValue.count() == 1
		SurveyEnteredValue.list()[0].value.listValue.size() == 2
		SurveyEnteredValue.list()[0].value.listValue[0].numberValue == 5
		SurveyEnteredValue.list()[0].value.listValue[1].numberValue == 10
	}

	def "test refresh without surveyelement"() {
		setup:
		setupLocationTree()
		setupSecurityManager(newUser('test', 'uuid'))
		def period = newPeriod()
		def survey = newSurvey(period)
		newSurveyObjective(survey, 2, [(HEALTH_CENTER_GROUP)])
		def objective = newSurveyObjective(survey, 1, [(HEALTH_CENTER_GROUP)])
		def section = newSurveySection(objective, 1, [(HEALTH_CENTER_GROUP)])
		def question = newSimpleQuestion(section, 1, [(HEALTH_CENTER_GROUP)])
		
		when:
		surveyPageService.refreshSectionForFacility(DataLocationEntity.findByCode(KIVUYE), section)
		
		then:
		SurveyEnteredValue.count() == 0
		SurveyEnteredQuestion.count() == 1
		SurveyEnteredSection.count() == 1
	}
	
	def "test refresh erases old values"() {
		setup:
		setupLocationTree()
		def period = newPeriod()
		def survey = newSurvey(period)
		def objective = newSurveyObjective(survey, 1, [(HEALTH_CENTER_GROUP),(DISTRICT_HOSPITAL_GROUP)])
		def section = newSurveySection(objective, 1, [(HEALTH_CENTER_GROUP),(DISTRICT_HOSPITAL_GROUP)])
		def question1 = newSimpleQuestion(section, 1, [(HEALTH_CENTER_GROUP)])
		def element1 = newSurveyElement(question1, newRawDataElement(CODE(1), Type.TYPE_NUMBER()))
		
		when:
		newSurveyEnteredValue(element1, period, DataLocationEntity.findByCode(KIVUYE), v("1"))
		surveyPageService.refreshSectionForFacility(DataLocationEntity.findByCode(KIVUYE), section)
		
		then:
		SurveyEnteredValue.list()[0].value.equals(Value.NULL_INSTANCE())
	}
	
	def "test refresh erases unused entered values"() {
		setup:
		setupLocationTree()
		def period = newPeriod()
		def survey = newSurvey(period)
		def objective = newSurveyObjective(survey, 1, [(HEALTH_CENTER_GROUP)])
		def section = newSurveySection(objective, 1, [(HEALTH_CENTER_GROUP)])
		def question1 = newSimpleQuestion(section, 1, [(HEALTH_CENTER_GROUP)])
		def element1 = newSurveyElement(question1, newRawDataElement(CODE(1), Type.TYPE_NUMBER()))
		
		when:
		newSurveyEnteredQuestion(question1, period, DataLocationEntity.findByCode(BUTARO), false, true)
		newSurveyEnteredValue(element1, period, DataLocationEntity.findByCode(BUTARO), v("1"))
		newSurveyEnteredSection(section, period, DataLocationEntity.findByCode(BUTARO), false, true)
		newSurveyEnteredObjective(objective, period, DataLocationEntity.findByCode(BUTARO), false, true, false)
		surveyPageService.refreshSurveyForFacility(DataLocationEntity.findByCode(BUTARO), survey, false)
		sessionFactory.currentSession.flush()
		
		then:
		SurveyEnteredQuestion.count() == 0
		SurveyEnteredValue.count() == 0
		SurveyEnteredSection.count() == 0
		SurveyEnteredObjective.count() == 0
		
	}
		
	def "test objective order"() {
		setup:
		setupLocationTree()
		setupSecurityManager(newUser('test', 'uuid'))
		def period = newPeriod()
		def survey = newSurvey(period)
		def objective1 = newSurveyObjective(survey, 2, [(HEALTH_CENTER_GROUP)])
		def objective2 = newSurveyObjective(survey, 1, [(HEALTH_CENTER_GROUP)])
		
		when:
		def surveyPage = surveyPageService.getSurveyPage(DataLocationEntity.findByCode(KIVUYE), survey)
		
		then:
		surveyPage.objectives.equals(objective2, objective1)
	}
	
	def "test checkbox option order"() {
		setup:
		setupLocationTree()
		setupSecurityManager(newUser('test', 'uuid'))
		def period = newPeriod()
		def survey = newSurvey(period)
		def objective = newSurveyObjective(survey, 1, [(HEALTH_CENTER_GROUP)])
		def section = newSurveySection(objective, 1, [(HEALTH_CENTER_GROUP)])
		def question = newCheckboxQuestion(section, 1, [(HEALTH_CENTER_GROUP)])
		def option1 = newCheckboxOption(question, 2, [(HEALTH_CENTER_GROUP)], null)
		def option2 = newCheckboxOption(question, 1, [(HEALTH_CENTER_GROUP)], null)
		
		when:
		def surveyPage = surveyPageService.getSurveyPage(DataLocationEntity.findByCode(KIVUYE), section)
		
		then:
		surveyPage.getOptions(question).equals([option2, option1])
	}
	
	def "test enum option order"() {
		setup:
		setupLocationTree()
		setupSecurityManager(newUser('test', 'uuid'))
		def period = newPeriod()
		def survey = newSurvey(period)
		def objective = newSurveyObjective(survey, 1, [(HEALTH_CENTER_GROUP)])
		def section = newSurveySection(objective, 1, [(HEALTH_CENTER_GROUP)])
		def question = newSimpleQuestion(section, 1, [(HEALTH_CENTER_GROUP)])
		def enume = newEnume(CODE(2))
		def option1 = newEnumOption(enume, v("1"), o(['en':2, 'fr':1]))
		def option2 = newEnumOption(enume, v("2"), o(['en':1, 'fr':2]))
		def dataElement = newRawDataElement(CODE(1), Type.TYPE_ENUM(enume.code))
		def surveyElement = newSurveyElement(question, dataElement)
		
		when:
		def surveyPage = surveyPageService.getSurveyPage(DataLocationEntity.findByCode(KIVUYE), section)
		
		then:
		surveyPage.getEnumOptions(enume).equals([option2, option1])
		
		when:
		surveyPageService.languageService = new LanguageService(){
			public String getCurrentLanguage() {
				return "fr";
			}
		}
		surveyPage = surveyPageService.getSurveyPage(DataLocationEntity.findByCode(KIVUYE), section)
		
		then:
		surveyPage.getEnumOptions(enume).equals([option1, option2])
	}
	
	
}
