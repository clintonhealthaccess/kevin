package org.chai.kevin.maps

class MapsTargetControllerSpec extends MapsIntegrationTests {

	def mapsTargetController
	def locationService
	
	def "save target"() {
		setup:
		def ratio = newSum("1", CODE(3))
		mapsTargetController = new MapsTargetController()
		mapsTargetController.locationService = locationService
		
		when:
		mapsTargetController.params.code = CODE(2)
		mapsTargetController.params['calculation.id'] = ratio.id+""
		mapsTargetController.params.typeCodes = [DISTRICT_HOSPITAL_GROUP]
		mapsTargetController.saveWithoutTokenCheck()
		
		then:
		MapsTarget.count() == 1
		MapsTarget.list()[0].calculation.equals(ratio)
	}
	
}