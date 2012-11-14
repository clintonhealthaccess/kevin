package org.chai.kevin.exports

import org.chai.kevin.data.Type
import org.chai.kevin.dsr.DsrController
import org.chai.kevin.dsr.DsrIntegrationTests
import org.chai.kevin.fct.FctController
import org.chai.kevin.fct.FctIntegrationTests
import org.chai.kevin.location.DataLocation
import org.chai.kevin.location.DataLocationType
import org.chai.kevin.location.Location
import org.chai.kevin.location.LocationLevel
import org.chai.kevin.reports.ReportIntegrationTests
import org.chai.kevin.reports.ReportProgram
import org.chai.kevin.util.Utils
import org.chai.kevin.util.Utils.ReportType

class ReportExportControllerSpec extends ReportIntegrationTests {
	
	def "dsr report export works"() {
		setup:
		setupLocationTree()
		setupProgramTree()
		def period = newPeriod()
		def program = ReportProgram.findByCode(PROGRAM1)
		def location = Location.findByCode(BURERA)
		def dataElement = newRawDataElement(CODE(2), Type.TYPE_NUMBER())
		def dataElementValue1 = newRawDataElementValue(dataElement, period, DataLocation.findByCode(KIVUYE), v("30"))
		def dataElementValue2 = newRawDataElementValue(dataElement, period, DataLocation.findByCode(BUTARO), v("50"))
		def category = DsrIntegrationTests.newDsrTargetCategory(CODE(1), 1)
		def target = DsrIntegrationTests.newDsrTarget(CODE(3), 1, dataElement, program, category)
		def reportType = Utils.ReportType.TABLE
		
		when:
		def dsrController = new DsrController()
		dsrController.params.period = period.id
		dsrController.params.program = program.id
		dsrController.params.location = location.id
		dsrController.params.dataLocationTypes = [DataLocationType.findByCode(HEALTH_CENTER_GROUP).id, DataLocationType.findByCode(DISTRICT_HOSPITAL_GROUP).id]
		dsrController.params.dsrCategory = category.id
		dsrController.params.reportType = reportType.toString().toLowerCase()
		dsrController.export()
		
		then:
		dsrController.response.getContentType() == "application/zip"
	}
	
	def "fct report export works"() {
		setup:
		setupLocationTree()
		def period = newPeriod()
		def program = newReportProgram(CODE(2))
		def location = Location.findByCode(RWANDA)
		def target = FctIntegrationTests.newFctTarget(CODE(3), 1, program)
		def sum = newSum("1", CODE(2))
		def targetOption1 = FctIntegrationTests.newFctTargetOption(CODE(4), 1, target, sum)
		def reportType = Utils.ReportType.TABLE
		
		when:
		def fctController = new FctController()
		fctController.params.period = period.id
		fctController.params.program = program.id
		fctController.params.location = location.id
		fctController.params.dataLocationTypes = [DataLocationType.findByCode(HEALTH_CENTER_GROUP).id, DataLocationType.findByCode(DISTRICT_HOSPITAL_GROUP).id]
		fctController.params.fctTarget = target.id
		fctController.params.reportType = reportType.toString().toLowerCase()
		fctController.export()
		
		then:
		fctController.response.getContentType() == "application/zip"
	}
	
}
