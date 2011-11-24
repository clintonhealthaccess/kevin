package org.chai.kevin.data;

import org.chai.kevin.IntegrationTests;

class DataControllerSpec extends IntegrationTests {
	
	def dataController
	
	def "get data elements"() {
		setup:
		newRawDataElement(j(["en":"Element 1"]), CODE(1), Type.TYPE_NUMBER())
		
		when:
		dataController = new DataController()
		dataController.params.searchText = 'ele'
		dataController.params['class'] = 'RawDataElement'
		def model = dataController.getData()

		then:
		dataController.response.contentAsString.contains("success")
		dataController.response.contentAsString.contains("Element 1")
	}

	def "get data element description"() {
		setup:
		def dataElement = newRawDataElement(j(["en":"Element 1"]), CODE(1), Type.TYPE_NUMBER())

		when:
		dataController = new DataController()
		dataController.params.id = dataElement.id+""
		def model = dataController.getDescription()

		then:
		dataController.response.contentAsString.contains("success")
		dataController.response.contentAsString.contains("number")
	}

	
}
