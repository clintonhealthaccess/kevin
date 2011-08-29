package org.chai.kevin.survey

import org.chai.kevin.IntegrationTestInitializer;
import org.chai.kevin.IntegrationTests;
import org.chai.kevin.data.DataElement;
import org.chai.kevin.data.ValueType;

class ClonerSpec extends IntegrationTests {

	def setup() {
		IntegrationTestInitializer.createDummyStructure()
		if (DataElement.count() == 0) new DataElement(names:j(["en":"Element 8"]), descriptions:j([:]), code:"CODE8", type: ValueType.VALUE).save(failOnError: true, flush: true)
	}
	
	def "test clone double number of elements"() {
		setup:
		def section = DomainSpec.createDummySection()
		DomainSpec.createDummyTableQuestion(section)
		def survey = section.getSurvey()
		
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
		
		!Survey.list()[1].objectives[0].equals(Survey.list()[0].objectives[0])
		Survey.list()[1].objectives[0].survey.equals(Survey.list()[1])
		Survey.list()[0].objectives[0].survey.equals(Survey.list()[0])
		
	}
	
	def "test clone survey with skip rule"() {
		setup:
		def section = DomainSpec.createDummySection()
		DomainSpec.createDummyTableQuestion(section)
		def survey = section.getSurvey()
		def element = SurveyElement.list()[0]
		def skipRule = new SurveySkipRule(expression: "["+element.id+"]")
		survey.addSkipRule(skipRule)
		survey.save(failOnError: true)
		
		when:
		SurveyCloner cloner = new SurveyCloner(survey)
		cloner.cloneTree();
		def copy = cloner.getSurvey()
		copy.save(failOnError: true, flush: true)
		cloner.cloneRules();
		
		then:
		copy.getSkipRules().size() == 1
		copy.getSkipRules()[0].expression != survey.getSkipRules()[0].expression
		copy.getSkipRules()[0].expression == "["+SurveyElement.list()[1].id+"]"
		cloner.getUnchangedSkipRules().size() == 0
		
	}
	
	def "test clone survey with validation rule from other survey"() {
		setup:
		def section1 = DomainSpec.createDummySection()
		DomainSpec.createDummyTableQuestion(section1)
		def section2 = DomainSpec.createDummySection()
		DomainSpec.createDummyTableQuestion(section2)
		def element1 = SurveyElement.list()[0]
		def element2 = SurveyElement.list()[1]
		def validationRule = new SurveyValidationRule(expression: "["+element1.id+"]")
		element2.addValidationRule(validationRule)
		def survey2 = section2.getSurvey()
		
		
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
		def section1 = DomainSpec.createDummySection()
		DomainSpec.createDummyTableQuestion(section1)
		def element1 = SurveyElement.list()[0]
		def validationRule = new SurveyValidationRule(expression: "["+element1.id+"]", dependencies: [element1])
		element1.addValidationRule(validationRule)
		def survey1 = section1.getSurvey()
		
		when:
		SurveyCloner cloner = new SurveyCloner(survey1)
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
