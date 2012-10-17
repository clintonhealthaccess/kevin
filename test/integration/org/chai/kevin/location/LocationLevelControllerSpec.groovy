package org.chai.location

import org.chai.kevin.IntegrationTests;

class LocationLevelControllerSpec extends IntegrationTests {

	def locationLevelController
	
	def "test cannot delete when associated locations"() {
		setup:
		setupLocationTree()
		locationLevelController = new LocationLevelController()
		
		when:
		locationLevelController.params.id = LocationLevel.findByCode(DISTRICT).id
		locationLevelController.delete()
		
		then:
		LocationLevel.findByCode(DISTRICT) != null
		LocationLevel.count() == 4
	}
	
	
	def "test can delete when no locations"() {
		setup:
		def province = newLocationLevel(DISTRICT, 2)
		locationLevelController = new LocationLevelController()
		
		when:
		locationLevelController.params.id = LocationLevel.findByCode(DISTRICT).id
		locationLevelController.delete()
		
		then:
		LocationLevel.findByCode(DISTRICT) == null
		LocationLevel.count() == 0
	}
	
}
