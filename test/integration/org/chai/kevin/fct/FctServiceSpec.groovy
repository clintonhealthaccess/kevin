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
	
	def "get fct total average"() {
		setup:
		setupLocationTree()
		def period = newPeriod()
		def program = newReportProgram(CODE(1))
		def location = Location.findByCode(RWANDA)
		def nde1 = newNormalizedDataElement(CODE(2), Type.TYPE_NUMBER(), [(period.id+''):[(DISTRICT_HOSPITAL_GROUP):"1", (HEALTH_CENTER_GROUP):"1"]])		
		def sum1 = newSum("\$"+nde1.id, CODE(3))		
		def target = newFctTarget(CODE(4), 1, program)
		def targetOption = newFctTargetOption(CODE(5), 1, target, sum1)
		def dataLocationTypes = new HashSet([DataLocationType.findByCode(DISTRICT_HOSPITAL_GROUP), DataLocationType.findByCode(HEALTH_CENTER_GROUP)])
		def reportType = ReportType.TABLE
		refresh()
		
		when: "total report average = 1"
		def fctTable = fctService.getFctTable(location, target, period, dataLocationTypes, reportType)
		
		then:
		fctTable.getTotalAverage(location) == 1
		
		when: "add another data location such that total report average < 1"
		def dummy = newDataLocation(["en":"dummy"], "dummy", Location.findByCode(BURERA), DataLocationType.findByCode(HEALTH_CENTER_GROUP))
		def ndeHC = newNormalizedDataElement(CODE(6), Type.TYPE_NUMBER(), [(period.id+''):[(DISTRICT_HOSPITAL_GROUP):"0", (HEALTH_CENTER_GROUP):"1"]])
		def sumHC = newSum("\$"+ndeHC.id, CODE(7))
		def targetHC = newFctTarget(CODE(8), 1, program)
		def targetOptionHC = newFctTargetOption(CODE(9), 1, targetHC, sumHC)
		refresh()
		fctTable = fctService.getFctTable(location, targetHC, period, dataLocationTypes, reportType)
		
		then:
		fctTable.getTotalAverage(location) == 0.67
		
		when: "add data locations such that total report average < .06, min % to display the report value inside the stacked bar"
		ndeHC = newNormalizedDataElement(CODE(10), Type.TYPE_NUMBER(), [(period.id+''):[(DISTRICT_HOSPITAL_GROUP):"0", (HEALTH_CENTER_GROUP):"1"]])
		sumHC = newSum("\$"+ndeHC.id, CODE(11))
		targetHC = newFctTarget(CODE(12), 1, program)
		targetOptionHC = newFctTargetOption(CODE(13), 1, targetHC, sumHC)
		int i = 0
		while(i != 30){
			newDataLocation(["en":"dummy"+i], "dummy"+i, Location.findByCode(BURERA), DataLocationType.findByCode(DISTRICT_HOSPITAL_GROUP))
			i++
		}
		refresh()
		fctTable = fctService.getFctTable(location, targetHC, period, dataLocationTypes, reportType)
		
		then:
		fctTable.getTableReportValue(location, targetOptionHC).getNumberOfDataLocations() == 33
		fctTable.getTableReportValue(location, targetOptionHC).getAverage().numberValue.round(2) == 0.06
		Utils.formatNumber("#.##", fctTable.getTableReportValue(location, targetOptionHC).getAverage().numberValue) == "0.06"
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
