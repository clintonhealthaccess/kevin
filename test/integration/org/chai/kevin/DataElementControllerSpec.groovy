package org.chai.kevin;

import static org.junit.Assert.*;

import org.chai.kevin.data.DataElement;
import org.chai.kevin.data.DataElementController;
import org.chai.kevin.data.Type;
import org.chai.kevin.value.DataValue;
import org.chai.kevin.value.Value;

class DataElementControllerSpec extends IntegrationTests {

	def dataElementController

	def "cannot delete data element when it still has values"() {
		setup:
		dataElementController = new DataElementController()
		def organisation = newOrganisationUnit(BUTARO)
		def period = newPeriod()
		def dataElement = null
		
		when:
		dataElement = newDataElement(CODE(1), Type.TYPE_NUMBER())
		dataElementController.params.id = dataElement.id
		dataElementController.delete()
		
		then:
		DataElement.count() == 0
		
		when:
		dataElement = newDataElement(CODE(1), Type.TYPE_NUMBER())
		newDataValue(dataElement, period, organisation, Value.NULL)
		dataElementController.params.id = dataElement.id
		dataElementController.delete()
		
		then:
//		dataElementController.response.contentAsString.contains("success")
		DataElement.count() == 1
		DataValue.count() == 1
	}
	
	def "cannot edit data element type if it still has values" () {
		setup:
		dataElementController = new DataElementController()
		def organisation = newOrganisationUnit(BUTARO)
		def period = newPeriod()
		def dataElement = newDataElement(CODE(1), Type.TYPE_NUMBER())
	
		when:
		dataElementController.params.id = dataElement.id
		dataElementController.params['type.jsonType'] = Type.TYPE_BOOL().getJsonType()
		dataElementController.saveWithoutTokenCheck()
		
		then:
		dataElementController.response.contentAsString.contains("success")
		dataElement.type.equals(Type.TYPE_BOOL())
		
		when:
		newDataValue(dataElement, period, organisation, Value.NULL)
		dataElementController.params.id = dataElement.id
		dataElementController.params['type.jsonType'] = Type.TYPE_STRING().getJsonType()
		dataElementController.saveWithoutTokenCheck()
		
		then:
//		dataElementController.response.contentAsString.contains("success")
		(Type.TYPE_BOOL()).equals(dataElement.type)
	}
	
	def "get data elements"() {
		setup:
		dataElementController = new DataElementController()
		
		when:
		newDataElement(j(["en":"Element 1"]), CODE(1), Type.TYPE_NUMBER())
		
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
		def dataElement = newDataElement(j(["en":"Element 1"]), CODE(1), Type.TYPE_NUMBER())
		dataElementController.params.dataElement = dataElement.id+""
		def model = dataElementController.getDescription()
		
		then:
		dataElementController.response.contentAsString.contains("success")
		dataElementController.response.contentAsString.contains("number")
		
	}
}
