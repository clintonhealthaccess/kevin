package org.chai.kevin.data;

import org.chai.kevin.IntegrationTests;
import org.chai.kevin.data.RawDataElement;
import org.chai.kevin.data.RawDataElementController;
import org.chai.kevin.data.Type;
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
		rawDataElementController.params['type.jsonValue'] = Type.TYPE_BOOL().getJsonValue()
		rawDataElementController.saveWithoutTokenCheck()

		then:
		rawDataElementController.response.redirectedUrl.equals(rawDataElementController.getTargetURI())
	}

	def "can delete data element"() {
		setup:
		rawDataElementController = new RawDataElementController()
		def organisation = newOrganisationUnit(BUTARO)
		def period = newPeriod()
		def dataElement = null

		when:
		dataElement = newRawDataElement(CODE(1), Type.TYPE_NUMBER())
		rawDataElementController.params.id = dataElement.id
		rawDataElementController.delete()

		then:
		RawDataElement.count() == 0
	}

	def "cannot delete data element when it still has values"() {
		setup:
		rawDataElementController = new RawDataElementController()
		def organisation = newOrganisationUnit(BUTARO)
		def period = newPeriod()
		def dataElement = null

		when:
		dataElement = newRawDataElement(CODE(2), Type.TYPE_NUMBER())
		newRawDataElementValue(dataElement, period, organisation, Value.NULL)
		rawDataElementController.params.id = dataElement.id
		rawDataElementController.delete()

		then:
		//		rawDataElementController.response.contentAsString.contains("success")
		RawDataElement.count() == 1
		RawDataElementValue.count() == 1
	}

	def "cannot edit data element type if it still has values" () {
		setup:
		rawDataElementController = new RawDataElementController()
		def organisation = newOrganisationUnit(BUTARO)
		def period = newPeriod()
		def dataElement = newRawDataElement(CODE(1), Type.TYPE_NUMBER())

		when:
		rawDataElementController.params.id = dataElement.id
		rawDataElementController.params['type.jsonValue'] = Type.TYPE_BOOL().getJsonValue()
		rawDataElementController.saveWithoutTokenCheck()

		then:
		rawDataElementController.response.redirectedUrl.equals(rawDataElementController.getTargetURI())
		dataElement.type.equals(Type.TYPE_BOOL())

		when:
		newRawDataElementValue(dataElement, period, organisation, Value.NULL)
		rawDataElementController.params.id = dataElement.id
		rawDataElementController.params['type.jsonValue'] = Type.TYPE_STRING().getJsonValue()
		rawDataElementController.saveWithoutTokenCheck()

		then:
		//		rawDataElementController.response.contentAsString.contains("success")
		(Type.TYPE_BOOL()).equals(dataElement.type)
	}

	def "search data element"() {
		setup:
		rawDataElementController = new RawDataElementController()

		when:
		def dataElement = newRawDataElement(j(["en":"Element 1"]), CODE(1), Type.TYPE_NUMBER())
		rawDataElementController.params.q = "ele"
		rawDataElementController.search()

		then:
		rawDataElementController.modelAndView.model.entities.size() == 1
		rawDataElementController.modelAndView.model.entities[0].equals(dataElement)
		rawDataElementController.modelAndView.model.entityCount == 1
	}

	def "get data element explainer"() {
		rawDataElementController = new RawDataElementController()
		when:
		def dataElement = newRawDataElement(j(["en":"Element 1"]), CODE(1), Type.TYPE_NUMBER())
		rawDataElementController.params.id = dataElement.id+''
		rawDataElementController.getExplainer()

		then:
		rawDataElementController.modelAndView.model.rawDataElement.equals(dataElement)
		rawDataElementController.modelAndView.model.periodValues.isEmpty()
	}
}
