package org.chai.kevin.survey

import org.chai.kevin.IntegrationTestInitializer;
import org.chai.kevin.IntegrationTests;
import org.chai.kevin.data.DataElement;
import org.chai.kevin.data.ValueType;
import org.chai.kevin.survey.validation.SurveyValidationRule;
import org.chai.kevin.value.DataValue;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.period.Period;

class ValidationSpec extends IntegrationTests {

	def validationService;
	
	def setup() {
		IntegrationTestInitializer.createDummyStructure()	
	}
	
	def "true validation"() {
		
		setup:
		def dataElement1 = new DataElement(code:"ELEM1", type: ValueType.VALUE).save(failOnError: true)
		def surveyElement1 = new SurveyElement(dataElement: dataElement1).save(failOnError: true)
		
		when:
		def dataValue1 = new DataValue(value: "1")
		def surveyElementValue = new SurveyElementValue(surveyElement: surveyElement1, dataValue: dataValue1)
		surveyElementValue.userValidation(validationService, null, null)
		
		then:
		surveyElementValue.isValid()
		
	}
	
	def "false validation"() {
		
		setup:
		def dataElement1 = new DataElement(code:"ELEM1", type: ValueType.VALUE).save(failOnError: true)
		def surveyElement1 = new SurveyElement(dataElement: dataElement1).save(failOnError: true)
		
		def validationRule = new SurveyValidationRule(expression: "["+surveyElement1.id+"] > 1")
		surveyElement1.addValidationRule(validationRule)
		surveyElement1.save(failOnError: true)
		
		when:
		def dataValue1 = new DataValue(value: "1")
		def surveyElementValue = new SurveyElementValue(surveyElement: surveyElement1, dataValue: dataValue1)
		surveyElementValue.userValidation(validationService, null, null)
		
		then:
		!surveyElementValue.isValid()
		
	}
	
	def "false validation based on other elements"() {
		setup:
		def dataElement1 = new DataElement(code:"ELEM1", type: ValueType.VALUE).save(failOnError: true)
		def dataElement2 = new DataElement(code:"ELEM2", type: ValueType.VALUE).save(failOnError: true)
		def surveyElement1 = new SurveyElement(dataElement: dataElement1).save(failOnError: true)
		def surveyElement2 = new SurveyElement(dataElement: dataElement2).save(failOnError: true)
		
		def validationRule = new SurveyValidationRule(expression: "["+surveyElement1.id+"] > ["+surveyElement2.id+"]")
		surveyElement1.addValidationRule(validationRule)
		surveyElement1.save(failOnError: true)
		
		when:
		def dataValue1 = new DataValue(value: "1")
		def dataValue2 = new DataValue(value: "2")
		def surveyElementValue1 = new SurveyElementValue(surveyElement: surveyElement1, dataValue: dataValue1)
		def surveyElementValue2 = new SurveyElementValue(surveyElement: surveyElement2, dataValue: dataValue2)
		def surveyPage = new SurveyPage(surveyElements: [(surveyElement1.id): surveyElementValue1, (surveyElement2.id): surveyElementValue2])
		surveyElementValue1.surveyPage = surveyPage
		surveyElementValue2.surveyPage = surveyPage
		surveyElementValue1.userValidation(validationService, null, null)
		
		then:
		!surveyElementValue1.isValid()
		
		when:
		dataValue1 = new DataValue(value: "2")
		dataValue2 = new DataValue(value: "1")
		surveyElementValue1 = new SurveyElementValue(surveyElement: surveyElement1, dataValue: dataValue1)
		surveyElementValue2 = new SurveyElementValue(surveyElement: surveyElement2, dataValue: dataValue2)
		surveyPage = new SurveyPage(surveyElements: [(surveyElement1.id): surveyElementValue1, (surveyElement2.id): surveyElementValue2])
		surveyElementValue1.surveyPage = surveyPage
		surveyElementValue2.surveyPage = surveyPage
		surveyElementValue1.userValidation(validationService, null, null)
		
		then:
		surveyElementValue1.isValid()
	}

	def "false validation based on other elements in different pages"() {
		setup:
		def dataElement1 = new DataElement(code:"ELEM1", type: ValueType.VALUE).save(failOnError: true)
		def dataElement2 = new DataElement(code:"ELEM2", type: ValueType.VALUE).save(failOnError: true)
		def surveyElement1 = new SurveyElement(dataElement: dataElement1).save(failOnError: true)
		def surveyElement2 = new SurveyElement(dataElement: dataElement2).save(failOnError: true)
		
		def validationRule = new SurveyValidationRule(expression: "["+surveyElement1.id+"] > ["+surveyElement2.id+"]")
		surveyElement1.addValidationRule(validationRule)
		surveyElement1.save(failOnError: true)
		
		def dataValue2 = new DataValue(
			dataElement: dataElement2,
			organisationUnit: OrganisationUnit.findByName("Butaro DH"),
			period: Period.list()[0],
			value: "2",
			timestamp: new Date()
		).save(failOnError: true, flush: true)
		
		when:
		def dataValue1 = new DataValue(value: "1")
		def surveyElementValue1 = new SurveyElementValue(surveyElement: surveyElement1, dataValue: dataValue1)
		def surveyPage = new SurveyPage(surveyElements: [(surveyElement1.id): surveyElementValue1], organisationUnit: OrganisationUnit.findByName("Butaro DH"), period: Period.list()[0])
		surveyElementValue1.surveyPage = surveyPage
		surveyPage.userValidation(validationService)
		
		then:
		!surveyPage.isValid()
	}
	
	def "validation based on non-existing elements"() {
		setup:
		def dataElement1 = new DataElement(code:"ELEM1", type: ValueType.VALUE).save(failOnError: true)
		def surveyElement1 = new SurveyElement(dataElement: dataElement1).save(failOnError: true)
		def validationRule = new SurveyValidationRule(expression: "["+(surveyElement1.id+1)+"] > 0")
		surveyElement1.addValidationRule(validationRule)
		surveyElement1.save(failOnError: true)
		
		when:
		def dataValue1 = new DataValue(value: "1")
		def surveyElementValue1 = new SurveyElementValue(surveyElement: surveyElement1, dataValue: dataValue1)
		def surveyPage = new SurveyPage(surveyElements: [(surveyElement1.id): surveyElementValue1])
		surveyElementValue1.surveyPage = surveyPage
		surveyPage.userValidation(validationService)
		
		then:
		!surveyPage.isValid()
	}
	
	def "validation based on null values"() {
		setup:
		def dataElement1 = new DataElement(code:"ELEM1", type: ValueType.VALUE).save(failOnError: true)
		def dataElement2 = new DataElement(code:"ELEM2", type: ValueType.VALUE).save(failOnError: true)
		def surveyElement1 = new SurveyElement(dataElement: dataElement1).save(failOnError: true)
		def surveyElement2 = new SurveyElement(dataElement: dataElement2).save(failOnError: true)
		
		def validationRule = new SurveyValidationRule(expression: "if (["+surveyElement1.id+"] == 0, \"["+surveyElement2.id+"]\" == \"null\", 1==1)")
		surveyElement1.addValidationRule(validationRule)
		surveyElement1.save(failOnError: true)
		
		when:
		def dataValue1 = new DataValue(value: "0")
		def dataValue2 = new DataValue(value: null)
		def surveyElementValue1 = new SurveyElementValue(surveyElement: surveyElement1, dataValue: dataValue1)
		def surveyElementValue2 = new SurveyElementValue(surveyElement: surveyElement2, dataValue: dataValue2)
		def surveyPage = new SurveyPage(surveyElements: [(surveyElement1.id): surveyElementValue1, (surveyElement2.id): surveyElementValue2])
		surveyElementValue1.surveyPage = surveyPage
		surveyElementValue2.surveyPage = surveyPage
		surveyElementValue1.userValidation(validationService, null, null)
		
		then:
		surveyElementValue1.isValid()
	}	
		
}
