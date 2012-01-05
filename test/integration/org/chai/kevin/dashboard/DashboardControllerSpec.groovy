package org.chai.kevin.dashboard

import org.chai.kevin.location.DataEntityType;
import org.chai.kevin.location.LocationEntity;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;


class DashboardControllerSpec extends DashboardIntegrationTests {

	def dashboardController
	
	def "get dashboard"() {
		setup:
		def period = newPeriod()
		setupLocationTree()
		def root = newDashboardObjective(CODE(1))
		def calculation = newAverage("1", CODE(2))
		def target = newDashboardTarget(TARGET1, calculation, root, 1)
		dashboardController = new DashboardController()
		
		when:
		dashboardController.params.organisation = LocationEntity.findByCode(RWANDA).id
		dashboardController.params.objective = root.id
		dashboardController.params.period = period.id
		dashboardController.params.groupUuids = [DataEntityType.findByCode(DISTRICT_HOSPITAL_GROUP).id]
		def model = dashboardController.view()
		
		then:
		model.currentObjective.equals(root)
		model.currentPeriod.equals(period)
		model.currentOrganisation.equals(LocationEntity.findByCode(RWANDA))
		model.currentFacilityTypes.equals([DataEntityType.findByCode(DISTRICT_HOSPITAL_GROUP)])
		model.dashboard != null
	}
	
	
	def "get explainer"() {
		setup:
		def period = newPeriod()
		setupLocationTree()
		def root = newDashboardObjective(CODE(1))
		def calculation = newAverage("1", CODE(2))
		def target = newDashboardTarget(TARGET1, calculation, root, 1)
		dashboardController = new DashboardController()
		
		when:
		dashboardController.params.organisation = LocationEntity.findByCode(RWANDA).id
		dashboardController.params.objective = root.id
		dashboardController.params.period = period.id
		dashboardController.params.groupUuids = [DataEntityType.findByCode(DISTRICT_HOSPITAL_GROUP).id]
		def model = dashboardController.explain()
		
		then:
		model.entry.equals(root)
		model.info != null
	}
	
}
