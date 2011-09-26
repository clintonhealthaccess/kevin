package org.chai.kevin.survey

import org.chai.kevin.IntegrationTestInitializer;
import org.chai.kevin.IntegrationTests;
import org.chai.kevin.data.DataElement;
import org.chai.kevin.survey.validation.SurveyEnteredValue;
import org.chai.kevin.util.JSONUtils;
import org.chai.kevin.value.DataValue;
import org.chai.kevin.value.Value;
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
	
	def "false validation"() {
		
		setup:
		createSurvey();
		def question = SurveySimpleQuestion.list()[0]
		def dataElement1 = new DataElement(code:"ELEM3", type: JSONUtils.TYPE_NUMBER).save(failOnError: true)
		def surveyElement1 = new SurveyElement(surveyQuestion: question, dataElement: dataElement1).save(failOnError: true)
		
		def validationRule = new SurveyValidationRule(expression: "\$"+surveyElement1.id+" > 1")
		surveyElement1.addValidationRule(validationRule)
		surveyElement1.save(failOnError: true)
		
		when:
		def organisation = getOrganisation("Kivuye HC")
		new SurveyEnteredValue(surveyElement: surveyElement1, value: v("1"), organisation: organisation.organisationUnit).save(failOnError: true)
		Set<String> prefixes = validationService.getInvalidPrefix(validationRule, organisation)
		
		then:
		prefixes.equals([""])
		
	}
	
	def "false validation based on other elements"() {
		setup:
		createSurvey();
		def question = SurveySimpleQuestion.list()[0]
		def dataElement1 = new DataElement(code:"ELEM4", type: JSONUtils.TYPE_NUMBER).save(failOnError: true)
		def dataElement2 = new DataElement(code:"ELEM5", type: JSONUtils.TYPE_NUMBER).save(failOnError: true)
		def surveyElement1 = new SurveyElement(surveyQuestion: question, dataElement: dataElement1).save(failOnError: true)
		def surveyElement2 = new SurveyElement(surveyQuestion: question, dataElement: dataElement2).save(failOnError: true)
		
		def validationRule = new SurveyValidationRule(expression: "\$"+surveyElement1.id+" > \$"+surveyElement2.id)
		surveyElement1.addValidationRule(validationRule)
		surveyElement1.save(failOnError: true)
		
		when:
		def organisation = getOrganisation("Kivuye HC")
		new SurveyEnteredValue(surveyElement: surveyElement1, value: v("1"), organisation: organisation.organisationUnit).save(failOnError: true)
		new SurveyEnteredValue(surveyElement: surveyElement2, value: v("1"), organisation: organisation.organisationUnit).save(failOnError: true)
		Set<String> prefixes = validationService.getInvalidPrefix(validationRule, organisation)
		
		then:
		prefixes.equals([""])
		
		when:
		organisation = getOrganisation("Kivuye HC")
		new SurveyEnteredValue(surveyElement: surveyElement1, value: v("2"), organisation: organisation.organisationUnit).save(failOnError: true)
		new SurveyEnteredValue(surveyElement: surveyElement2, value: v("1"), organisation: organisation.organisationUnit).save(failOnError: true)
		prefixes = validationService.getInvalidPrefix(validationRule, organisation)
		
		then:
		prefixes.isEmpty()
	}

	
	
	def "validation based on null values"() {
		setup:
		createSurvey();
		def question = SurveySimpleQuestion.list()[0]
		def dataElement1 = new DataElement(code:"ELEM9", type: JSONUtils.TYPE_NUMBER).save(failOnError: true)
		def dataElement2 = new DataElement(code:"ELEM10", type: JSONUtils.TYPE_NUMBER).save(failOnError: true)
		def surveyElement1 = new SurveyElement(surveyQuestion: question, dataElement: dataElement1).save(failOnError: true)
		def surveyElement2 = new SurveyElement(surveyQuestion: question, dataElement: dataElement2).save(failOnError: true)
		
		def validationRule = new SurveyValidationRule(expression: "if (\$"+surveyElement1.id+" == 0) \$"+surveyElement2.id+" == null else false")
		surveyElement1.addValidationRule(validationRule)
		surveyElement1.save(failOnError: true)
		
		when:
		def organisation = getOrganisation("Kivuye HC")
		new SurveyEnteredValue(surveyElement: surveyElement1, value: v("0"), organisation: organisation.organisationUnit).save(failOnError: true)
		new SurveyEnteredValue(surveyElement: surveyElement2, value: Value.NULL, organisation: organisation.organisationUnit).save(failOnError: true)
		def prefixes = validationService.getInvalidPrefix(validationRule, organisation)
		
		then:
		prefixes.isEmpty()
	}	
		
}
