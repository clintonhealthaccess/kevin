package org.chai.kevin.maps

class MapsTargetControllerSpec extends MapsIntegrationTests {

	def mapsTargetController
	def organisationService
	
	def "save target"() {
		setup:
		def average = newAverage("1", CODE(3))
		mapsTargetController = new MapsTargetController()
		mapsTargetController.organisationService = organisationService
		
		when:
		mapsTargetController.params.code = CODE(2)
		mapsTargetController.params['calculation.id'] = average.id+""
		mapsTargetController.params.groupUuids = [DISTRICT_HOSPITAL_GROUP]
		mapsTargetController.saveWithoutTokenCheck()
		
		then:
		MapsTarget.count() == 1
		MapsTarget.list()[0].calculation.equals(average)
	}
	
//	def "create target does not show sums"() {
//		setup:
//		setupOrganisationUnitTree()
//		def sum = newSum("1", CODE(1))
//		def average = newAverage("1", CODE(2))
//		mapsTargetController = new MapsTargetController()
//		mapsTargetController.organisationService = organisationService
//		
//		when:
//		mapsTargetController.create()
//		
//		then:
//		mapsTargetController.modelAndView.model.calculations.equals([average])
//		
//	}
	
}
