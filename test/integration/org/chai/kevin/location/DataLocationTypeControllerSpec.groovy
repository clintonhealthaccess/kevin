package org.chai.kevin.location

import org.chai.kevin.IntegrationTests;

class DataLocationTypeControllerSpec extends IntegrationTests {

	def dataLocationTypeController
	
	def "test cannot delete when associated locations"() {
		setup:
		setupLocationTree()
		dataLocationTypeController = new DataLocationTypeController()
		
		when:
		dataLocationTypeController.params.id = DataLocationType.findByCode(HEALTH_CENTER_GROUP).id
		dataLocationTypeController.delete()
		
		then:
		DataLocationType.findByCode(HEALTH_CENTER_GROUP) != null
		DataLocationType.count() == 2
	}
	
	
	def "test can delete when no locations"() {
		setup:
		def province = newDataLocationType(HEALTH_CENTER_GROUP)
		dataLocationTypeController = new DataLocationTypeController()
		
		when:
		dataLocationTypeController.params.id = DataLocationType.findByCode(HEALTH_CENTER_GROUP).id
		dataLocationTypeController.delete()
		
		then:
		DataLocationType.findByCode(HEALTH_CENTER_GROUP) == null
		DataLocationType.count() == 0
	}
	
}
