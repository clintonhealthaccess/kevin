package org.chai.kevin;

import static org.junit.Assert.*;

import org.chai.kevin.data.DataElement;
import org.chai.kevin.data.DataElementController;
import org.chai.kevin.data.Type;
import org.chai.kevin.value.DataValue;

class DataElementControllerSpec extends IntegrationTests {

	def dataElementController

	def "cannot delete data element when it still has values"() {
		setup:
		dataElementController = new DataElementController()
		def dataElement = newDataElement(CODE(1), Type.TYPE_NUMBER)
		def organisation = newOrganisationUnit(BUTARO)
		def period = newPeriod()
		
		when:
		dataElementController.params.id = dataElement.id
		dataElementController.delete()
		
		then:
		DataElement.count() == 0
		
		when:
		newDataValue(dataElement, period, organisation)
		dataElementController.params.id = dataElement.id
		dataElementController.delete()
		
		then:
		DataElement.count() == 1
		DataValue.count() == 1
	}
	
	def "get data elements"() {
		setup:
		dataElementController = new DataElementController()
		
		when:
		newDataElement(j(["en":"Element 1"]), CODE(1), Type.TYPE_NUMBER)
		
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
		def dataElement = newDataElement(j(["en":"Element 1"]), CODE(1), Type.TYPE_NUMBER)
		dataElementController.params.dataElement = dataElement.id+""
		def model = dataElementController.getDataElementDescription()
		
		then:
		dataElementController.response.contentAsString.contains("success")
		dataElementController.response.contentAsString.contains("Description")
		
	}
}
