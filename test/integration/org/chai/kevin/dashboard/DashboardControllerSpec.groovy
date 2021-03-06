package org.chai.kevin.dashboard

import org.chai.location.DataLocationType;
import org.chai.location.CalculationLocation;
import org.chai.location.Location;
import org.chai.kevin.Period;
import org.chai.kevin.reports.ReportProgram;
import org.chai.kevin.util.JSONUtils;
import org.chai.kevin.util.Utils;
import org.chai.kevin.value.Value
import org.chai.kevin.dsr.DsrIntegrationTests

class DashboardControllerSpec extends DashboardIntegrationTests {

	def dashboardController
	
	def "view dashboard with nothing"() {
		setup:
		dashboardController = new DashboardController()
		
		when:
		def model = dashboardController.view()
		
		then:
		dashboardController.response.redirectedUrl == null
		model.dashboard == null
	}
	
	def "get dashboard"() {
		setup:
		def period = newPeriod()
		setupLocationTree()
		setupProgramTree()
		setupDashboardTree()
		dashboardController = new DashboardController()
		refresh()
		
		when:
		dashboardController.params.period = period.id
		dashboardController.params.location = Location.findByCode(RWANDA).id
		dashboardController.params.program = ReportProgram.findByCode(ROOT).id
		dashboardController.params.dashboardEntity = DashboardProgram.findByCode(DASHBOARD_ROOT).id
		dashboardController.params.dataLocationTypes = [DataLocationType.findByCode(DISTRICT_HOSPITAL_GROUP).id]
		def model = dashboardController.view()
		
		then:
		model.currentPeriod.equals(period)
		model.dashboardEntity.equals(DashboardProgram.findByCode(DASHBOARD_ROOT))
		model.currentLocation.equals(Location.findByCode(RWANDA))
		model.currentLocationTypes.equals(s([DataLocationType.findByCode(DISTRICT_HOSPITAL_GROUP)]))
		model.dashboard.hasData() == true
	}
	
	def "get dashboard with dsr id"() {
		setup:
		def period = newPeriod()
		setupLocationTree()
		setupProgramTree()
		setupDashboardTree()
		def dsrCategory = DsrIntegrationTests.newDsrTargetCategory(CODE(1), ReportProgram.findByCode(ROOT), 1)
		dashboardController = new DashboardController()
		refresh()
		
		when:
		dashboardController.params.period = period.id
		dashboardController.params.location = Location.findByCode(RWANDA).id
		dashboardController.params.program = dsrCategory.id
		dashboardController.params.dataLocationTypes = [DataLocationType.findByCode(DISTRICT_HOSPITAL_GROUP).id]
		def model = dashboardController.view()
		
		then:
		model == null
		dashboardController.response.redirectedUrl.contains("/dashboard/view/")
		dashboardController.response.redirectedUrl.contains(period.id+"/"+ReportProgram.findByCode(ROOT).id+"/"+Location.findByCode(RWANDA).id)
	}
	
	def "get dashboard with no location type and no default"() {
		setup:
		def period = newPeriod()
		setupLocationTree()
		def dh = DataLocationType.findByCode(DISTRICT_HOSPITAL_GROUP)
		dh.defaultSelected = false
		dh.save()
		def cs = DataLocationType.findByCode(HEALTH_CENTER_GROUP)
		cs.defaultSelected = false
		cs.save()
		setupProgramTree()
		setupDashboardTree()
		dashboardController = new DashboardController()
		refresh()
		
		when:
		dashboardController.params.period = period.id
		dashboardController.params.location = Location.findByCode(RWANDA).id
		dashboardController.params.program = ReportProgram.findByCode(ROOT).id
		dashboardController.params.dashboardEntity = DashboardProgram.findByCode(DASHBOARD_ROOT).id
		def model = dashboardController.view()
		
		then:
		model.currentPeriod.equals(period)
		model.dashboardEntity.equals(DashboardProgram.findByCode(DASHBOARD_ROOT))
		model.currentLocation.equals(Location.findByCode(RWANDA))
		model.currentLocationTypes.equals(s([]))
		model.dashboard.hasData() == true
	}
	
	def "get dashboard with no parameters redirects to period, root program, root location, location types, and dashboard entity"(){
		setup:
		def period = newPeriod()
		setupLocationTree()
		def program = newReportProgram(PROGRAM1)
		def dashboardProgram = newDashboardProgram(DASHBOARD_PROGRAM1, program, 1)
		def calculation = newSum("1", CODE(2))
		def target = newDashboardTarget(TARGET1, calculation, program, 1)
		dashboardController = new DashboardController()
		refresh()
		
		when:
		def model = dashboardController.view()
		
		then:
		dashboardController.response.redirectedUrl.contains("/dashboard/view/")
		dashboardController.response.redirectedUrl.contains(period.id+"/"+program.id+"/"+Location.findByCode(RWANDA).id)
		dashboardController.response.redirectedUrl.contains("dataLocationTypes="+DataLocationType.findByCode(HEALTH_CENTER_GROUP).id)
		dashboardController.response.redirectedUrl.contains("dataLocationTypes="+DataLocationType.findByCode(DISTRICT_HOSPITAL_GROUP).id)
	}
	
	def "get dashboard with invalid parameters, redirect to default period, root program, root location, location types, and dashboard entity"(){
		setup:
		def period = newPeriod()
		setupLocationTree()
		def program = newReportProgram(PROGRAM1)
		def dashboardProgram = newDashboardProgram(DASHBOARD_PROGRAM1, program, 1)
		def calculation = newSum("1", CODE(2))
		def target = newDashboardTarget(TARGET1, calculation, program, 1)
		dashboardController = new DashboardController()
		refresh()
		
		when:
		dashboardController.params.period = -1
		dashboardController.params.program = -1
		dashboardController.params.dashboardEntity = -1
		dashboardController.params.location = -1
		dashboardController.params.dataLocationTypes = [-1, -2]
		def model = dashboardController.view()
		
		then:
		dashboardController.response.redirectedUrl.contains("/dashboard/view/")
		dashboardController.response.redirectedUrl.contains(period.id+"/"+program.id+"/"+Location.findByCode(RWANDA).id)
		dashboardController.response.redirectedUrl.contains("dataLocationTypes="+DataLocationType.findByCode(HEALTH_CENTER_GROUP).id)
		dashboardController.response.redirectedUrl.contains("dataLocationTypes="+DataLocationType.findByCode(DISTRICT_HOSPITAL_GROUP).id)
	}
	
	def "get dashboard with invalid parameters, redirect with correct parameters"(){
		setup:
		def period = newPeriod()
		setupLocationTree()
		def program = newReportProgram(PROGRAM1)
		def dashboardProgram = newDashboardProgram(DASHBOARD_PROGRAM1, program, 1)
		def calculation = newSum("1", CODE(2))
		def target = newDashboardTarget(TARGET1, calculation, program, 1)
		dashboardController = new DashboardController()
		refresh()
		
		when: "valid location parameter"
		dashboardController.params.period = -1
		dashboardController.params.program = -1
		dashboardController.params.dashboardEntity = -1
		dashboardController.params.location = Location.findByCode(BURERA).id
		dashboardController.params.dataLocationTypes = [-1, -2]
		def model = dashboardController.view()
		
		then:
		dashboardController.response.redirectedUrl.contains("/dashboard/view/")
		dashboardController.response.redirectedUrl.contains(period.id+"/"+program.id+"/"+Location.findByCode(BURERA).id)
		dashboardController.response.redirectedUrl.contains("dataLocationTypes="+DataLocationType.findByCode(HEALTH_CENTER_GROUP).id)
		dashboardController.response.redirectedUrl.contains("dataLocationTypes="+DataLocationType.findByCode(DISTRICT_HOSPITAL_GROUP).id)
	}
	
	def "get dashboard with null dashboard program for report program"(){
		setup:
		def period = newPeriod()
		setupLocationTree()
		def program = newReportProgram(PROGRAM1)
		def calculation = newSum("1", CODE(2))
		def target = newDashboardTarget(TARGET1, calculation, program, 1)
		dashboardController = new DashboardController()
		refresh()
		
		when:
		dashboardController.params.period = period.id
		dashboardController.params.program = program.id
		dashboardController.params.location = Location.findByCode(RWANDA).id
		dashboardController.params.dataLocationTypes = [DataLocationType.findByCode(DISTRICT_HOSPITAL_GROUP).id]
		def model = dashboardController.view()
		
		then:
		model.currentPeriod.equals(period)
		model.currentProgram.equals(program)
		model.dashboardEntity == null
		model.currentLocation.equals(Location.findByCode(RWANDA))
		model.currentLocationTypes.equals(s([DataLocationType.findByCode(DISTRICT_HOSPITAL_GROUP)]))
		model.dashboard == null
	}
	
	def "get program compare dashboard"() {
		setup:
		def period = newPeriod()
		setupLocationTree()
		setupProgramTree()
		setupDashboardTree()
		dashboardController = new DashboardController()
		refresh()
		
		when:
		dashboardController.params.location = CalculationLocation.findByCode(RWANDA).id
		dashboardController.params.program = ReportProgram.findByCode(ROOT).id
		dashboardController.params.dashboardEntity = DashboardProgram.findByCode(DASHBOARD_ROOT).id
		dashboardController.params.period = period.id
		dashboardController.params.dataLocationTypes = [DataLocationType.findByCode(DISTRICT_HOSPITAL_GROUP).id]
		dashboardController.params.table = 'program'
		dashboardController.compare()
		def dashboardControllerResponse = dashboardController.response.contentAsString
		def jsonResult = JSONUtils.getMapFromJSON(dashboardControllerResponse)
		def compareValues = jsonResult.compareValues
		
		then:		
		compareValues != null
		compareValues.size() > 0
		compareValues[0].id == DashboardProgram.findByCode(DASHBOARD_PROGRAM1).id
		compareValues[0].value == 30
		compareValues[1].id == DashboardProgram.findByCode(DASHBOARD_PROGRAM2).id
		compareValues[1].value == 10
	}
	
	def "get location compare dashboard"() {
		setup:
		def period = newPeriod()
		setupLocationTree()
		setupProgramTree()
		setupDashboardTree()
		dashboardController = new DashboardController()
		refresh()
		
		when:
		dashboardController.params.location = CalculationLocation.findByCode(RWANDA).id
		dashboardController.params.program = ReportProgram.findByCode(ROOT).id
		dashboardController.params.dashboardEntity = DashboardProgram.findByCode(DASHBOARD_ROOT).id
		dashboardController.params.period = period.id
		dashboardController.params.dataLocationTypes = [DataLocationType.findByCode(DISTRICT_HOSPITAL_GROUP).id]
		dashboardController.params.table = 'location'
		dashboardController.compare()
		def dashboardControllerResponse = dashboardController.response.contentAsString
		def jsonResult = JSONUtils.getMapFromJSON(dashboardControllerResponse)
		def compareValues = jsonResult.compareValues
		
		then:
		compareValues != null
		compareValues.size() == 1
		compareValues[0].id == DashboardProgram.findByCode(DASHBOARD_ROOT).id
		Utils.formatNumber("#.0", compareValues[0].value) == "16.7"
	}

}