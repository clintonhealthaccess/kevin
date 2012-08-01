package org.chai.kevin.dsr

import org.chai.kevin.data.Type
import org.chai.kevin.location.DataLocationType
import org.chai.kevin.location.Location
import org.chai.kevin.reports.ReportProgram
import org.chai.kevin.util.Utils

class DsrControllerSpec extends DsrIntegrationTests {

	def dsrController
	
	def "get dsr"() {
		setup:
		setupLocationTree()
		def period = newPeriod()
		def program = newReportProgram(ROOT)
		def category = newDsrTargetCategory(CATEGORY1, 0)
		def dataElement = newRawDataElement(CODE(3), Type.TYPE_NUMBER())
		def target = newDsrTarget(CODE(4), 1, dataElement, program, category)
		def reportType = Utils.ReportType.TABLE
		
		when: "valid table"
		dsrController = new DsrController()
		dsrController.params.period = period.id
		dsrController.params.program = program.id
		dsrController.params.location = Location.findByCode(RWANDA).id
		dsrController.params.dataLocationTypes = [DataLocationType.findByCode(HEALTH_CENTER_GROUP).id, DataLocationType.findByCode(DISTRICT_HOSPITAL_GROUP).id]
		dsrController.params.dsrCategory = category.id
		dsrController.params.reportType = reportType.toString().toLowerCase()
		def model = dsrController.view()
		
		then:
		model.currentPeriod.equals(period)
		model.currentProgram.equals(program)
		model.currentLocation.equals(Location.findByCode(RWANDA))
		model.currentLocationTypes.equals(s([DataLocationType.findByCode(HEALTH_CENTER_GROUP), DataLocationType.findByCode(DISTRICT_HOSPITAL_GROUP)]))
		model.currentCategory.equals(category)
		model.currentView.equals(reportType)
		model.dsrTable != null
	}
	
	def "get dsr with category belonging to several programs only gets target belonging to the specified category"() {
		setup:
		setupLocationTree()
		def period = newPeriod()
		def program1 = newReportProgram(ROOT)
		def program2 = newReportProgram(PROGRAM1, program1)
		def category = newDsrTargetCategory(CATEGORY1, 0)
		def dataElement = newRawDataElement(CODE(3), Type.TYPE_NUMBER())
		def target1 = newDsrTarget(CODE(4), 1, dataElement, program1, category)
		def target2 = newDsrTarget(CODE(5), 1, dataElement, program2, category)
		def reportType = Utils.ReportType.TABLE
	
		when: "valid table"
		dsrController = new DsrController()
		dsrController.params.period = period.id
		dsrController.params.program = program1.id
		dsrController.params.location = Location.findByCode(RWANDA).id
		dsrController.params.dataLocationTypes = [DataLocationType.findByCode(HEALTH_CENTER_GROUP).id, DataLocationType.findByCode(DISTRICT_HOSPITAL_GROUP).id]
		dsrController.params.dsrCategory = category.id
		dsrController.params.reportType = reportType.toString().toLowerCase()
		def model = dsrController.view()
		
		then:
		model.currentPeriod.equals(period)
		model.currentProgram.equals(program1)
		model.currentLocation.equals(Location.findByCode(RWANDA))
		model.currentLocationTypes.equals(s([DataLocationType.findByCode(HEALTH_CENTER_GROUP), DataLocationType.findByCode(DISTRICT_HOSPITAL_GROUP)]))
		model.currentCategory.equals(category)
		model.currentView.equals(reportType)
		model.dsrTable != null
		model.dsrTable.targets == [target1]
	}
	
	def "get dsr with no category, redirect"() {
		setup:
		setupLocationTree()
		def period = newPeriod()
		def program = newReportProgram(ROOT)
		def dataElement = newRawDataElement(CODE(3), Type.TYPE_NUMBER())
		def category = newDsrTargetCategory(CATEGORY1, 0)
		def target = newDsrTarget(CODE(4), 1, dataElement, program, category)
		def reportType = Utils.ReportType.TABLE
		
		when: "valid table"
		dsrController = new DsrController()
		dsrController.params.period = period.id
		dsrController.params.program = program.id
		dsrController.params.location = Location.findByCode(RWANDA).id
		dsrController.params.dataLocationTypes = [DataLocationType.findByCode(HEALTH_CENTER_GROUP).id, DataLocationType.findByCode(DISTRICT_HOSPITAL_GROUP).id]
		dsrController.params.reportType = reportType.toString().toLowerCase()
		def model = dsrController.view()
		
		then:
		dsrController.response.redirectedUrl.contains("/dsr/view/")
		dsrController.response.redirectedUrl.contains(period.id+"/"+program.id+"/"+Location.findByCode(RWANDA).id+"/"+category.id)
		dsrController.response.redirectedUrl.contains("reportType="+reportType.toString().toLowerCase())
		dsrController.response.redirectedUrl.contains("dataLocationTypes="+DataLocationType.findByCode(HEALTH_CENTER_GROUP).id)
		dsrController.response.redirectedUrl.contains("dataLocationTypes="+DataLocationType.findByCode(DISTRICT_HOSPITAL_GROUP).id)
	}
	
	def "get dsr with no report type, redirect"() {
		setup:
		setupLocationTree()
		def period = newPeriod()
		def program = newReportProgram(ROOT)
		def dataElement = newRawDataElement(CODE(3), Type.TYPE_NUMBER())
		def category = newDsrTargetCategory(CATEGORY1, 0)
		def target = newDsrTarget(CODE(4), 1, dataElement, program, category)
		def reportType = Utils.ReportType.TABLE
		
		when: "valid table"
		dsrController = new DsrController()
		dsrController.params.period = period.id
		dsrController.params.program = program.id
		dsrController.params.location = Location.findByCode(RWANDA).id
		dsrController.params.dataLocationTypes = [DataLocationType.findByCode(HEALTH_CENTER_GROUP).id, DataLocationType.findByCode(DISTRICT_HOSPITAL_GROUP).id]
		dsrController.params.dsrCategory = category.id
		def model = dsrController.view()
		
		then:
		dsrController.response.redirectedUrl.contains("/dsr/view/")
		dsrController.response.redirectedUrl.contains(period.id+"/"+program.id+"/"+Location.findByCode(RWANDA).id+"/"+category.id)
		dsrController.response.redirectedUrl.contains("reportType="+reportType.toString().toLowerCase())
		dsrController.response.redirectedUrl.contains("dataLocationTypes="+DataLocationType.findByCode(HEALTH_CENTER_GROUP).id)
		dsrController.response.redirectedUrl.contains("dataLocationTypes="+DataLocationType.findByCode(DISTRICT_HOSPITAL_GROUP).id)
	}

	def "get dsr with no targets"() {
		setup:
		setupLocationTree()
		def period = newPeriod()
		def program = newReportProgram(ROOT)
		def category = newDsrTargetCategory(CATEGORY1, 0)
		def reportType = Utils.ReportType.TABLE
		
		when:
		dsrController = new DsrController()
		dsrController.params.period = period.id
		dsrController.params.program = program.id
		dsrController.params.location = Location.findByCode(BURERA).id
		dsrController.params.dataLocationTypes = [DataLocationType.findByCode(HEALTH_CENTER_GROUP).id, DataLocationType.findByCode(DISTRICT_HOSPITAL_GROUP).id]
		dsrController.params.dsrCategory = category.id
		dsrController.params.reportType = reportType.toString().toLowerCase()
		def model = dsrController.view()
		
		then:
		model.currentPeriod.id == period.id
		model.currentProgram.id == program.id
		model.currentLocation.id == Location.findByCode(BURERA).id
		model.currentLocationTypes.equals(s([DataLocationType.findByCode(HEALTH_CENTER_GROUP), DataLocationType.findByCode(DISTRICT_HOSPITAL_GROUP)]))
		model.currentCategory.id == category.id
		model.currentView.equals(reportType)
		model.dsrTable != null
		model.dsrTable.hasData() == false
	}
			
	def "get dsr with no parameters, redirect to period, program, location, category, and report type"() {
		setup:
		setupLocationTree()
		def period = newPeriod()
		def program = newReportProgram(ROOT)
		def category = newDsrTargetCategory(CATEGORY1, 0)
		def dataElement = newRawDataElement(CODE(3), Type.TYPE_NUMBER())
		def target = newDsrTarget(CODE(4), 1, dataElement, program, category)
		def reportType = Utils.ReportType.TABLE
		
		when: "no parameters"
		dsrController = new DsrController()
		def model = dsrController.view()
		
		then:
		dsrController.response.redirectedUrl.contains("/dsr/view/")
		dsrController.response.redirectedUrl.contains(period.id+"/"+program.id+"/"+Location.findByCode(RWANDA).id+"/"+category.id)
		dsrController.response.redirectedUrl.contains("reportType="+reportType.toString().toLowerCase())
		dsrController.response.redirectedUrl.contains("dataLocationTypes="+DataLocationType.findByCode(HEALTH_CENTER_GROUP).id)
		dsrController.response.redirectedUrl.contains("dataLocationTypes="+DataLocationType.findByCode(DISTRICT_HOSPITAL_GROUP).id)
	}	
	
	def "get dsr with invalid parameters, redirect to default period, root program, root location, location types, category, report type and target"() {
		setup:
		setupLocationTree()
		def period = newPeriod()
		def program = newReportProgram(ROOT)
		def category = newDsrTargetCategory(CATEGORY1, 0)
		def dataElement = newRawDataElement(CODE(3), Type.TYPE_NUMBER())
		def target = newDsrTarget(CODE(4), 1, dataElement, program, category)
		def reportType = Utils.ReportType.TABLE
		
		when: "invalid parameters"
		dsrController = new DsrController()
		dsrController.params.period = -1
		dsrController.params.program = -1
		dsrController.params.location = -1
		dsrController.params.dsrCategory = -1
		dsrController.params.dataLocationTypes = [-1, -2]
		dsrController.params.reportType = "yourmom"
		def model = dsrController.view()
		
		then:
		dsrController.response.redirectedUrl.contains("/dsr/view/")
		dsrController.response.redirectedUrl.contains(period.id+"/"+program.id+"/"+Location.findByCode(RWANDA).id+"/"+category.id)
		dsrController.response.redirectedUrl.contains("reportType="+reportType.toString().toLowerCase())
		dsrController.response.redirectedUrl.contains("dataLocationTypes="+DataLocationType.findByCode(HEALTH_CENTER_GROUP).id)
		dsrController.response.redirectedUrl.contains("dataLocationTypes="+DataLocationType.findByCode(DISTRICT_HOSPITAL_GROUP).id)
	}
	
	def "get dsr with invalid parameters, redirect with correct parameter"() {
		setup:
		setupLocationTree()
		def period = newPeriod()
		def program = newReportProgram(ROOT)
		def category = newDsrTargetCategory(CATEGORY1, 0)
		def dataElement = newRawDataElement(CODE(3), Type.TYPE_NUMBER())
		def target = newDsrTarget(CODE(4), 1, dataElement, program, category)
		def reportType = Utils.ReportType.TABLE
		
		when: "valid location parameter"
		dsrController = new DsrController()
		dsrController.params.period = -1
		dsrController.params.program = -1
		dsrController.params.location = Location.findByCode(BURERA).id
		dsrController.params.dsrCategory = -1
		dsrController.params.dataLocationTypes = [-1, -2]
		dsrController.params.reportType = "yourmom"
		def model = dsrController.view()
		
		then:
		dsrController.response.redirectedUrl.contains("/dsr/view/")
		dsrController.response.redirectedUrl.contains(period.id+"/"+program.id+"/"+Location.findByCode(BURERA).id+"/"+category.id)
		dsrController.response.redirectedUrl.contains("reportType="+reportType.toString().toLowerCase())
		dsrController.response.redirectedUrl.contains("dataLocationTypes="+DataLocationType.findByCode(HEALTH_CENTER_GROUP).id)
		dsrController.response.redirectedUrl.contains("dataLocationTypes="+DataLocationType.findByCode(DISTRICT_HOSPITAL_GROUP).id)
	}
	
	def "get dsr for only district hospitals"() {
		setup:
		setupLocationTree()
		def period = newPeriod()
		def program = newReportProgram(ROOT)
		def category = newDsrTargetCategory(CATEGORY1, 0)
		def dataElement = newRawDataElement(CODE(3), Type.TYPE_NUMBER())
		def target = newDsrTarget(CODE(4), 1, dataElement, program, category)
		def reportType = Utils.ReportType.TABLE
		
		when: "valid table"
		dsrController = new DsrController()
		dsrController.params.period = period.id
		dsrController.params.program = program.id
		dsrController.params.location = Location.findByCode(RWANDA).id
		dsrController.params.dataLocationTypes = [DataLocationType.findByCode(DISTRICT_HOSPITAL_GROUP).id]
		dsrController.params.dsrCategory = category.id
		dsrController.params.reportType = reportType.toString().toLowerCase()
		def model = dsrController.view()
		
		then:
		model.currentPeriod.equals(period)
		model.currentProgram.equals(program)
		model.currentLocation.equals(Location.findByCode(RWANDA))
		model.currentLocationTypes.equals(s([DataLocationType.findByCode(DISTRICT_HOSPITAL_GROUP)]))
		model.currentCategory.equals(category)
		model.currentView.equals(reportType)
		model.dsrTable != null
	}
	
}