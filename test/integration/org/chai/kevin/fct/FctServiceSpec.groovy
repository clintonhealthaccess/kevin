package org.chai.kevin.fct

import org.chai.kevin.data.Type
import org.chai.location.DataLocation
import org.chai.location.DataLocationType
import org.chai.location.Location
import org.chai.location.LocationLevel
import org.chai.kevin.util.Utils
import org.chai.kevin.reports.ReportService.ReportType;


class FctServiceSpec extends FctIntegrationTests { 

	def fctService
	def locationService
	
	def "get fct"() {
		setup:
		setupLocationTree()
		def period = newPeriod()
		def normalizedDataElement = newNormalizedDataElement(CODE(1), Type.TYPE_NUMBER(), [(period.id+''):[(DISTRICT_HOSPITAL_GROUP):"1", (HEALTH_CENTER_GROUP):"1"]])
		def program = newReportProgram(CODE(2))
		def sum = newSum("\$"+normalizedDataElement.id, CODE(2))
		def target = newFctTarget(CODE(3), 1, program)
		def targetOption = newFctTargetOption(CODE(4), 1, target, sum)
		def location = Location.findByCode(RWANDA)
		def dataLocationTypes = new HashSet([DataLocationType.findByCode(DISTRICT_HOSPITAL_GROUP), DataLocationType.findByCode(HEALTH_CENTER_GROUP)])
		def reportType = ReportType.TABLE
		def fctTable = null
		refresh()
		
		when:
		fctTable = fctService.getFctTable(location, target, period, dataLocationTypes, reportType)
		
		then:
		fctTable.getTableReportValue(Location.findByCode(NORTH), targetOption).getValue().numberValue == 2d
		fctTable.getTableReportValue(Location.findByCode(NORTH), targetOption).getAverage().numberValue == 1d
		
		when:
		dataLocationTypes = new HashSet([DataLocationType.findByCode(DISTRICT_HOSPITAL_GROUP)])
		fctTable = fctService.getFctTable(location, target, period, dataLocationTypes, reportType)
		
		then:
		fctTable.getTableReportValue(Location.findByCode(NORTH), targetOption).getValue().numberValue == 1d
		fctTable.getTableReportValue(Location.findByCode(NORTH), targetOption).getAverage().numberValue == 1d
	}
		
	def "get fct service with dummy location"() {
		setup:
		setupLocationTree()
		def period = newPeriod()
		def normalizedDataElement = newNormalizedDataElement(CODE(1), Type.TYPE_NUMBER(), [(period.id+''):[(DISTRICT_HOSPITAL_GROUP):"1", (HEALTH_CENTER_GROUP):"1"]])
		def program = newReportProgram(CODE(2))
		def sum = newSum("\$"+normalizedDataElement.id, CODE(2))
		def target = newFctTarget(CODE(3), 1, program)
		def targetOption = newFctTargetOption(CODE(4), 1, target, sum)
		def location = Location.findByCode(RWANDA)
		def level = locationService.getLevelAfter(location.getLevel(), new HashSet([LocationLevel.findByCode(SECTOR)]))
		def dataLocationTypes = new HashSet([DataLocationType.findByCode(DISTRICT_HOSPITAL_GROUP), DataLocationType.findByCode(HEALTH_CENTER_GROUP)])
		def reportType = ReportType.TABLE
		def fctTable = null
		
		when:	
		def dummy = newLocation([:], "dummy", location, level)
		refresh()
		fctTable = fctService.getFctTable(location, target, period, dataLocationTypes, reportType)
		
		then:
		fctTable.getTableReportValue(Location.findByCode("dummy"), targetOption) == null
		fctTable.getTableReportValue(Location.findByCode(NORTH), targetOption).getValue().numberValue == 2d
		fctTable.getTableReportValue(Location.findByCode(NORTH), targetOption).getAverage().numberValue == 1d			
	}
	
	def "get fct table report value"() {
		setup:
		setupLocationTree()
		def period = newPeriod()
		def normalizedDataElement = newNormalizedDataElement(CODE(1), Type.TYPE_NUMBER(), [(period.id+''):[(DISTRICT_HOSPITAL_GROUP):"1", (HEALTH_CENTER_GROUP):"1"]])
		def program = newReportProgram(CODE(2))
		def sum = newSum("\$"+normalizedDataElement.id, CODE(2))
		def target = newFctTarget(CODE(3), 1, program)
		def targetOption = newFctTargetOption(CODE(4), 1, target, sum)
		def location = Location.findByCode(RWANDA)
		def dataLocationTypes = new HashSet([DataLocationType.findByCode(DISTRICT_HOSPITAL_GROUP), DataLocationType.findByCode(HEALTH_CENTER_GROUP)])
		def reportType = ReportType.TABLE
		def fctTable = null
		refresh()
		
		when:
		fctTable = fctService.getFctTable(location, target, period, dataLocationTypes, reportType)
		
		then:
		fctTable.getTableReportValue(Location.findByCode(NORTH), targetOption).getValue().numberValue == 2d
		fctTable.getTableReportValue(Location.findByCode(NORTH), targetOption).getAverage().numberValue == 1d
		
		when:
		dataLocationTypes = new HashSet([DataLocationType.findByCode(DISTRICT_HOSPITAL_GROUP)])
		fctTable = fctService.getFctTable(location, target, period, dataLocationTypes, reportType)
		
		then:
		fctTable.getTableReportValue(Location.findByCode(NORTH), targetOption).getValue().numberValue == 1d
		fctTable.getTableReportValue(Location.findByCode(NORTH), targetOption).getAverage().numberValue == 1d
	}
	
	def "get fct with sorted target options"(){
		setup:
		setupLocationTree()
		def period = newPeriod()
		def normalizedDataElement = newNormalizedDataElement(CODE(1), Type.TYPE_NUMBER(), [(period.id+''):[(DISTRICT_HOSPITAL_GROUP):"1", (HEALTH_CENTER_GROUP):"1"]])
		def program = newReportProgram(CODE(2))
		def sum = newSum("\$"+normalizedDataElement.id, CODE(3))
		def target = newFctTarget(CODE(4), 1, program)
		def targetOption1 = newFctTargetOption(CODE(5), 1, target, sum)
		def targetOption2 = newFctTargetOption(CODE(6), 2, target, sum)
		refresh()
		def location = Location.findByCode(RWANDA)
		def dataLocationTypes = new HashSet([DataLocationType.findByCode(DISTRICT_HOSPITAL_GROUP), DataLocationType.findByCode(HEALTH_CENTER_GROUP)])
		def reportType = ReportType.TABLE

		when:
		def fctTable = fctService.getFctTable(location, target, period, dataLocationTypes, reportType)

		then:
		fctTable.indicators[0].equals(FctTargetOption.findByCode(CODE(5)))
		fctTable.indicators[1].equals(FctTargetOption.findByCode(CODE(6)))

		when:
		FctTargetOption.findByCode(CODE(5)).order = 2
		FctTargetOption.findByCode(CODE(6)).order = 1
		refresh()
		fctTable = fctService.getFctTable(location, target, period, dataLocationTypes, reportType)

		then:
		fctTable.indicators[0].equals(FctTargetOption.findByCode(CODE(6)))
		fctTable.indicators[1].equals(FctTargetOption.findByCode(CODE(5)))
	}

		
	def "get fct skip levels"(){
		setup:
		setupLocationTree()
		
		when:
		def fctSkipLevels = fctService.getSkipLocationLevels()
		
		then:
		fctSkipLevels.equals(s([LocationLevel.findByCode(SECTOR)]))
	}
}
