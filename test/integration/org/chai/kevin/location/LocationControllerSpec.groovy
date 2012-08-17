package org.chai.kevin.location

import org.chai.kevin.IntegrationTests;
import org.chai.kevin.util.JSONUtils;

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
	
	def "get ajax data with class - Location"() {
		setup:
		setupLocationTree()
		locationController = new LocationController()
		def jsonResult
		
		when:
		locationController.params.term = ''
		locationController.params['class'] = 'Location'
		locationController.getAjaxData()
		jsonResult = JSONUtils.getMapFromJSON(locationController.response.contentAsString)
	
		then:
		jsonResult.elements.size() == 3
	}
	
	def "get ajax data with class - DataLocation"() {
		setup:
		setupLocationTree()
		locationController = new LocationController()
		def jsonResult
	
		when:
		locationController.params.term = ''
		locationController.params['class'] = 'DataLocation'
		locationController.getAjaxData()
		jsonResult = JSONUtils.getMapFromJSON(locationController.response.contentAsString)
		
		then:
		jsonResult.elements.size() == 2
	}
	
	def "get ajax data with class - CalculationLocation"() {
		setup:
		setupLocationTree()
		locationController = new LocationController()
		def jsonResult
	
		when:
		locationController.params.term = ''
		locationController.params['class'] = 'CalculationLocation'
		locationController.getAjaxData()
		jsonResult = JSONUtils.getMapFromJSON(locationController.response.contentAsString)
		
		then:
		jsonResult.elements.size() == 5
	}
	
	
	def "test list"() {
		setup:
		setupLocationTree()
		locationController = new LocationController()
		
		when:
		locationController.list()
		
		then:
		s(locationController.modelAndView.model.entities) == s([Location.findByCode(RWANDA), Location.findByCode(NORTH), Location.findByCode(BURERA)])
	}
	
	def "test list with location"() {
		setup:
		setupLocationTree()
		locationController = new LocationController()
		
		when:
		locationController.params.parent = Location.findByCode(RWANDA).id
		locationController.list()
		
		then:
		locationController.modelAndView.model.entities == [Location.findByCode(NORTH)]
	}
	
	def "test list with type"() {
		setup:
		setupLocationTree()
		locationController = new LocationController()
		
		when:
		locationController.params.level = LocationLevel.findByCode(NATIONAL).id
		locationController.list()
		
		then:
		locationController.modelAndView.model.entities == [Location.findByCode(RWANDA)]
		
	}
	
}
