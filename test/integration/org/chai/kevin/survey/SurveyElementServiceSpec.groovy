package org.chai.kevin.survey

import org.chai.kevin.IntegrationTests
import org.chai.kevin.data.DataElement
import org.chai.kevin.data.Type;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.period.Period

class SurveyElementServiceSpec extends SurveyIntegrationTests {

	def surveyElementService;
	
	def "test number of organisation applicable with all groups"() {
		setup:
		setupOrganisationUnitTree()
		def period = newPeriod()
		def survey = newSurvey(period)
		def objective = null
		def section = null
		def question = null
		def element = null
		
		when:
		objective = newSurveyObjective(survey, 1, [(HEALTH_CENTER_GROUP)])
		section = newSurveySection(objective, 1, [(HEALTH_CENTER_GROUP)])
		question = newSimpleQuestion(section, 1, [(HEALTH_CENTER_GROUP)])

		element = newSurveyElement(question, newDataElement(CODE(1), Type.TYPE_NUMBER()))
		
		then:
		element.getOrganisationUnitGroupApplicable().equals(new HashSet([(HEALTH_CENTER_GROUP)]))
		surveyElementService.getNumberOfOrganisationUnitApplicable(element) == 1
	}
	
	def "test number of organisation applicable with empty group"() {
		setup:
		setupOrganisationUnitTree()
		def period = newPeriod()
		def survey = newSurvey(period)
		def objective = null
		def section = null
		def question = null
		def element = null
		
		when:
		objective = newSurveyObjective(survey, 1, [(HEALTH_CENTER_GROUP)])
		section = newSurveySection(objective, 1, [])
		question = newSimpleQuestion(section, 1, [(HEALTH_CENTER_GROUP)])

		element = newSurveyElement(question, newDataElement(CODE(1), Type.TYPE_NUMBER()))
	
		then:
		element.getOrganisationUnitGroupApplicable().equals(new HashSet([]))
		surveyElementService.getNumberOfOrganisationUnitApplicable(element) == 0
	}
	
	def "test retrieve skip rule - no rule"() {
		setup:
		def period = newPeriod()
		
		def survey = newSurvey(period)
		def objective = newSurveyObjective(survey, 1, [(HEALTH_CENTER_GROUP)])
		def section = newSurveySection(objective, 1, [(HEALTH_CENTER_GROUP)])
		def question = newSimpleQuestion(section, 1, [(HEALTH_CENTER_GROUP)])

		def element = newSurveyElement(question, newDataElement(CODE(1), Type.TYPE_NUMBER()))

		def list = null
		
		when:
		list = surveyElementService.searchSkipRules(element)
		
		then:
		list.isEmpty()
	}
		
	def "test retrieve skip rule - no element in expression"() {
		setup:
		def period = newPeriod()
		
		def survey = newSurvey(period)
		def objective = newSurveyObjective(survey, 1, [(HEALTH_CENTER_GROUP)])
		def section = newSurveySection(objective, 1, [(HEALTH_CENTER_GROUP)])
		def question = newSimpleQuestion(section, 1, [(HEALTH_CENTER_GROUP)])

		def element = newSurveyElement(question, newDataElement(CODE(1), Type.TYPE_NUMBER()))

		def list = null
		
		when:
		def rule1 = newSkipRule(survey, "1==1", [(element): ""], [])
		list = surveyElementService.searchSkipRules(element)
		
		then:
		list.isEmpty()
	}
		
	def "test retrieve skip rule - element in expression"() {
		setup:
		def period = newPeriod()
		
		def survey = newSurvey(period)
		def objective = newSurveyObjective(survey, 1, [(HEALTH_CENTER_GROUP)])
		def section = newSurveySection(objective, 1, [(HEALTH_CENTER_GROUP)])
		def question = newSimpleQuestion(section, 1, [(HEALTH_CENTER_GROUP)])

		def element = newSurveyElement(question, newDataElement(CODE(1), Type.TYPE_NUMBER()))

		def list = null
		
		when:
		def rule2 = newSkipRule(survey, "\$"+element.id+"==1", [(element): ""], [])
		list = surveyElementService.searchSkipRules(element)
		
		then:
		list.equals(new HashSet([rule2]))
	}
	
	def "test retrieve skip rule - several rules"() {
		setup:
		def period = newPeriod()
		
		def survey = newSurvey(period)
		def objective = newSurveyObjective(survey, 1, [(HEALTH_CENTER_GROUP)])
		def section = newSurveySection(objective, 1, [(HEALTH_CENTER_GROUP)])
		def question = newSimpleQuestion(section, 1, [(HEALTH_CENTER_GROUP)])
	
		def element = newSurveyElement(question, newDataElement(CODE(1), Type.TYPE_NUMBER()))
	
		def list = null
	
		when:
		def rule3 = newSkipRule(survey, "\$"+element.id+"0"+"==1", [(element): ""], [])
		def rule4 = newSkipRule(survey, "\$"+element.id+"==1", [(element): ""], [])
		list = surveyElementService.searchSkipRules(element)
		
		then:
		list.equals(new HashSet([rule4]))
	}
	
	def "test retrieve validation rules"() {
		setup:
		def period = newPeriod()
		
		def survey = newSurvey(period)
		def objective = newSurveyObjective(survey, 1, [(HEALTH_CENTER_GROUP)])
		def section = newSurveySection(objective, 1, [(HEALTH_CENTER_GROUP)])
		def question = newSimpleQuestion(section, 1, [(HEALTH_CENTER_GROUP)])

		def element = newSurveyElement(question, newDataElement(CODE(1), Type.TYPE_NUMBER()))

		def list = null
		
		when:
		list = surveyElementService.searchValidationRules(element, HEALTH_CENTER_GROUP)
		
		then:
		list.isEmpty()
		
		when:
		def rule1 = newSurveyValidationRule(element, "", [(HEALTH_CENTER_GROUP)], "\$"+element.id+"==1")
		list = surveyElementService.searchValidationRules(element, HEALTH_CENTER_GROUP)
		
		then:
		list.equals(new HashSet([rule1]))
		
		when:
		list = surveyElementService.searchValidationRules(element, DISTRICT_HOSPITAL_GROUP)
		
		then:
		list.isEmpty()
	}
	
	def "test retrieve validation rule - several rules"() {
		setup:
		def period = newPeriod()
		
		def survey = newSurvey(period)
		def objective = newSurveyObjective(survey, 1, [(HEALTH_CENTER_GROUP)])
		def section = newSurveySection(objective, 1, [(HEALTH_CENTER_GROUP)])
		def question = newSimpleQuestion(section, 1, [(HEALTH_CENTER_GROUP)])
	
		def element = newSurveyElement(question, newDataElement(CODE(1), Type.TYPE_NUMBER()))
	
		def list = null
	
		when:
		def rule3 = newSurveyValidationRule(element, "", [(HEALTH_CENTER_GROUP), (DISTRICT_HOSPITAL_GROUP)], "\$"+element.id+"0"+"==1")
		def rule4 = newSurveyValidationRule(element, "", [(HEALTH_CENTER_GROUP), (DISTRICT_HOSPITAL_GROUP)], "\$"+element.id+"==1")
		list = surveyElementService.searchValidationRules(element, HEALTH_CENTER_GROUP)
		
		then:
		list.equals(new HashSet([rule4]))
	}
	
	def "test get survey elements for data element"() {
		setup:
		def period = newPeriod()
		
		def survey = newSurvey(period)
		def objective = newSurveyObjective(survey, 1, [(HEALTH_CENTER_GROUP)])
		def section = newSurveySection(objective, 1, [(HEALTH_CENTER_GROUP)])
		def question = newSimpleQuestion(section, 1, [(HEALTH_CENTER_GROUP)])
		
		def dataElement = newDataElement(CODE(1), Type.TYPE_NUMBER())
		def element = newSurveyElement(question, dataElement)

		when:
		def surveyElements = surveyElementService.getSurveyElements(dataElement, null)
		
		then:
		surveyElements.size() == 1
		surveyElements.iterator().next().equals(element)
		
	}

	def "search survey elements"() {
		def period = newPeriod()
		
		def survey = newSurvey(period)
		def objective = newSurveyObjective(survey, 1, [(HEALTH_CENTER_GROUP)])
		def section = newSurveySection(objective, 1, [(HEALTH_CENTER_GROUP)])
		def question = newSimpleQuestion(section, 1, [(HEALTH_CENTER_GROUP)])
		
		def dataElement = newDataElement(j(["en": "element"]),CODE(1), Type.TYPE_NUMBER())
		def element = newSurveyElement(question, dataElement)

		def surveyElements = null
				
		when:
		surveyElements = surveyElementService.searchSurveyElements("ele", survey, [])
		
		then:
		surveyElements.equals([element])
		
		when:
		def survey2 = newSurvey(period)
		surveyElements = surveyElementService.searchSurveyElements("ele", survey2, [])
		
		then:
		surveyElements.isEmpty()
	}
	
}
