package org.chai.kevin.fct

import org.chai.kevin.data.Summ
import org.chai.kevin.IntegrationTests
import org.chai.location.DataLocationType;
import org.chai.location.Location;
import org.chai.location.LocationLevel;
import org.chai.location.LocationService
import org.chai.kevin.util.Utils
import org.chai.kevin.reports.ReportProgram
import org.chai.kevin.dashboard.DashboardIntegrationTests
import org.chai.kevin.dashboard.DashboardTarget
import org.chai.kevin.reports.ReportService.ReportType;

class FctControllerSpec extends FctIntegrationTests {

	def fctController
	
	def "view fct with nothing"() {
		setup:
		fctController = new FctController()
		
		when:
		def model = fctController.view()
		
		then:
		fctController.response.redirectedUrl == null
		model.fctTable == null
	}
	
	def "get fct"() {
		setup:
		setupLocationTree()
		def period = newPeriod()
		def program = newReportProgram(CODE(1))
		def target = newFctTarget(CODE(3), 1, program)
		def sum = newSum("1", CODE(2))
		def targetOption1 = newFctTargetOption(CODE(4), 1, target, sum)
		def reportType = ReportType.TABLE
		
		when: "valid table"
		fctController = new FctController()
		fctController.params.period = period.id
		fctController.params.program = program.id
		fctController.params.location = Location.findByCode(RWANDA).id
		fctController.params.dataLocationTypes = [DataLocationType.findByCode(HEALTH_CENTER_GROUP).id, DataLocationType.findByCode(DISTRICT_HOSPITAL_GROUP).id]
		fctController.params.fctTarget = target.id
		fctController.params.reportType = reportType.toString().toLowerCase()
		def model = fctController.view()
		
		then:
		model.currentPeriod.equals(period)		
		model.currentProgram.equals(program)
		model.currentLocation.equals(Location.findByCode(RWANDA))		
		model.currentLocationTypes.equals(s([DataLocationType.findByCode(HEALTH_CENTER_GROUP), DataLocationType.findByCode(DISTRICT_HOSPITAL_GROUP)]))
		model.locationSkipLevels.equals(s([LocationLevel.findByCode(SECTOR)]))
		model.currentTarget.equals(target)
		model.fctTable != null
		model.fctTable.hasData() == true
	}
	
	def "get fct with no parameters, redirect to period, root program, root location, location types, and target"(){
		setup:
		setupLocationTree()
		def period = newPeriod()
		def program = newReportProgram(CODE(1))
		def target = newFctTarget(CODE(3), 1, program)
		def sum = newSum("1", CODE(2))
		def targetOption1 = newFctTargetOption(CODE(4), 1, target, sum)
		
		when: "no parameters"
		fctController = new FctController()
		def model = fctController.view()
		
		then:
		fctController.response.redirectedUrl.contains("/fct/view/")
		fctController.response.redirectedUrl.contains(period.id+"/"+program.id+"/"+Location.findByCode(RWANDA).id+"/"+target.id)
		fctController.response.redirectedUrl.contains("dataLocationTypes="+DataLocationType.findByCode(HEALTH_CENTER_GROUP).id)
		fctController.response.redirectedUrl.contains("dataLocationTypes="+DataLocationType.findByCode(DISTRICT_HOSPITAL_GROUP).id)
	}
		
	def "get fct with no parameters, redirect to period, root program, root location, location types"(){
		setup:
		setupLocationTree()
		def period = newPeriod()
		def program = newReportProgram(CODE(1))
		def target = newFctTarget(CODE(3), 1, program)
		def sum = newSum("1", CODE(2))
		def targetOption1 = newFctTargetOption(CODE(4), 1, target, sum)
		
		when: "no parameters"
		fctController = new FctController()
		def model = fctController.view()
		
		then:
		fctController.response.redirectedUrl.contains("/fct/view/")
		fctController.response.redirectedUrl.contains(period.id+"/"+program.id+"/"+Location.findByCode(RWANDA).id)
		fctController.response.redirectedUrl.contains("dataLocationTypes="+DataLocationType.findByCode(HEALTH_CENTER_GROUP).id)
		fctController.response.redirectedUrl.contains("dataLocationTypes="+DataLocationType.findByCode(DISTRICT_HOSPITAL_GROUP).id)
	}
		
	
	def "get fct with with invalid parameters, default to period, root program, root location, location types, and target"(){
		setup:
		setupLocationTree()
		def period = newPeriod()
		def program = newReportProgram(CODE(1))
		def target = newFctTarget(CODE(3), 1, program)
		def sum = newSum("1", CODE(2))
		def targetOption1 = newFctTargetOption(CODE(4), 1, target, sum)
		
		when: "invalid parameters"
		fctController = new FctController()
		fctController.params.period = -1
		fctController.params.program = -1
		fctController.params.location = -1
		fctController.params.dataLocationTypes = [-1, -2]
		fctController.params.fctTarget = -1
		def model = fctController.view()
		
		then:
		fctController.response.redirectedUrl.contains("/fct/view/")
		fctController.response.redirectedUrl.contains(period.id+"/"+program.id+"/"+Location.findByCode(RWANDA).id+"/"+target.id)
		fctController.response.redirectedUrl.contains("dataLocationTypes="+DataLocationType.findByCode(HEALTH_CENTER_GROUP).id)
		fctController.response.redirectedUrl.contains("dataLocationTypes="+DataLocationType.findByCode(DISTRICT_HOSPITAL_GROUP).id)
	}	
	
	def "get fct with with invalid parameters, redirect with correct parameter"(){
		setup:
		setupLocationTree()
		def period = newPeriod()
		def program = newReportProgram(CODE(1))
		def target = newFctTarget(CODE(3), 1, program)
		def sum = newSum("1", CODE(2))
		def targetOption1 = newFctTargetOption(CODE(4), 1, target, sum)
		
		when: "valid location parameter"
		fctController = new FctController()
		fctController.params.period = -1
		fctController.params.program = -1
		fctController.params.location = Location.findByCode(BURERA).id
		fctController.params.dataLocationTypes = [-1, -2]
		fctController.params.fctTarget = -1
		def model = fctController.view()
		
		then:
		fctController.response.redirectedUrl.contains("/fct/view/")
		fctController.response.redirectedUrl.contains(period.id+"/"+program.id+"/"+Location.findByCode(BURERA).id+"/"+target.id)
		fctController.response.redirectedUrl.contains("dataLocationTypes="+DataLocationType.findByCode(HEALTH_CENTER_GROUP).id)
		fctController.response.redirectedUrl.contains("dataLocationTypes="+DataLocationType.findByCode(DISTRICT_HOSPITAL_GROUP).id)
	}
	
	def "get fct with invalid parameters corresponding to dashboard, redirect with correct parameter"() {
		setup:
		def period = newPeriod()
		setupLocationTree()
		setupProgramTree()
		DashboardIntegrationTests.setupDashboardTree()
		def target = newFctTarget(CODE(3), 1, ReportProgram.findByCode(ROOT))
		def sum = Summ.findByCode('CODE2')
		def targetOption1 = newFctTargetOption(CODE(4), 1, target, sum)
		
		when:
		fctController = new FctController()
		fctController.params.period = period.id
		fctController.params.program = ReportProgram.findByCode(ROOT).id
		fctController.params.location = Location.findByCode(BURERA).id
		fctController.params.dataLocationTypes = [DataLocationType.findByCode(HEALTH_CENTER_GROUP).id, DataLocationType.findByCode(DISTRICT_HOSPITAL_GROUP).id]
		fctController.params.fctTarget = DashboardTarget.findByCode(TARGET1)
		def model = fctController.view()
		
		then:
		fctController.response.redirectedUrl.contains("/fct/view/")
		fctController.response.redirectedUrl.contains(period.id+"/"+ReportProgram.findByCode(ROOT).id+"/"+Location.findByCode(BURERA).id)
		fctController.response.redirectedUrl.contains("dataLocationTypes="+DataLocationType.findByCode(HEALTH_CENTER_GROUP).id)
		fctController.response.redirectedUrl.contains("dataLocationTypes="+DataLocationType.findByCode(DISTRICT_HOSPITAL_GROUP).id)
	}
	
	def "get fct with with invalid data location type parameters, redirect with correct parameter"(){
		setup:
		setupLocationTree()
		def period = newPeriod()
		def program = newReportProgram(CODE(1))
		def target = newFctTarget(CODE(3), 1, program)
		def sum = newSum("1", CODE(2))
		def targetOption1 = newFctTargetOption(CODE(4), 1, target, sum)
		
		when: "valid location parameter"
		fctController = new FctController()
		fctController.params.period = -1
		fctController.params.program = -1
		fctController.params.location = Location.findByCode(BURERA).id
		fctController.params.dataLocationTypes = ['1,2', 'test']
		fctController.params.fctTarget = -1
		def model = fctController.view()
		
		then:
		fctController.response.redirectedUrl.contains("/fct/view/")
		fctController.response.redirectedUrl.contains(period.id+"/"+program.id+"/"+Location.findByCode(BURERA).id+"/"+target.id)
		fctController.response.redirectedUrl.contains("dataLocationTypes="+DataLocationType.findByCode(HEALTH_CENTER_GROUP).id)
		fctController.response.redirectedUrl.contains("dataLocationTypes="+DataLocationType.findByCode(DISTRICT_HOSPITAL_GROUP).id)
	}
	
	def "get fct with target with no target redirects to first target"() {
		setup:
		setupLocationTree()
		def period = newPeriod()
		def program = newReportProgram(CODE(1))
		def sum = newSum("1", CODE(2))
		def target = newFctTarget(CODE(3), 1, program)
		def targetOption1 = newFctTargetOption(CODE(4), 1, target, sum)
		
		when:
		fctController = new FctController()
		fctController.params.period = period.id
		fctController.params.program = program.id
		fctController.params.location = Location.findByCode(BURERA).id
		fctController.params.dataLocationTypes = [DataLocationType.findByCode(HEALTH_CENTER_GROUP).id, DataLocationType.findByCode(DISTRICT_HOSPITAL_GROUP).id]
		def model = fctController.view()
		
		then:
		fctController.response.redirectedUrl.contains("/fct/view/")
		fctController.response.redirectedUrl.contains(period.id+"/"+program.id+"/"+Location.findByCode(BURERA).id+"/"+target.id)
		fctController.response.redirectedUrl.contains("dataLocationTypes="+DataLocationType.findByCode(HEALTH_CENTER_GROUP).id)
		fctController.response.redirectedUrl.contains("dataLocationTypes="+DataLocationType.findByCode(DISTRICT_HOSPITAL_GROUP).id)
	}
	
	def "get fct with no targets"() {
		setup:
		setupLocationTree()
		def period = newPeriod()
		def program = newReportProgram(CODE(2))
		def reportType = ReportType.TABLE
		
		when:
		fctController = new FctController()
		fctController.params.period = period.id
		fctController.params.program = program.id
		fctController.params.location = Location.findByCode(BURERA).id		
		fctController.params.dataLocationTypes = [DataLocationType.findByCode(HEALTH_CENTER_GROUP).id, DataLocationType.findByCode(DISTRICT_HOSPITAL_GROUP).id]
		fctController.params.reportType = reportType.toString().toLowerCase()
		def model = fctController.view()
		
		then:
		model.fctTable == null
	}
	
	def "get fct with target with no target options"() {
		setup:
		setupLocationTree()
		def period = newPeriod()
		def program = newReportProgram(CODE(1))
		def sum = newSum("1", CODE(2))
		def target = newFctTarget(CODE(3), 1, program)
		def reportType = ReportType.TABLE
		
		when:
		fctController = new FctController()
		fctController.params.period = period.id
		fctController.params.program = program.id
		fctController.params.location = Location.findByCode(BURERA).id
		fctController.params.dataLocationTypes = [DataLocationType.findByCode(HEALTH_CENTER_GROUP).id, DataLocationType.findByCode(DISTRICT_HOSPITAL_GROUP).id]
		fctController.params.fctTarget = target.id
		fctController.params.reportType = reportType.toString().toLowerCase()
		def model = fctController.view()
		
		then:
		model.fctTable != null
		model.fctTable.hasData() == false
	}
	
	def "get fct with skipped level parameter"() {
		setup:
		setupLocationTree()
		def period = newPeriod()
		def program = newReportProgram(CODE(2))
		def sum = newSum("1", CODE(2))
		def target = newFctTarget(CODE(3), 1, program)
		def targetOption1 = newFctTargetOption(CODE(4), 1, target, sum)
		def reportType = ReportType.TABLE
		
		when:
		fctController = new FctController()
		fctController.params.period = period.id
		fctController.params.program = program.id
		fctController.params.location = Location.findByCode(BURERA).id
		fctController.params.dataLocationTypes = [DataLocationType.findByCode(HEALTH_CENTER_GROUP).id, DataLocationType.findByCode(DISTRICT_HOSPITAL_GROUP).id]
		fctController.params.fctTarget = target.id
		fctController.params.reportType = reportType.toString().toLowerCase()
		def model = fctController.view()
		
		then:
		model.currentLocation.equals(Location.findByCode(BURERA))
		model.fctTable != null
		model.fctTable.hasData() == true
	}
}

