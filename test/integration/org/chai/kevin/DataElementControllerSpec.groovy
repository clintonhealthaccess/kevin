package org.chai.kevin;

import org.chai.kevin.data.RawDataElement;
import org.chai.kevin.data.DataElementController;
import org.chai.kevin.data.Type;
import org.chai.kevin.value.RawDataElementValue;
import org.chai.kevin.value.Value;

class DataElementControllerSpec extends IntegrationTests {

	def dataElementController

	def "create new data element"() {
		setup:
		dataElementController = new DataElementController()
		
		when:
		dataElementController.create()
		
		then:
		dataElementController.modelAndView.model.dataElement.id == null
	}
	
	def "save new data element"() {
		setup:
		dataElementController = new DataElementController()
		
		when:
		dataElementController.params.code = CODE(1)
		dataElementController.params['type.jsonValue'] = Type.TYPE_BOOL().getJsonValue()
		dataElementController.saveWithoutTokenCheck()
		
		then:
		dataElementController.response.redirectedUrl.equals(dataElementController.getTargetURI())
	}
	
	def "can delete data element"() {
		setup:
		dataElementController = new DataElementController()
		def organisation = newOrganisationUnit(BUTARO)
		def period = newPeriod()
		def dataElement = null
		
		when:
		dataElement = newRawDataElement(CODE(1), Type.TYPE_NUMBER())
		dataElementController.params.id = dataElement.id
		dataElementController.delete()
		
		then:
		RawDataElement.count() == 0
	}
		
	def "cannot delete data element when it still has values"() {
		setup:
		dataElementController = new DataElementController()
		def organisation = newOrganisationUnit(BUTARO)
		def period = newPeriod()
		def dataElement = null
		
		when:
		dataElement = newRawDataElement(CODE(2), Type.TYPE_NUMBER())
		newRawDataElementValue(dataElement, period, organisation, Value.NULL)
		dataElementController.params.id = dataElement.id
		dataElementController.delete()
		
		then:
//		dataElementController.response.contentAsString.contains("success")
		RawDataElement.count() == 1
		RawDataElementValue.count() == 1
	}
	
	def "cannot edit data element type if it still has values" () {
		setup:
		dataElementController = new DataElementController()
		def organisation = newOrganisationUnit(BUTARO)
		def period = newPeriod()
		def dataElement = newRawDataElement(CODE(1), Type.TYPE_NUMBER())
	
		when:
		dataElementController.params.id = dataElement.id
		dataElementController.params['type.jsonValue'] = Type.TYPE_BOOL().getJsonValue()
		dataElementController.saveWithoutTokenCheck()
		
		then:
		dataElementController.response.redirectedUrl.equals(dataElementController.getTargetURI())
		dataElement.type.equals(Type.TYPE_BOOL())
		
		when:
		newRawDataElementValue(dataElement, period, organisation, Value.NULL)
		dataElementController.params.id = dataElement.id
		dataElementController.params['type.jsonValue'] = Type.TYPE_STRING().getJsonValue()
		dataElementController.saveWithoutTokenCheck()
		
		then:
//		dataElementController.response.contentAsString.contains("success")
		(Type.TYPE_BOOL()).equals(dataElement.type)
	}
	
	def "get data elements"() {
		setup:
		dataElementController = new DataElementController()
		
		when:
		newRawDataElement(j(["en":"Element 1"]), CODE(1), Type.TYPE_NUMBER())
		
		dataElementController.params.searchText = 'ele'
		def model = dataElementController.getData()
		
		then:
		dataElementController.response.contentAsString.contains("success")
		dataElementController.response.contentAsString.contains("Element 1")
		
	}
		
	def "get data element description"() {
		setup:
		dataElementController = new DataElementController()
		
		when:
		def dataElement = newRawDataElement(j(["en":"Element 1"]), CODE(1), Type.TYPE_NUMBER())
		dataElementController.params.dataElement = dataElement.id+""
		def model = dataElementController.getDescription()
		
		then:
		dataElementController.response.contentAsString.contains("success")
		dataElementController.response.contentAsString.contains("number")
		
	}
	
	def "search data element"() {
		setup:
		dataElementController = new DataElementController()
		
		when:
		def dataElement = newRawDataElement(j(["en":"Element 1"]), CODE(1), Type.TYPE_NUMBER())
		dataElementController.params.q = "ele"
		dataElementController.search()
		
		then:
		dataElementController.modelAndView.model.entities.size() == 1
		dataElementController.modelAndView.model.entities[0].equals(dataElement)
		dataElementController.modelAndView.model.entityCount == 1
		
	}
}
