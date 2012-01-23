package org.chai.kevin.location

import org.chai.kevin.IntegrationTests;

class LocationControllerSpec extends IntegrationTests {

	def locationController
	
	def "test cannot delete when children"() {
		setup:
		setupLocationTree()
		locationController = new LocationController()
		
		when:
		locationController.params.id = LocationEntity.findByCode(RWANDA).id
		locationController.delete()
		
		then:
		LocationEntity.findByCode(RWANDA) != null
		LocationEntity.count() == 3
	}
	
	def "test cannot delete when data entities"() {
		setup:
		setupLocationTree()
		locationController = new LocationController()
		
		when:
		locationController.params.id = LocationEntity.findByCode(BURERA).id
		locationController.delete()
		
		then:
		LocationEntity.findByCode(BURERA) != null
		LocationEntity.count() == 3
	}
	
	def "test can delete when no children"() {
		setup:
		def country = newLocationLevel(COUNTRY, 1)
		newLocationEntity(RWANDA, country)
		locationController = new LocationController()
		
		when:
		locationController.params.id = LocationEntity.findByCode(RWANDA).id
		locationController.delete()
		
		then:
		LocationEntity.findByCode(RWANDA) == null
		LocationEntity.count() == 0
	}
	
}
