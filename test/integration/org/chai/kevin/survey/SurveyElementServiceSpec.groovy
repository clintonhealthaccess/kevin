package org.chai.kevin.survey

import org.chai.kevin.IntegrationTestInitializer;
import org.chai.kevin.IntegrationTests;
import org.chai.kevin.data.DataElement;
import org.chai.kevin.util.JSONUtils;
import org.hisp.dhis.period.Period;

class SurveyElementServiceSpec extends IntegrationTests {

	def surveyElementService;
	
	def setup() {
		IntegrationTestInitializer.createDummyStructure();
	}
	
	def createSurvey() {
		def dataElement = new DataElement(code:"ELEM1", type: JSONUtils.TYPE_NUMBER).save(failOnError: true)
		
		def survey = new Survey(period: Period.list()[0]).save(failOnError: true);
		def objective = new SurveyObjective(survey: survey, order: 1, groupUuidString: "Health Center")
		survey.addObjective(objective)
		survey.save(failOnError: true)
		def section = new SurveySection(objective: objective, order: 1, groupUuidString: "Health Center")
		objective.addSection(section)
		objective.save(failOnError: true)
		def question = new SurveySimpleQuestion(section: section, order: 1, groupUuidString: "Health Center")
		section.addQuestion(question)
		section.save(failOnError: true)
		def element = new SurveyElement(surveyQuestion: question, dataElement: dataElement)
		question.surveyElement = element;
		question.save(failOnError: true)
	}
	
	def "test retrieve skip rules when empty"() {
		setup:
		createSurvey()
		
		def survey = Survey.list()[0]
		def element = SurveyElement.list()[0]
		
		when:
		def list = surveyElementService.getSkipRules(element)
		
		then:
		list.size() == 0
	}
	
	def "test retrieve skip rules"() {
		setup:
		createSurvey()
		def survey = Survey.list()[0]
		def element = SurveyElement.list()[0]
		
		when:
		def skipRule1 = new SurveySkipRule(survey: survey, expression: "1==1", skippedSurveyElements: [element])
		survey.addSkipRule(skipRule1)
		survey.save(failOnError: true, flush: true)
		def list1 = surveyElementService.getSkipRules(element)
		
		then:
		SurveySkipRule.list()[0].skippedSurveyElements.size() == 1
		
		list1.size() == 1
		list1.iterator().next().equals(skipRule1)
		
		when:
		def skipRule2 = new SurveySkipRule(survey: survey, expression: "1==1")
		survey.addSkipRule(skipRule2)
		survey.save(failOnError: true)
		def list2 = surveyElementService.getSkipRules(element)
		
		then:
		list2.size() == 1
		list2.iterator().next().equals(skipRule1)
	}	
	
	def "test get survey elements for data element"() {
		setup:
		createSurvey()
		def element = SurveyElement.list()[0]
		def dataElement = DataElement.findByCode("ELEM1")
		
		when:
		def surveyElements = surveyElementService.getSurveyElements(dataElement)
		
		then:
		surveyElements.size() == 1
		surveyElements.iterator().next().equals(element)
		
		
	}
	
}
