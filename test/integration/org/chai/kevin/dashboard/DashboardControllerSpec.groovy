package org.chai.kevin.dashboard

import org.chai.kevin.location.DataEntityType;
import org.chai.kevin.location.LocationEntity;


class DashboardControllerSpec extends DashboardIntegrationTests {

	def dashboardController
	
	def "get dashboard"() {
		setup:
		def period = newPeriod()
		setupLocationTree()
		def root = newReportProgram(CODE(1))
		def dashboardRoot = newDashboardProgram(CODE(3), root)
		def calculation = newAverage("1", CODE(2))
		def target = newDashboardTarget(TARGET1, calculation, root, 1)
		dashboardController = new DashboardController()
		
		when:
		dashboardController.params.location = LocationEntity.findByCode(RWANDA).id
		dashboardController.params.program = root.id
		dashboardController.params.period = period.id
		dashboardController.params.locationTypes = [DataEntityType.findByCode(DISTRICT_HOSPITAL_GROUP).id]
		def model = dashboardController.view()
		
		then:
		model.dashboardEntity.equals(dashboardRoot)
		model.currentPeriod.equals(period)
		model.currentLocation.equals(LocationEntity.findByCode(RWANDA))
		model.currentLocationTypes.equals(s([DataEntityType.findByCode(DISTRICT_HOSPITAL_GROUP)]))
		model.programDashboard != null
		model.locationDashboard != null
	}
	
	def "get root dashboard"(){
		setup:
		def period = newPeriod()
		setupLocationTree()
		def root = newReportProgram(CODE(1))
		def dashboardRoot = newDashboardProgram(CODE(3), root)
		def calculation = newAverage("1", CODE(2))
		def target = newDashboardTarget(TARGET1, calculation, root, 1)
		dashboardController = new DashboardController()
		
		when:
		dashboardController.params.location = LocationEntity.findByCode(RWANDA).id
		//dashboardController.params.program = root.id	
		dashboardController.params.period = period.id
		dashboardController.params.locationTypes = [DataEntityType.findByCode(DISTRICT_HOSPITAL_GROUP).id]
		def model = dashboardController.view()
		
		then:
		model.dashboardEntity.equals(dashboardRoot)
		model.currentPeriod.equals(period)
		model.currentLocation.equals(LocationEntity.findByCode(RWANDA))
		model.currentLocationTypes.equals(s([DataEntityType.findByCode(DISTRICT_HOSPITAL_GROUP)]))
		model.programDashboard != null
		model.locationDashboard != null
	}
	
	def "get dashboard with null dashboard program for the report program"(){
		setup:
		def period = newPeriod()
		setupLocationTree()
		def program = newReportProgram(CODE(1))
		def calculation = newAverage("1", CODE(2))
		def target = newDashboardTarget(TARGET1, calculation, program, 1)
		dashboardController = new DashboardController()
		
		when:
		dashboardController.params.location = LocationEntity.findByCode(RWANDA).id
		dashboardController.params.program = program.id
		dashboardController.params.period = period.id
		dashboardController.params.locationTypes = [DataEntityType.findByCode(DISTRICT_HOSPITAL_GROUP).id]
		def model = dashboardController.view()
		
		then:
		model.dashboardEntity == null
		model.currentPeriod.equals(period)
		model.currentLocation.equals(LocationEntity.findByCode(RWANDA))
		model.currentLocationTypes.equals(s([DataEntityType.findByCode(DISTRICT_HOSPITAL_GROUP)]))
		model.programDashboard == null
		model.locationDashboard == null
	}
	
	def "get dashboard with no programs"(){
	}
	
	def "get dashboard with no locations"(){
		
	}
	
}