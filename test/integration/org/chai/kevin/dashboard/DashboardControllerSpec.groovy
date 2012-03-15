package org.chai.kevin.dashboard

import org.chai.kevin.location.DataEntityType;
import org.chai.kevin.location.LocationEntity;
import org.chai.kevin.reports.ReportProgram;
import org.chai.kevin.util.JSONUtils;
import org.chai.kevin.value.Value


class DashboardControllerSpec extends DashboardIntegrationTests {

	def dashboardController
	
	def "get dashboard"() {
		setup:
		def period = newPeriod()
		setupLocationTree()
		setupDashboard()
		dashboardController = new DashboardController()
		refresh()
		
		when:
		dashboardController.params.location = LocationEntity.findByCode(RWANDA).id
		dashboardController.params.program = ReportProgram.findByCode(ROOT)
		dashboardController.params.period = period.id
		dashboardController.params.locationTypes = [DataEntityType.findByCode(DISTRICT_HOSPITAL_GROUP).id]
		def model = dashboardController.view()
		
		then:
		model.dashboardEntity.equals(DashboardProgram.findByCode(ROOT))
		model.currentPeriod.equals(period)
		model.currentLocation.equals(LocationEntity.findByCode(RWANDA))
		model.currentLocationTypes.equals(s([DataEntityType.findByCode(DISTRICT_HOSPITAL_GROUP)]))
		model.programDashboard.hasData() == true
		model.locationDashboard.hasData() == true
	}		
	
	def "get root dashboard"(){
		setup:
		def period = newPeriod()
		setupLocationTree()
		setupDashboard()
		dashboardController = new DashboardController()
		refresh()
		
		when:
		dashboardController.params.location = LocationEntity.findByCode(RWANDA).id
		dashboardController.params.period = period.id
		dashboardController.params.locationTypes = [DataEntityType.findByCode(DISTRICT_HOSPITAL_GROUP).id]
		def model = dashboardController.view()
		
		then:
		model.dashboardEntity.equals(DashboardProgram.findByCode(ROOT))
		model.currentPeriod.equals(period)
		model.currentLocation.equals(LocationEntity.findByCode(RWANDA))
		model.currentLocationTypes.equals(s([DataEntityType.findByCode(DISTRICT_HOSPITAL_GROUP)]))
		model.programDashboard.hasData() == true
		model.locationDashboard.hasData() == true
	}
	
	def "get dashboard with null dashboard program for report program"(){
		setup:
		def period = newPeriod()
		setupLocationTree()
		def program = newReportProgram(PROGRAM1)
		def calculation = newAverage("1", CODE(2))
		def target = newDashboardTarget(TARGET1, calculation, program, 1)
		dashboardController = new DashboardController()
		refresh()
		
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
		
	def "get program compare dashboard"() {
		setup:
		def period = newPeriod()
		setupLocationTree()
		setupDashboard()
		dashboardController = new DashboardController()
		def percentageCompareValue1 = newDashboardPercentage("30")
		def percentageCompareValue2 = newDashboardPercentage("10")
		refresh()
		
		when:
		dashboardController.params.location = LocationEntity.findByCode(RWANDA).id
		dashboardController.params.program = ReportProgram.findByCode(ROOT).id
		dashboardController.params.dashboardEntity = DashboardProgram.findByCode(ROOT).id
		dashboardController.params.period = period.id
		dashboardController.params.locationTypes = [DataEntityType.findByCode(DISTRICT_HOSPITAL_GROUP).id]
		dashboardController.params.table = 'program'
		dashboardController.compare()
		def dashboardControllerResponse = dashboardController.response.contentAsString
		def jsonResult = JSONUtils.getMapFromJSON(dashboardControllerResponse)
		def compareValues = jsonResult.compareValues
		
		then:		
		compareValues != null
		compareValues.size() > 0
		compareValues[0].id == DashboardProgram.findByCode(PROGRAM1).id
		compareValues[0].value == getPercentage(percentageCompareValue1)
		compareValues[1].id == DashboardProgram.findByCode(PROGRAM2).id
		compareValues[1].value == getPercentage(percentageCompareValue2)
	}
	
	def "get location compare dashboard"() {
		setup:
		def period = newPeriod()
		setupLocationTree()
		setupDashboard()
		dashboardController = new DashboardController()
		def percentageCompareValue = newDashboardPercentage("20")
		refresh()
		
		when:
		dashboardController.params.location = LocationEntity.findByCode(RWANDA).id
		dashboardController.params.program = ReportProgram.findByCode(ROOT).id
		dashboardController.params.dashboardEntity = DashboardProgram.findByCode(ROOT).id
		dashboardController.params.period = period.id
		dashboardController.params.locationTypes = [DataEntityType.findByCode(DISTRICT_HOSPITAL_GROUP).id]
		dashboardController.params.table = 'location'		
		dashboardController.compare()
		def dashboardControllerResponse = dashboardController.response.contentAsString
		def jsonResult = JSONUtils.getMapFromJSON(dashboardControllerResponse)
		def compareValues = jsonResult.compareValues
		
		then:
		compareValues != null
		compareValues.size() == 1
		compareValues[0].id == DashboardProgram.findByCode(ROOT).id
		compareValues[0].value == getPercentage(percentageCompareValue)
	}
	
	def getPercentage(def percentage) {
		if(percentage != null && percentage.isValid())
			return percentage.getRoundedValue();
		else
			return null;
	}
}