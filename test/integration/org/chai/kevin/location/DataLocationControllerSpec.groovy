package org.chai.kevin.location

import org.chai.kevin.IntegrationTests;

class DataLocationControllerSpec extends IntegrationTests {

	def dataLocationController
	
	def "test list"() {
		setup:
		setupLocationTree()
		dataLocationController = new DataLocationController()
		
		when:
		dataLocationController.list()
		
		then:
		s(dataLocationController.modelAndView.model.entities) == s([DataLocation.findByCode(BUTARO), DataLocation.findByCode(KIVUYE)])
	}
	
	def "test list with location"() {
		setup:
		setupLocationTree()
		dataLocationController = new DataLocationController()
		
		when:
		dataLocationController.params.location = Location.findByCode(BURERA).id
		dataLocationController.list()
		
		then:
		s(dataLocationController.modelAndView.model.entities) == s([DataLocation.findByCode(BUTARO), DataLocation.findByCode(KIVUYE)])
		
		when:
		dataLocationController.params.location = Location.findByCode(RWANDA).id
		dataLocationController.list()
		
		then:
		dataLocationController.modelAndView.model.entities == []
	}
	
	def "test list with type"() {
		setup:
		setupLocationTree()
		dataLocationController = new DataLocationController()
		
		when:
		dataLocationController.params.type = DataLocationType.findByCode(HEALTH_CENTER_GROUP).id
		dataLocationController.list()
		
		then:
		dataLocationController.modelAndView.model.entities == [DataLocation.findByCode(KIVUYE)]
	}
	
}
