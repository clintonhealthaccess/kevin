package org.chai.kevin.location

import org.chai.kevin.IntegrationTests;

class DataEntityTypeControllerSpec extends IntegrationTests {

	def dataEntityTypeController
	
	def "test cannot delete when associated locations"() {
		setup:
		setupLocationTree()
		dataEntityTypeController = new DataEntityTypeController()
		
		when:
		dataEntityTypeController.params.id = DataEntityType.findByCode(HEALTH_CENTER_GROUP).id
		dataEntityTypeController.delete()
		
		then:
		DataEntityType.findByCode(HEALTH_CENTER_GROUP) != null
		DataEntityType.count() == 2
	}
	
	
	def "test can delete when no locations"() {
		setup:
		def province = newDataEntityType(HEALTH_CENTER_GROUP)
		dataEntityTypeController = new DataEntityTypeController()
		
		when:
		dataEntityTypeController.params.id = DataEntityType.findByCode(HEALTH_CENTER_GROUP).id
		dataEntityTypeController.delete()
		
		then:
		DataEntityType.findByCode(HEALTH_CENTER_GROUP) == null
		DataEntityType.count() == 0
	}
	
}
