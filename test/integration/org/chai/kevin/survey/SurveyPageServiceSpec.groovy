package org.chai.kevin.survey

import org.chai.kevin.data.Type;
import org.chai.kevin.survey.validation.SurveyEnteredValue;
import org.hisp.dhis.organisationunit.OrganisationUnit;

class SurveyPageServiceSpec extends SurveyIntegrationTests {

	def surveyPageService
	
	def "test submit objective"() {
		setup:
		setupOrganisationUnitTree()
		def period = newPeriod()
		def survey = newSurvey(period)
		newSurveyObjective(survey, 2, [(HEALTH_CENTER_GROUP)])
		def objective = newSurveyObjective(survey, 1, [(HEALTH_CENTER_GROUP)])
		def section = newSurveySection(objective, 1, [(HEALTH_CENTER_GROUP)])
		def question = newSimpleQuestion(section, 1, [(HEALTH_CENTER_GROUP)])
		def element = newSurveyElement(question, newDataElement(CODE(1), Type.TYPE_NUMBER()))
		
		when:
		newSurveyEnteredValue(element, period, OrganisationUnit.findByName(KIVUYE), v("1"))
		newSurveyEnteredQuestion(question, period, OrganisationUnit.findByName(KIVUYE), false, true)
		newSurveyEnteredSection(section, period, OrganisationUnit.findByName(KIVUYE), false, true)
		newSurveyEnteredObjective(objective, period, OrganisationUnit.findByName(KIVUYE), false, true, false)
				
		then:
		surveyPageService.submit(getOrganisation(KIVUYE), objective) == true
	}
	
	def "test modify"() {
		setup:
		setupOrganisationUnitTree()
		def period = newPeriod()
		def survey = newSurvey(period)
		newSurveyObjective(survey, 2, [(HEALTH_CENTER_GROUP)])
		def objective = newSurveyObjective(survey, 1, [(HEALTH_CENTER_GROUP)])
		def section = newSurveySection(objective, 1, [(HEALTH_CENTER_GROUP)])
		def question = newSimpleQuestion(section, 1, [(HEALTH_CENTER_GROUP)])
		
		def element = newSurveyElement(question, newDataElement(CODE(1), Type.TYPE_NUMBER()))
		
		when:
		surveyPageService.modify(getOrganisation(KIVUYE), objective, [element], [("surveyElements["+element.id+"].value"): "10"])
		
		then:
		SurveyEnteredValue.count() == 1
		SurveyEnteredValue.list()[0].value.numberValue == 10
	}
	
	def "test warning"() {
		setup:
		setupOrganisationUnitTree()
		def period = newPeriod()
		def survey = newSurvey(period)
		newSurveyObjective(survey, 2, [(HEALTH_CENTER_GROUP)])
		def objective = newSurveyObjective(survey, 1, [(HEALTH_CENTER_GROUP)])
		def section = newSurveySection(objective, 1, [(HEALTH_CENTER_GROUP)])
		def question = newSimpleQuestion(section, 1, [(HEALTH_CENTER_GROUP)])
		
		def element = newSurveyElement(question, newDataElement(CODE(1), Type.TYPE_NUMBER()))
		def rule = newSurveyValidationRule(element, "", [(HEALTH_CENTER_GROUP)], "\$"+element.id+" > 10", true, [])
		
		when:
		surveyPageService.modify(getOrganisation(KIVUYE), objective, [element], [("surveyElements["+element.id+"].value"): "5"])
		
		then:
		SurveyEnteredValue.count() == 1
		SurveyEnteredValue.list()[0].value.numberValue == 5
		SurveyEnteredValue.list()[0].value.getAttribute("invalid") == rule.id+""
		
		when:
		surveyPageService.modify(getOrganisation(KIVUYE), objective, [element], [("surveyElements["+element.id+"].value"): "5", ("surveyElements["+element.id+"].value[warning]"): ""+rule.id])
		
		then:
		SurveyEnteredValue.count() == 1
		SurveyEnteredValue.list()[0].value.numberValue == 5
		SurveyEnteredValue.list()[0].value.getAttribute("invalid") == rule.id+""
		SurveyEnteredValue.list()[0].value.getAttribute("warning") == rule.id+""
		
		when:
		surveyPageService.modify(getOrganisation(KIVUYE), objective, [element], [("surveyElements["+element.id+"].value"): "4", ("surveyElements["+element.id+"].value[warning]"): ""+rule.id])
		
		then:
		SurveyEnteredValue.count() == 1
		SurveyEnteredValue.list()[0].value.numberValue == 4
		SurveyEnteredValue.list()[0].value.getAttribute("invalid") == rule.id+""
	}
	
	def "test warning and invalid values"() {
		setup:
		setupOrganisationUnitTree()
		def period = newPeriod()
		def survey = newSurvey(period)
		newSurveyObjective(survey, 2, [(HEALTH_CENTER_GROUP)])
		def objective = newSurveyObjective(survey, 1, [(HEALTH_CENTER_GROUP)])
		def section = newSurveySection(objective, 1, [(HEALTH_CENTER_GROUP)])
		def question = newSimpleQuestion(section, 1, [(HEALTH_CENTER_GROUP)])
		
		def element = newSurveyElement(question, newDataElement(CODE(1), Type.TYPE_NUMBER()))
		def rule1 = newSurveyValidationRule(element, "", [(HEALTH_CENTER_GROUP)], "\$"+element.id+" > 10", true, [])
		def rule2 = newSurveyValidationRule(element, "", [(HEALTH_CENTER_GROUP)], "\$"+element.id+" > 100")
		
		when:
		surveyPageService.modify(getOrganisation(KIVUYE), objective, [element], [("surveyElements["+element.id+"].value"): "5"])
		
		then:
		SurveyEnteredValue.count() == 1
		SurveyEnteredValue.list()[0].value.numberValue == 5
		SurveyEnteredValue.list()[0].value.getAttribute("invalid").contains(rule1.id)
		SurveyEnteredValue.list()[0].value.getAttribute("invalid").contains(rule2.id)
		
	}
	
	def "test modify does not touch unmodified values"() {
		setup:
		setupOrganisationUnitTree()
		def period = newPeriod()
		def survey = newSurvey(period)
		newSurveyObjective(survey, 2, [(HEALTH_CENTER_GROUP)])
		def objective = newSurveyObjective(survey, 1, [(HEALTH_CENTER_GROUP)])
		def section = newSurveySection(objective, 1, [(HEALTH_CENTER_GROUP)])
		def question = newSimpleQuestion(section, 1, [(HEALTH_CENTER_GROUP)])
		
		def element = newSurveyElement(question, newDataElement(CODE(1), Type.TYPE_LIST(Type.TYPE_NUMBER())))
		
		when:
		surveyPageService.modify(getOrganisation(KIVUYE), objective, [element], [("surveyElements["+element.id+"].value"): ["0", "1"]])
		
		then:
		SurveyEnteredValue.count() == 1
		SurveyEnteredValue.list()[0].value.listValue.size() == 2
		SurveyEnteredValue.list()[0].value.listValue[0].isNull()
		SurveyEnteredValue.list()[0].value.listValue[1].isNull()
		when:
		surveyPageService.modify(getOrganisation(KIVUYE), objective, [element], [("surveyElements["+element.id+"].value[0]"): "5"])
		
		then:
		SurveyEnteredValue.count() == 1
		SurveyEnteredValue.list()[0].value.listValue.size() == 2
		SurveyEnteredValue.list()[0].value.listValue[0].numberValue == 5
		SurveyEnteredValue.list()[0].value.listValue[1].isNull()
		
		when:
		surveyPageService.modify(getOrganisation(KIVUYE), objective, [element], [("surveyElements["+element.id+"].value[1]"): "10"])
		
		then:
		SurveyEnteredValue.count() == 1
		SurveyEnteredValue.list()[0].value.listValue.size() == 2
		SurveyEnteredValue.list()[0].value.listValue[0].numberValue == 5
		SurveyEnteredValue.list()[0].value.listValue[1].numberValue == 10

	}
	
}
