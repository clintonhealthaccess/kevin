package org.chai.kevin.dashboard

import org.hisp.dhis.organisationunit.OrganisationUnitGroup;


class DashboardControllerSpec extends DashboardIntegrationTests {

	def dashboardController
	
	def "get dashboard"() {
		setup:
		def period = newPeriod()
		setupOrganisationUnitTree()
		def root = newDashboardObjective(CODE(1))
		def calculation = newAverage("1", CODE(2))
		def target = newDashboardTarget(TARGET1, calculation, root, 1)
		dashboardController = new DashboardController()
		
		when:
		dashboardController.params.organisation = getOrganisation(RWANDA).id
		dashboardController.params.objective = root.id
		dashboardController.params.period = period.id
		dashboardController.params.groupUuids = [DISTRICT_HOSPITAL_GROUP]
		def model = dashboardController.view()
		
		then:
		model.currentObjective.equals(root)
		model.currentPeriod.equals(period)
		model.currentOrganisation.equals(getOrganisation(RWANDA))
		model.currentFacilityTypes.equals([OrganisationUnitGroup.findByUuid(DISTRICT_HOSPITAL_GROUP)])
		model.dashboard != null
	}
	
	
	def "get explainer"() {
		setup:
		def period = newPeriod()
		setupOrganisationUnitTree()
		def root = newDashboardObjective(CODE(1))
		def calculation = newAverage("1", CODE(2))
		def target = newDashboardTarget(TARGET1, calculation, root, 1)
		dashboardController = new DashboardController()
		
		when:
		dashboardController.params.organisation = getOrganisation(RWANDA).id
		dashboardController.params.objective = root.id
		dashboardController.params.period = period.id
		dashboardController.params.groupUuids = [DISTRICT_HOSPITAL_GROUP]
		def model = dashboardController.explain()
		
		then:
		model.entry.equals(root)
		model.info != null
	}
	
}
