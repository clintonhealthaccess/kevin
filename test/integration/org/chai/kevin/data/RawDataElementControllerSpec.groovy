package org.chai.kevin.data;

import org.chai.kevin.IntegrationTests;
import org.chai.kevin.data.RawDataElement;
import org.chai.kevin.data.RawDataElementController;
import org.chai.kevin.data.Type;
import org.chai.kevin.dsr.DsrIntegrationTests;
import org.chai.kevin.form.FormEnteredValue;
import org.chai.location.DataLocation;
import org.chai.location.Location;
import org.chai.kevin.survey.SurveyCheckboxOption;
import org.chai.kevin.survey.SurveyCheckboxQuestion;
import org.chai.kevin.survey.SurveyElement;
import org.chai.kevin.survey.SurveyIntegrationTests;
import org.chai.kevin.survey.SurveyQuestion;
import org.chai.kevin.value.RawDataElementValue;
import org.chai.kevin.value.Value;

class RawDataElementControllerSpec extends IntegrationTests {

	def rawDataElementController
	
	def "create new data element"() {
		setup:
		rawDataElementController = new RawDataElementController()

		when:
		rawDataElementController.create()

		then:
		rawDataElementController.modelAndView.model.rawDataElement.id == null
	}

	def "save new data element"() {
		setup:
		rawDataElementController = new RawDataElementController()

		when:
		rawDataElementController.params.code = CODE(1)
		rawDataElementController.params['typeString'] = Type.TYPE_BOOL().getJsonValue()
		rawDataElementController.saveWithoutTokenCheck()

		then:
		rawDataElementController.response.redirectedUrl.equals(rawDataElementController.getTargetURI())
	}
	
	def "edit data element"() {
		setup:
		def dataElement = newRawDataElement(CODE(2), Type.TYPE_NUMBER())
		rawDataElementController = new RawDataElementController()

		when:
		rawDataElementController.params.id = dataElement.id
		rawDataElementController.params.code = "new_code"
		rawDataElementController.params.names_en = "new_name"
		rawDataElementController.params['typeString'] = Type.TYPE_NUMBER().getJsonValue()
		rawDataElementController.saveWithoutTokenCheck()

		then:
		rawDataElementController.response.redirectedUrl.equals(rawDataElementController.getTargetURI())
		RawDataElement.list()[0].id == dataElement.id
		RawDataElement.list()[0].code == "new_code"
		RawDataElement.list()[0].names_en == "new_name"
		
	}

	def "can delete data element"() {
		setup:
		def dataElement = newRawDataElement(CODE(1), Type.TYPE_NUMBER())
		rawDataElementController = new RawDataElementController()

		when:
		rawDataElementController.params.id = dataElement.id
		rawDataElementController.delete()

		then:
		RawDataElement.count() == 0
	}

	def "cannot delete data element when it still has values"() {
		setup:
		setupLocationTree()
		rawDataElementController = new RawDataElementController()
		def dataLocation = DataLocation.findByCode(BUTARO)
		def period = newPeriod()
		def dataElement = null

		when:
		dataElement = newRawDataElement(CODE(2), Type.TYPE_NUMBER())
		newRawDataElementValue(dataElement, period, dataLocation, Value.NULL_INSTANCE())
		rawDataElementController.params.id = dataElement.id
		rawDataElementController.delete()

		then:
		//		rawDataElementController.response.contentAsString.contains("success")
		RawDataElement.count() == 1
		RawDataElementValue.count() == 1
	}

	def "can change data element type if it has no values" () {
		setup:
		setupLocationTree()
		rawDataElementController = new RawDataElementController()
		def dataLocation = DataLocation.findByCode(BUTARO)
		def period = newPeriod()
		def dataElement = newRawDataElement(CODE(1), Type.TYPE_NUMBER())

		when:
		rawDataElementController.params.id = dataElement.id
		rawDataElementController.params.code = dataElement.code
		rawDataElementController.params['typeString'] = Type.TYPE_BOOL().getJsonValue()
		rawDataElementController.saveWithoutTokenCheck()

		then:
		rawDataElementController.response.redirectedUrl.equals(rawDataElementController.getTargetURI())
		dataElement.type.equals(Type.TYPE_BOOL())
	}
		
	def "cannot change data element type if it has values" () {
		setup:
		setupLocationTree()
		rawDataElementController = new RawDataElementController()
		def dataLocation = DataLocation.findByCode(BUTARO)
		def period = newPeriod()
		def dataElement = newRawDataElement(CODE(1), Type.TYPE_NUMBER())

		when:
		newRawDataElementValue(dataElement, period, dataLocation, Value.NULL_INSTANCE())
		rawDataElementController.params.id = dataElement.id
		rawDataElementController.params.code = dataElement.code
		rawDataElementController.params['typeString'] = Type.TYPE_STRING().getJsonValue()
		rawDataElementController.saveWithoutTokenCheck()

		then:
		(Type.TYPE_NUMBER()).equals(dataElement.type)
	}

	def "can save data element if it still has values and type does not change" () {
		setup:
		setupLocationTree()
		rawDataElementController = new RawDataElementController()
		def dataLocation = DataLocation.findByCode(BUTARO)
		def period = newPeriod()
		def dataElement = newRawDataElement(CODE(1), Type.TYPE_NUMBER())
		newRawDataElementValue(dataElement, period, dataLocation, Value.NULL_INSTANCE())
		
		when:
		rawDataElementController.params.id = dataElement.id
		rawDataElementController.params.code = dataElement.code
		rawDataElementController.params['typeString'] = Type.TYPE_NUMBER().getJsonValue()
		rawDataElementController.saveWithoutTokenCheck()

		then:
		rawDataElementController.response.redirectedUrl.equals(rawDataElementController.getTargetURI())
		dataElement.type.equals(Type.TYPE_NUMBER())
	}
	
	def "not changing data element type does not delete survey entered values"() {
		setup:
		setupLocationTree()
		def period = newPeriod()
		def dataElement = newRawDataElement(["en":"Element 1"], CODE(1), Type.TYPE_NUMBER())
		def survey = SurveyIntegrationTests.newSurvey(CODE(1), period)
		def program = SurveyIntegrationTests.newSurveyProgram(CODE(1), survey, 1, [(HEALTH_CENTER_GROUP)])
		def section = SurveyIntegrationTests.newSurveySection(CODE(1), program, 1, [(HEALTH_CENTER_GROUP)])
		def question = SurveyIntegrationTests.newSimpleQuestion(CODE(1), section, 1, [(HEALTH_CENTER_GROUP)])
		def element = SurveyIntegrationTests.newSurveyElement(question, dataElement);
		SurveyIntegrationTests.newFormEnteredValue(element, period, DataLocation.findByCode(BUTARO), v("1"))

		expect:
		RawDataElement.count() == 1
		SurveyElement.count() == 1
		FormEnteredValue.count() == 1
		
		when:
		rawDataElementController = new RawDataElementController()
		rawDataElementController.params.id = dataElement.id
		rawDataElementController.params.code = dataElement.code
		rawDataElementController.params['typeString'] = Type.TYPE_NUMBER().getJsonValue()
		rawDataElementController.saveWithoutTokenCheck()
		
		then:
		RawDataElement.count() == 1
		SurveyElement.count() == 1
		FormEnteredValue.count() == 1
	}
		
	def "changing data element type deletes survey entered values"() {
		setup:
		setupLocationTree()
		def period = newPeriod()
		def dataElement = newRawDataElement(["en":"Element 1"], CODE(27), Type.TYPE_NUMBER())
		def survey = SurveyIntegrationTests.newSurvey(CODE(1), period)
		def program = SurveyIntegrationTests.newSurveyProgram(CODE(1), survey, 1, [(HEALTH_CENTER_GROUP)])
		def section = SurveyIntegrationTests.newSurveySection(CODE(1), program, 1, [(HEALTH_CENTER_GROUP)])
		def question = SurveyIntegrationTests.newSimpleQuestion(CODE(1), section, 1, [(HEALTH_CENTER_GROUP)])
		def element = SurveyIntegrationTests.newSurveyElement(question, dataElement);
		SurveyIntegrationTests.newFormEnteredValue(element, period, DataLocation.findByCode(BUTARO), v("1"))

		expect:
		RawDataElement.count() == 1
		SurveyElement.count() == 1
		FormEnteredValue.count() == 1
		
		when:
		rawDataElementController = new RawDataElementController()
		rawDataElementController.params.id = dataElement.id
		rawDataElementController.params.code = dataElement.code
		rawDataElementController.params['typeString'] = Type.TYPE_BOOL().getJsonValue()
		rawDataElementController.saveWithoutTokenCheck()
		
		then:
		RawDataElement.count() == 1
		SurveyElement.count() == 1
		FormEnteredValue.count() == 0
	}
	
	def "cannot delete data element if referencing data"() {
		setup:
		setupLocationTree()
		def period = newPeriod()
		def dataElement = newRawDataElement(["en":"Element 1"], CODE(1), Type.TYPE_NUMBER())
		def normalizedDataElement = newNormalizedDataElement(CODE(2), Type.TYPE_NUMBER(), ["1":[DISTRICT_HOSPITAL_GROUP:"\$"+dataElement.id]])
		rawDataElementController = new RawDataElementController()
		
		when:
		rawDataElementController.params.id = dataElement.id
		rawDataElementController.delete()
		
		then:
		NormalizedDataElement.count() == 1
		RawDataElement.count() == 1
	}

	def "cannot delete data element if there are associated targets"() {
		setup:
		def program = newReportProgram(CODE(1))
		def targetCategory = DsrIntegrationTests.newDsrTargetCategory(CODE(2), program, 1)
		def dataElement = newRawDataElement(["en":"Element 1"], CODE(1), Type.TYPE_NUMBER())
		def target = DsrIntegrationTests.newDsrTarget(CODE(3), dataElement, targetCategory)
		rawDataElementController = new RawDataElementController()
		
		when:
		rawDataElementController.params.id = dataElement.id
		rawDataElementController.delete()
		
		then:
		RawDataElement.count() == 1
	}
	
	def "delete data element deletes survey element and survey entered values"() {
		setup:
		setupLocationTree()
		def period = newPeriod()
		def dataElement = newRawDataElement(["en":"Element 1"], CODE(1), Type.TYPE_NUMBER())
		def survey = SurveyIntegrationTests.newSurvey(CODE(1), period)
		def program = SurveyIntegrationTests.newSurveyProgram(CODE(1), survey, 1, [(HEALTH_CENTER_GROUP)])
		def section = SurveyIntegrationTests.newSurveySection(CODE(1), program, 1, [(HEALTH_CENTER_GROUP)])
		def question = SurveyIntegrationTests.newSimpleQuestion(CODE(1), section, 1, [(HEALTH_CENTER_GROUP)])
		def element = SurveyIntegrationTests.newSurveyElement(question, dataElement);
		SurveyIntegrationTests.newFormEnteredValue(element, period, DataLocation.findByCode(BUTARO), v("1"))
		rawDataElementController = new RawDataElementController()

		expect:
		RawDataElement.count() == 1
		SurveyElement.count() == 1
		FormEnteredValue.count() == 1
				
		when:
		rawDataElementController.params.id = dataElement.id
		rawDataElementController.delete()
		
		then:
		RawDataElement.count() == 0
		SurveyElement.count() == 0
		FormEnteredValue.count() == 0
	}

	def "search data element"() {
		setup:
		rawDataElementController = new RawDataElementController()

		when:
		def dataElement = newRawDataElement(["en":"Element 1"], CODE(1), Type.TYPE_NUMBER())
		rawDataElementController.params.q = "ele"
		rawDataElementController.search()

		then:
		rawDataElementController.modelAndView.model.entities.size() == 1
		rawDataElementController.modelAndView.model.entities[0].equals(dataElement)
		rawDataElementController.modelAndView.model.entityCount == 1
	}

	def "delete data element with survey checkbox and no attached elements"() {
		setup:
		setupLocationTree()
		def period = newPeriod()
		def dataElement = newRawDataElement(["en":"Element 1"], CODE(27), Type.TYPE_BOOL())
		def survey = SurveyIntegrationTests.newSurvey(CODE(1), period)
		def program = SurveyIntegrationTests.newSurveyProgram(CODE(1), survey, 1, [(HEALTH_CENTER_GROUP)])
		def section = SurveyIntegrationTests.newSurveySection(CODE(1), program, 1, [(HEALTH_CENTER_GROUP)])
		def question = SurveyIntegrationTests.newCheckboxQuestion(CODE(1), section, 1, [(HEALTH_CENTER_GROUP)])
		def option2 = SurveyIntegrationTests.newCheckboxOption(CODE(1), question, 1, [(HEALTH_CENTER_GROUP)])
		def element = SurveyIntegrationTests.newSurveyElement(question, dataElement);
		def option1 = SurveyIntegrationTests.newCheckboxOption(CODE(2), question, 1, [(HEALTH_CENTER_GROUP)], element)
		rawDataElementController = new RawDataElementController()
		
		when:
		rawDataElementController.params.id = dataElement.id
		rawDataElementController.delete()
		
		then:
		SurveyElement.count() == 0
		SurveyCheckboxQuestion.count() == 1
		SurveyCheckboxOption.count() == 2
		RawDataElement.count() == 0
	}
	
}
