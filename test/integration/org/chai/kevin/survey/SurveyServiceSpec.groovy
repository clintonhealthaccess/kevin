package org.chai.kevin.survey

import org.chai.kevin.data.Type;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;

class SurveyServiceSpec extends SurveyIntegrationTests {

	def surveyService
	
	def "get survey question"() {
		setup:
		def period = newPeriod()
		def survey = newSurvey(period)
		def objective = newSurveyObjective(survey, 1, [(HEALTH_CENTER_GROUP)])
		def section = newSurveySection(objective, 1, [(HEALTH_CENTER_GROUP)])
		def question1 = newSimpleQuestion(["en": "question"], section, 1, [(HEALTH_CENTER_GROUP)])
		def question2 = newSimpleQuestion(["en": "somethig"], section, 2, [(HEALTH_CENTER_GROUP)])
		
		expect:
		surveyService.getSurveyQuestion(question1.id).equals(question1)
		surveyService.getSurveyQuestion(question2.id).equals(question2)
	}
	
	def "search question test"() {
		setup:
		def period = newPeriod() 
		def survey = newSurvey(period)
		def objective = newSurveyObjective(survey, 1, [(HEALTH_CENTER_GROUP)])
		def section = newSurveySection(objective, 1, [(HEALTH_CENTER_GROUP)])
		def question1 = newSimpleQuestion(["en": "question"], section, 1, [(HEALTH_CENTER_GROUP)])
		def question2 = newSimpleQuestion(["en": "somethig"], section, 2, [(HEALTH_CENTER_GROUP)])
		
		expect:
		surveyService.searchSurveyQuestions("que", survey).equals([question1])
		surveyService.searchSurveyQuestions("que some", survey).equals([])
	}
	
	def "search question - paging works"() {
		setup:
		def period = newPeriod()
		def survey = newSurvey(period)
		def objective = newSurveyObjective(survey, 1, [(HEALTH_CENTER_GROUP)])
		def section = newSurveySection(objective, 1, [(HEALTH_CENTER_GROUP)])
		def question1 = newSimpleQuestion(["en": "question"], section, 1, [(HEALTH_CENTER_GROUP)])
		def question2 = newSimpleQuestion(["en": "somethig"], section, 2, [(HEALTH_CENTER_GROUP)])
		
		expect:
		surveyService.searchSurveyQuestions("", survey, [offset: 0, max:1]).equals([question1])
		surveyService.searchSurveyQuestions("", survey, [offset: 1, max:1]).equals([question2])
	}
		

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

		element = newSurveyElement(question, newRawDataElement(CODE(1), Type.TYPE_NUMBER()))
		
		then:
		element.getOrganisationUnitGroupApplicable().equals(new HashSet([(HEALTH_CENTER_GROUP)]))
		surveyService.getNumberOfOrganisationUnitApplicable(element) == 1
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

		element = newSurveyElement(question, newRawDataElement(CODE(1), Type.TYPE_NUMBER()))
	
		then:
		element.getOrganisationUnitGroupApplicable().equals(new HashSet([]))
		surveyService.getNumberOfOrganisationUnitApplicable(element) == 0
	}
	
	def "test retrieve skip rule - no rule"() {
		setup:
		def period = newPeriod()
		
		def survey = newSurvey(period)
		def objective = newSurveyObjective(survey, 1, [(HEALTH_CENTER_GROUP)])
		def section = newSurveySection(objective, 1, [(HEALTH_CENTER_GROUP)])
		def question = newSimpleQuestion(section, 1, [(HEALTH_CENTER_GROUP)])

		def element = newSurveyElement(question, newRawDataElement(CODE(1), Type.TYPE_NUMBER()))

		def list = null
		
		when:
		list = surveyService.searchSkipRules(element)
		
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

		def element = newSurveyElement(question, newRawDataElement(CODE(1), Type.TYPE_NUMBER()))

		def list = null
		
		when:
		def rule1 = newSkipRule(survey, "1==1", [(element): ""], [])
		list = surveyService.searchSkipRules(element)
		
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

		def element = newSurveyElement(question, newRawDataElement(CODE(1), Type.TYPE_NUMBER()))

		def list = null
		
		when:
		def rule2 = newSkipRule(survey, "\$"+element.id+"==1", [(element): ""], [])
		list = surveyService.searchSkipRules(element)
		
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
	
		def element = newSurveyElement(question, newRawDataElement(CODE(1), Type.TYPE_NUMBER()))
	
		def list = null
	
		when:
		def rule3 = newSkipRule(survey, "\$"+element.id+"0"+"==1", [(element): ""], [])
		def rule4 = newSkipRule(survey, "\$"+element.id+"==1", [(element): ""], [])
		list = surveyService.searchSkipRules(element)
		
		then:
		list.equals(new HashSet([rule4]))
	}
	
	def "test retrieve validation rules"() {
		setup:
		def set = newOrganisationUnitGroupSet(GROUP_SET_TYPE);
		def hc = newOrganisationUnitGroup(HEALTH_CENTER_GROUP, set);
		def dh = newOrganisationUnitGroup(DISTRICT_HOSPITAL_GROUP, set);
		
		def period = newPeriod()
		
		def survey = newSurvey(period)
		def objective = newSurveyObjective(survey, 1, [(HEALTH_CENTER_GROUP)])
		def section = newSurveySection(objective, 1, [(HEALTH_CENTER_GROUP)])
		def question = newSimpleQuestion(section, 1, [(HEALTH_CENTER_GROUP)])

		def element = newSurveyElement(question, newRawDataElement(CODE(1), Type.TYPE_NUMBER()))

		def list = null
		
		when:
		list = surveyService.searchValidationRules(element, OrganisationUnitGroup.findByUuid( (HEALTH_CENTER_GROUP) ))
		
		then:
		list.isEmpty()
		
		when:
		def rule1 = newSurveyValidationRule(element, "", [(HEALTH_CENTER_GROUP)], "\$"+element.id+"==1")
		list = surveyService.searchValidationRules(element, OrganisationUnitGroup.findByUuid( (HEALTH_CENTER_GROUP) ))
		
		then:
		list.equals(new HashSet([rule1]))
		
		when:
		list = surveyService.searchValidationRules(element, OrganisationUnitGroup.findByUuid( (DISTRICT_HOSPITAL_GROUP) ))
		
		then:
		list.isEmpty()
	}
	
	def "test retrieve validation rule - several rules"() {
		setup:
		def set = newOrganisationUnitGroupSet(GROUP_SET_TYPE);
		def hc = newOrganisationUnitGroup(HEALTH_CENTER_GROUP, set);
		def dh = newOrganisationUnitGroup(DISTRICT_HOSPITAL_GROUP, set);
		
		def period = newPeriod()
		
		def survey = newSurvey(period)
		def objective = newSurveyObjective(survey, 1, [(HEALTH_CENTER_GROUP)])
		def section = newSurveySection(objective, 1, [(HEALTH_CENTER_GROUP)])
		def question = newSimpleQuestion(section, 1, [(HEALTH_CENTER_GROUP)])
	
		def element = newSurveyElement(question, newRawDataElement(CODE(1), Type.TYPE_NUMBER()))
	
		def list = null
	
		when:
		def rule3 = newSurveyValidationRule(element, "", [(HEALTH_CENTER_GROUP), (DISTRICT_HOSPITAL_GROUP)], "\$"+element.id+"0"+"==1")
		def rule4 = newSurveyValidationRule(element, "", [(HEALTH_CENTER_GROUP), (DISTRICT_HOSPITAL_GROUP)], "\$"+element.id+"==1")
		list = surveyService.searchValidationRules(element, OrganisationUnitGroup.findByUuid( (HEALTH_CENTER_GROUP) ))
		
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
		
		def dataElement = newRawDataElement(CODE(1), Type.TYPE_NUMBER())
		def element = newSurveyElement(question, dataElement)

		when:
		def surveyElements = surveyService.getSurveyElements(dataElement, null)
		
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
		
		def dataElement = newRawDataElement(j(["en": "element"]),CODE(1), Type.TYPE_NUMBER())
		def element = newSurveyElement(question, dataElement)

		def surveyElements = null
				
		when: "search by element text"
		surveyElements = surveyService.searchSurveyElements("ele", survey, [], [:])
		
		then:
		surveyElements.equals([element])
		
		when: "search by element id"
		surveyElements = surveyService.searchSurveyElements(""+element.id, survey, [], [:])
		
		then:
		surveyElements.equals([element])
		
		when: "search by data element id"
		surveyElements = surveyService.searchSurveyElements(""+dataElement.id, survey, [], [:])
		
		then:
		surveyElements.equals([element])

		when: "search filtered by type"
		surveyElements = surveyService.searchSurveyElements("ele", survey, ['bool'], [:])
		
		then:
		surveyElements.isEmpty()

		when: "search filtered by type"
		surveyElements = surveyService.searchSurveyElements("ele", survey, ['number'], [:])
		
		then:
		surveyElements.equals([element])
				
		when: "search filtered by survey"
		def survey2 = newSurvey(period)
		surveyElements = surveyService.searchSurveyElements("ele", survey2, [], [:])
		
		then:
		surveyElements.isEmpty()
	}
	
	def "get header prefixes"() {
		setup:
		def period = newPeriod()
		
		def survey = newSurvey(period)
		def objective = newSurveyObjective(survey, 1, [(HEALTH_CENTER_GROUP)])
		def section = newSurveySection(objective, 1, [(HEALTH_CENTER_GROUP)])
		def question = newSimpleQuestion(section, 1, [(HEALTH_CENTER_GROUP)])

		def dataElement = null
		def element = null
		def prefixes = null
		
		when:
		dataElement = newRawDataElement(CODE(1), Type.TYPE_MAP(["key1": Type.TYPE_MAP(["key11": Type.TYPE_NUMBER()])]))
		element = newSurveyElement(question, dataElement)
		prefixes = surveyService.getHeaderPrefixes(element)
		
		then:
		prefixes.equals([".key1", ".key1.key11"])
		
		when:
		dataElement = newRawDataElement(CODE(2), Type.TYPE_LIST(Type.TYPE_MAP(["key1": Type.TYPE_MAP(["key11": Type.TYPE_NUMBER()])])))
		element = newSurveyElement(question, dataElement)
		prefixes = surveyService.getHeaderPrefixes(element)
		
		then:
		prefixes.equals(["[_].key1", "[_].key1.key11"])
	}
	
}
