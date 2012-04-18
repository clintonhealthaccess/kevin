package org.chai.kevin.location

import org.chai.kevin.IntegrationTests;

class LocationControllerSpec extends IntegrationTests {

	def locationController
	
	def "test cannot delete when children"() {
		setup:
		setupLocationTree()
		locationController = new LocationController()
		
		when:
		locationController.params.id = Location.findByCode(RWANDA).id
		locationController.delete()
		
		then:
		Location.findByCode(RWANDA) != null
		Location.count() == 3
	}
	
	def "test cannot delete when data locations"() {
		setup:
		setupLocationTree()
		locationController = new LocationController()
		
		when:
		locationController.params.id = Location.findByCode(BURERA).id
		locationController.delete()
		
		then:
		Location.findByCode(BURERA) != null
		Location.count() == 3
	}
	
	def "test can delete when no children"() {
		setup:
		def country = newLocationLevel(NATIONAL, 1)
		newLocation(RWANDA, country)
		locationController = new LocationController()
		
		when:
		locationController.params.id = Location.findByCode(RWANDA).id
		locationController.delete()
		
		then:
		Location.findByCode(RWANDA) == null
		Location.count() == 0
	}
	
}
