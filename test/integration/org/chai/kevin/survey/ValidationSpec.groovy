package org.chai.kevin.survey

import org.chai.kevin.IntegrationTestInitializer;
import org.chai.kevin.IntegrationTests;
import org.chai.kevin.data.DataElement;
import org.chai.kevin.util.JSONUtils;
import org.chai.kevin.value.DataValue;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.period.Period;

class ValidationSpec extends IntegrationTests {

	def validationService;
	
	def setup() {
		IntegrationTestInitializer.createDummyStructure()	
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
	
	
	def "true validation"() {
		
		setup:
		createSurvey();
		def question = SurveySimpleQuestion.list()[0]
		def dataElement1 = new DataElement(code:"ELEM2", type: JSONUtils.TYPE_NUMBER).save(failOnError: true)
		def surveyElement1 = new SurveyElement(surveyQuestion: question, dataElement: dataElement1).save(failOnError: true)
		
		when:
		def surveyElementValue = new SurveyElementValue(surveyElement: surveyElement1, value: v("1"))
		surveyElementValue.userValidation(validationService, null, null)
		
		then:
		surveyElementValue.isValid()
		
	}
	
	def "false validation"() {
		
		setup:
		createSurvey();
		def question = SurveySimpleQuestion.list()[0]
		def dataElement1 = new DataElement(code:"ELEM3", type: JSONUtils.TYPE_NUMBER).save(failOnError: true)
		def surveyElement1 = new SurveyElement(surveyQuestion: question, dataElement: dataElement1).save(failOnError: true)
		
		def validationRule = new SurveyValidationRule(expression: "["+surveyElement1.id+"] > 1")
		surveyElement1.addValidationRule(validationRule)
		surveyElement1.save(failOnError: true)
		
		when:
		def surveyElementValue = new SurveyElementValue(surveyElement: surveyElement1, value: v("1"))
		surveyElementValue.userValidation(validationService, null, null)
		
		then:
		!surveyElementValue.isValid()
		
	}
	
	def "false validation based on other elements"() {
		setup:
		createSurvey();
		def question = SurveySimpleQuestion.list()[0]
		def dataElement1 = new DataElement(code:"ELEM4", type: JSONUtils.TYPE_NUMBER).save(failOnError: true)
		def dataElement2 = new DataElement(code:"ELEM5", type: JSONUtils.TYPE_NUMBER).save(failOnError: true)
		def surveyElement1 = new SurveyElement(surveyQuestion: question, dataElement: dataElement1).save(failOnError: true)
		def surveyElement2 = new SurveyElement(surveyQuestion: question, dataElement: dataElement2).save(failOnError: true)
		
		def validationRule = new SurveyValidationRule(expression: "["+surveyElement1.id+"] > ["+surveyElement2.id+"]")
		surveyElement1.addValidationRule(validationRule)
		surveyElement1.save(failOnError: true)
		
		when:
		def surveyElementValue1 = new SurveyElementValue(surveyElement: surveyElement1, value: v("1"))
		def surveyElementValue2 = new SurveyElementValue(surveyElement: surveyElement2, value: v("2"))
		def surveyPage = new SurveyPage(surveyElements: [(surveyElement1.id): surveyElementValue1, (surveyElement2.id): surveyElementValue2])
		surveyElementValue1.userValidation(validationService, null, null)
		
		then:
		!surveyElementValue1.isValid()
		
		when:
		surveyElementValue1 = new SurveyElementValue(surveyElement: surveyElement1, value: v("2"))
		surveyElementValue2 = new SurveyElementValue(surveyElement: surveyElement2, value: v("1"))
		surveyPage = new SurveyPage(surveyElements: [(surveyElement1.id): surveyElementValue1, (surveyElement2.id): surveyElementValue2])
		surveyElementValue1.userValidation(validationService, null, null)
		
		then:
		surveyElementValue1.isValid()
	}

	def "false validation based on other elements in different pages"() {
		setup:
		createSurvey();
		def question = SurveySimpleQuestion.list()[0]
		def dataElement1 = new DataElement(code:"ELEM6", type: JSONUtils.TYPE_NUMBER).save(failOnError: true)
		def dataElement2 = new DataElement(code:"ELEM7", type: JSONUtils.TYPE_NUMBER).save(failOnError: true)
		def surveyElement1 = new SurveyElement(surveyQuestion: question, dataElement: dataElement1).save(failOnError: true)
		def surveyElement2 = new SurveyElement(surveyQuestion: question, dataElement: dataElement2).save(failOnError: true)
		
		def validationRule = new SurveyValidationRule(expression: "["+surveyElement1.id+"] > ["+surveyElement2.id+"]")
		surveyElement1.addValidationRule(validationRule)
		surveyElement1.save(failOnError: true)
		
		def dataValue2 = new DataValue(
			dataElement: dataElement2,
			organisationUnit: OrganisationUnit.findByName("Butaro DH"),
			period: Period.list()[0],
			value: v("2"),
			timestamp: new Date()
		).save(failOnError: true, flush: true)
		
		when:
		def surveyElementValue1 = new SurveyElementValue(surveyElement: surveyElement1, value: v("1"))
		def surveyPage = new SurveyPage(surveyElements: [(surveyElement1.id): surveyElementValue1], organisationUnit: OrganisationUnit.findByName("Butaro DH"), period: Period.list()[0])
		surveyPage.userValidation(validationService)
		
		then:
		!surveyPage.isValid()
	}
	
	def "validation based on non-existing elements"() {
		setup:
		createSurvey();
		def question = SurveySimpleQuestion.list()[0]
		def dataElement1 = new DataElement(code:"ELEM8", type: JSONUtils.TYPE_NUMBER).save(failOnError: true)
		def surveyElement1 = new SurveyElement(surveyQuestion: question, dataElement: dataElement1).save(failOnError: true)
		def validationRule = new SurveyValidationRule(expression: "["+(surveyElement1.id+1)+"] > 0")
		surveyElement1.addValidationRule(validationRule)
		surveyElement1.save(failOnError: true)
		
		when:
		def surveyElementValue1 = new SurveyElementValue(surveyElement: surveyElement1, value: v("1"))
		def surveyPage = new SurveyPage(surveyElements: [(surveyElement1.id): surveyElementValue1])
		surveyPage.userValidation(validationService)
		
		then:
		!surveyPage.isValid()
	}
	
	def "validation based on null values"() {
		setup:
		createSurvey();
		def question = SurveySimpleQuestion.list()[0]
		def dataElement1 = new DataElement(code:"ELEM9", type: JSONUtils.TYPE_NUMBER).save(failOnError: true)
		def dataElement2 = new DataElement(code:"ELEM10", type: JSONUtils.TYPE_NUMBER).save(failOnError: true)
		def surveyElement1 = new SurveyElement(surveyQuestion: question, dataElement: dataElement1).save(failOnError: true)
		def surveyElement2 = new SurveyElement(surveyQuestion: question, dataElement: dataElement2).save(failOnError: true)
		
		def validationRule = new SurveyValidationRule(expression: "if (["+surveyElement1.id+"] == 0, \"["+surveyElement2.id+"]\" == \"null\", 1==1)")
		surveyElement1.addValidationRule(validationRule)
		surveyElement1.save(failOnError: true)
		
		when:
		def surveyElementValue1 = new SurveyElementValue(surveyElement: surveyElement1, value: v("0"))
		def surveyElementValue2 = new SurveyElementValue(surveyElement: surveyElement2, value: v(null))
		def surveyPage = new SurveyPage(surveyElements: [(surveyElement1.id): surveyElementValue1, (surveyElement2.id): surveyElementValue2])
		surveyElementValue1.userValidation(validationService, null, null)
		
		then:
		surveyElementValue1.isValid()
	}	
		
}
