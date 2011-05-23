package org.chai.kevin

import org.hisp.dhis.dataelement.Constant;
import org.chai.kevin.DataElement;

import grails.plugin.spock.UnitSpec;

class ExpressionControllerSpec extends IntegrationTests {

	def expressionController

	def "get constants"() {
		
		setup:
		Initializer.createDummyStructure()
		IntegrationTestInitializer.createConstants()
		expressionController = new ExpressionController()
		
		when:
		expressionController.params.constant = 'con'
		expressionController.params.type = 'constant'
		def model = expressionController.getData()
		
		then:
		expressionController.response.contentAsString.contains("success")
		expressionController.response.contentAsString.contains("Constant 1000")
		
	}
	
	def "get constant description"() {
		
		setup:
		Initializer.createDummyStructure()
		IntegrationTestInitializer.createConstants()
		expressionController = new ExpressionController()
		
		when:
		expressionController.params.constant = Constant.findByName("Constant 1000").id+''
		def model = expressionController.getConstantDescription()
		
		then:
		expressionController.response.contentAsString.contains("success")
		expressionController.response.contentAsString.contains("Description")
		
	}
	
	def "get data elements"() {
		
		setup:
		Initializer.createDummyStructure()
		IntegrationTestInitializer.createDataElements()
		expressionController = new ExpressionController()
		
		when:
		expressionController.params.constant = 'ele'
		expressionController.params.type = 'data-element'
		def model = expressionController.getData()
		
		then:
		expressionController.response.contentAsString.contains("success")
		expressionController.response.contentAsString.contains("Element 1")
		
	}
		
	def "get data element description"() {
		
		setup:
		Initializer.createDummyStructure()
		IntegrationTestInitializer.createDataElements()
		expressionController = new ExpressionController()
		
		when:
		expressionController.params.dataElement = DataElement.findByName("Element 1").id+''
		def model = expressionController.getDataElementDescription()
		
		then:
		expressionController.response.contentAsString.contains("success")
		expressionController.response.contentAsString.contains("Description")
		
	}
	
}
