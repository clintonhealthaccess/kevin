package org.chai.kevin.data;

import org.chai.kevin.IntegrationTests;
import org.chai.kevin.data.RawDataElement;
import org.chai.kevin.data.RawDataElementController;
import org.chai.kevin.data.Type;
import org.chai.kevin.survey.SurveyElement;
import org.chai.kevin.survey.SurveyIntegrationTests;
import org.chai.kevin.survey.validation.SurveyEnteredValue;
import org.chai.kevin.value.RawDataElementValue;
import org.chai.kevin.value.Value;
import org.hisp.dhis.organisationunit.OrganisationUnit;

class RawDataElementControllerSpec extends IntegrationTests {

	def rawDataElementController

//	def "create new data element"() {
//		setup:
//		rawDataElementController = new RawDataElementController()
//
//		when:
//		rawDataElementController.create()
//
//		then:
//		rawDataElementController.modelAndView.model.rawDataElement.id == null
//	}
//
//	def "save new data element"() {
//		setup:
//		rawDataElementController = new RawDataElementController()
//
//		when:
//		rawDataElementController.params.code = CODE(1)
//		rawDataElementController.params['type.jsonValue'] = Type.TYPE_BOOL().getJsonValue()
//		rawDataElementController.saveWithoutTokenCheck()
//
//		then:
//		rawDataElementController.response.redirectedUrl.equals(rawDataElementController.getTargetURI())
//	}
//
//	def "can delete data element"() {
//		setup:
//		rawDataElementController = new RawDataElementController()
//		def organisation = newOrganisationUnit(BUTARO)
//		def period = newPeriod()
//		def dataElement = null
//
//		when:
//		dataElement = newRawDataElement(CODE(1), Type.TYPE_NUMBER())
//		rawDataElementController.params.id = dataElement.id
//		rawDataElementController.delete()
//
//		then:
//		RawDataElement.count() == 0
//	}
//
//	def "cannot delete data element when it still has values"() {
//		setup:
//		rawDataElementController = new RawDataElementController()
//		def organisation = newOrganisationUnit(BUTARO)
//		def period = newPeriod()
//		def dataElement = null
//
//		when:
//		dataElement = newRawDataElement(CODE(2), Type.TYPE_NUMBER())
//		newRawDataElementValue(dataElement, period, organisation, Value.NULL)
//		rawDataElementController.params.id = dataElement.id
//		rawDataElementController.delete()
//
//		then:
//		//		rawDataElementController.response.contentAsString.contains("success")
//		RawDataElement.count() == 1
//		RawDataElementValue.count() == 1
//	}
//
//	def "cannot change data element type if it still has values" () {
//		setup:
//		rawDataElementController = new RawDataElementController()
//		def organisation = newOrganisationUnit(BUTARO)
//		def period = newPeriod()
//		def dataElement = newRawDataElement(CODE(1), Type.TYPE_NUMBER())
//
//		when:
//		rawDataElementController.params.id = dataElement.id
//		rawDataElementController.params.code = dataElement.code
//		rawDataElementController.params['type.jsonValue'] = Type.TYPE_BOOL().getJsonValue()
//		rawDataElementController.saveWithoutTokenCheck()
//
//		then:
//		rawDataElementController.response.redirectedUrl.equals(rawDataElementController.getTargetURI())
//		dataElement.type.equals(Type.TYPE_BOOL())
//
//		when:
//		newRawDataElementValue(dataElement, period, organisation, Value.NULL)
//		rawDataElementController.params.id = dataElement.id
//		rawDataElementController.params.code = dataElement.code
//		rawDataElementController.params['type.jsonValue'] = Type.TYPE_STRING().getJsonValue()
//		rawDataElementController.saveWithoutTokenCheck()
//
//		then:
//		//		rawDataElementController.response.contentAsString.contains("success")
//		(Type.TYPE_BOOL()).equals(dataElement.type)
//	}
//
//	
//	def "not changing data element type does not delete survey entered values"() {
//		setup:
//		setupOrganisationUnitTree()
//		def period = newPeriod()
//		def dataElement = newRawDataElement(j(["en":"Element 1"]), CODE(1), Type.TYPE_NUMBER())
//		def survey = SurveyIntegrationTests.newSurvey(period)
//		def objective = SurveyIntegrationTests.newSurveyObjective(survey, 1, [(HEALTH_CENTER_GROUP)])
//		def section = SurveyIntegrationTests.newSurveySection(objective, 1, [(HEALTH_CENTER_GROUP)])
//		def question = SurveyIntegrationTests.newSimpleQuestion(section, 1, [(HEALTH_CENTER_GROUP)])
//		def element = SurveyIntegrationTests.newSurveyElement(question, dataElement);
//		SurveyIntegrationTests.newSurveyEnteredValue(element, period, OrganisationUnit.findByName(BUTARO), v("1"))
//
//		expect:
//		RawDataElement.count() == 1
//		SurveyElement.count() == 1
//		SurveyEnteredValue.count() == 1
//		
//		when:
//		rawDataElementController = new RawDataElementController()
//		rawDataElementController.params.id = dataElement.id
//		rawDataElementController.params.code = dataElement.code
//		rawDataElementController.params['type.jsonValue'] = Type.TYPE_NUMBER().getJsonValue()
//		rawDataElementController.saveWithoutTokenCheck()
//		
//		then:
//		RawDataElement.count() == 1
//		SurveyElement.count() == 1
//		SurveyEnteredValue.count() == 1
//	}
		
	def "changing data element type deletes survey entered values"() {
		setup:
		setupOrganisationUnitTree()
		def period = newPeriod()
		def dataElement = newRawDataElement(j(["en":"Element 1"]), CODE(27), Type.TYPE_NUMBER())
		def survey = SurveyIntegrationTests.newSurvey(period)
		def objective = SurveyIntegrationTests.newSurveyObjective(survey, 1, [(HEALTH_CENTER_GROUP)])
		def section = SurveyIntegrationTests.newSurveySection(objective, 1, [(HEALTH_CENTER_GROUP)])
		def question = SurveyIntegrationTests.newSimpleQuestion(section, 1, [(HEALTH_CENTER_GROUP)])
		def element = SurveyIntegrationTests.newSurveyElement(question, dataElement);
		SurveyIntegrationTests.newSurveyEnteredValue(element, period, OrganisationUnit.findByName(BUTARO), v("1"))

		expect:
		RawDataElement.count() == 1
		SurveyElement.count() == 1
		SurveyEnteredValue.count() == 1
		
		when:
		rawDataElementController = new RawDataElementController()
		rawDataElementController.params.id = dataElement.id
		rawDataElementController.params.code = dataElement.code
		rawDataElementController.params['type.jsonValue'] = Type.TYPE_BOOL().getJsonValue()
		rawDataElementController.saveWithoutTokenCheck()
		
		then:
		RawDataElement.count() == 1
		SurveyElement.count() == 1
		SurveyEnteredValue.count() == 0
	}
	
//	def "cannot delete data element if referencing data"() {
//		setup:
//		setupOrganisationUnitTree()
//		def period = newPeriod()
//		def dataElement = newRawDataElement(j(["en":"Element 1"]), CODE(1), Type.TYPE_NUMBER())
//		def normalizedDataElement = newNormalizedDataElement(CODE(2), Type.TYPE_NUMBER(), e(["1":[DISTRICT_HOSPITAL_GROUP:"\$"+dataElement.id]]))
//		rawDataElementController = new RawDataElementController()
//		
//		when:
//		rawDataElementController.params.id = dataElement.id
//		rawDataElementController.delete()
//		
//		then:
//		NormalizedDataElement.count() == 1
//		RawDataElement.count() == 1
//	}
//
//	def "delete data element deletes survey element and survey entered values"() {
//		setup:
//		setupOrganisationUnitTree()
//		def period = newPeriod()
//		def dataElement = newRawDataElement(j(["en":"Element 1"]), CODE(1), Type.TYPE_NUMBER())
//		def survey = SurveyIntegrationTests.newSurvey(period)
//		def objective = SurveyIntegrationTests.newSurveyObjective(survey, 1, [(HEALTH_CENTER_GROUP)])
//		def section = SurveyIntegrationTests.newSurveySection(objective, 1, [(HEALTH_CENTER_GROUP)])
//		def question = SurveyIntegrationTests.newSimpleQuestion(section, 1, [(HEALTH_CENTER_GROUP)])
//		def element = SurveyIntegrationTests.newSurveyElement(question, dataElement);
//		SurveyIntegrationTests.newSurveyEnteredValue(element, period, OrganisationUnit.findByName(BUTARO), v("1"))
//		rawDataElementController = new RawDataElementController()
//
//		expect:
//		RawDataElement.count() == 1
//		SurveyElement.count() == 1
//		SurveyEnteredValue.count() == 1
//				
//		when:
//		rawDataElementController.params.id = dataElement.id
//		rawDataElementController.delete()
//		
//		then:
//		RawDataElement.count() == 0
//		SurveyElement.count() == 0
//		SurveyEnteredValue.count() == 0
//	}
//
//	def "search data element"() {
//		setup:
//		rawDataElementController = new RawDataElementController()
//
//		when:
//		def dataElement = newRawDataElement(j(["en":"Element 1"]), CODE(1), Type.TYPE_NUMBER())
//		rawDataElementController.params.q = "ele"
//		rawDataElementController.search()
//
//		then:
//		rawDataElementController.modelAndView.model.entities.size() == 1
//		rawDataElementController.modelAndView.model.entities[0].equals(dataElement)
//		rawDataElementController.modelAndView.model.entityCount == 1
//	}
//
//	def "get data element explainer"() {
//		setup:
//		rawDataElementController = new RawDataElementController()
//		
//		when:
//		def dataElement = newRawDataElement(j(["en":"Element 1"]), CODE(1), Type.TYPE_NUMBER())
//		rawDataElementController.params.id = dataElement.id+''
//		rawDataElementController.getExplainer()
//
//		then:
//		rawDataElementController.modelAndView.model.rawDataElement.equals(dataElement)
//		rawDataElementController.modelAndView.model.periodValues.isEmpty()
//	}
	
}
