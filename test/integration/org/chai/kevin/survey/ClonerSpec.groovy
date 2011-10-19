package org.chai.kevin.survey

import org.chai.kevin.data.DataElement;
import org.chai.kevin.data.Type;
import org.chai.kevin.util.JSONUtils;

class ClonerSpec extends SurveyIntegrationTests {
	
	def "test clone double number of elements"() {
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
		
		expect:
		Survey.count() == 1
		SurveyObjective.count() == 1
		SurveySection.count() == 1
		SurveyTableQuestion.count() == 1
		SurveyTableColumn.count() == 1
		SurveyTableRow.count() == 1
		SurveyElement.count() == 1
			
		when:
		SurveyCloner cloner = new SurveyCloner(survey)
		cloner.cloneTree();
		def copy = cloner.getSurvey()
		copy.save(failOnError: true, flush: true)
				
		then:
		Survey.count() == 2
		SurveyObjective.count() == 2
		SurveySection.count() == 2
		SurveyTableQuestion.count() == 2
		SurveyTableColumn.count() == 2
		SurveyTableRow.count() == 2
		SurveyElement.count() == 2
		DataElement.count() == 1 
		
		survey != copy
		
		!copy.objectives[0].equals(survey.objectives[0])
		copy.objectives[0].survey.equals(copy)
		survey.objectives[0].survey.equals(survey)
		
		survey.names['en']+' (copy)' == copy.names['en']
	}
	
	def "test clone survey with skip rule"() {
		setup:
		def period = newPeriod()
		def survey = newSurvey(period)
		def objective = newSurveyObjective(survey, 1, [])
		def section = newSurveySection(objective, 1, [])
		def question = newSimpleQuestion(section, 1, [])
		def dataElement = newDataElement(CODE(1), Type.TYPE_NUMBER())
		def element = newSurveyElement(question, dataElement)
		def skipRule = newSkipRule(survey, "\$"+element.id+" == 1", [:], [])
		
		when:
		SurveyCloner cloner = new SurveyCloner(survey)
		cloner.cloneTree();
		def copy = cloner.getSurvey()
		copy.save(failOnError: true, flush: true)
		cloner.cloneRules();
		
		then:
		copy.getSkipRules().size() == 1
		copy.getSkipRules()[0].expression != survey.getSkipRules()[0].expression
		copy.getSkipRules()[0].expression == "\$" + SurveyElement.list()[1].id + " == 1"
		cloner.getUnchangedSkipRules().size() == 0
	}
	
	def "test clone survey with validation rule from other survey"() {
		setup:
		def period = newPeriod()
		def survey1 = newSurvey(period)
		def objective1 = newSurveyObjective(survey1, 1, [])
		def section1 = newSurveySection(objective1, 1, [])
		def question1 = newSimpleQuestion(section1, 1, [])
		def dataElement1 = newDataElement(CODE(1), Type.TYPE_NUMBER())
		def element1 = newSurveyElement(question1, dataElement1)
		
		def survey2 = newSurvey(period)
		def objective2 = newSurveyObjective(survey2, 1, [])
		def section2 = newSurveySection(objective2, 1, [])
		def question2 = newSimpleQuestion(section2, 1, [])
		def dataElement2 = newDataElement(CODE(2), Type.TYPE_NUMBER())
		def element2 = newSurveyElement(question2, dataElement2)
		def validationRule = newSurveyValidationRule(element2, "", [(HEALTH_CENTER_GROUP), (DISTRICT_HOSPITAL_GROUP)], "\$"+element1+" == 1", [element1])
		
		when:
		SurveyCloner cloner = new SurveyCloner(survey2)
		cloner.cloneTree();
		def copy = cloner.getSurvey()
		copy.save(failOnError: true, flush: true)
		cloner.cloneRules();
		
		then:
		def element3 = SurveyElement.list()[2]
		element3.validationRules.size() == 1
		element3.validationRules.iterator().next().expression == element2.validationRules.iterator().next().expression
		cloner.getUnchangedValidationRules().size() == 1
	}

	def "test clone survey with validation rule transforms dependencies"() {
		setup:
		def period = newPeriod()
		def survey = newSurvey(period)
		def objective = newSurveyObjective(survey, 1, [])
		def section = newSurveySection(objective, 1, [])
		def question = newSimpleQuestion(section, 1, [])
		def dataElement = newDataElement(CODE(1), Type.TYPE_NUMBER())
		def element = newSurveyElement(question, dataElement)
		def validationRule = newSurveyValidationRule(element, "", [(HEALTH_CENTER_GROUP), (DISTRICT_HOSPITAL_GROUP)], "1 == 1", [element])
		
		when:
		SurveyCloner cloner = new SurveyCloner(survey)
		cloner.cloneTree();
		def copy = cloner.getSurvey()
		copy.save(failOnError: true, flush: true)
		cloner.cloneRules();
		
		then:
		def elementCopy = SurveyElement.list()[1]
		elementCopy.validationRules.size() == 1
		elementCopy.validationRules.iterator().next().dependencies.size() == 1
		elementCopy.validationRules.iterator().next().dependencies[0].equals(elementCopy)
		cloner.getUnchangedValidationRules().size() == 0
		
	}
		
}
