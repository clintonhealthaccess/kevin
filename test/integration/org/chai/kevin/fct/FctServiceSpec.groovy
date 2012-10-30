package org.chai.kevin.fct

import org.chai.kevin.data.Type
import org.chai.location.DataLocation
import org.chai.location.DataLocationType
import org.chai.location.Location
import org.chai.location.LocationLevel
import org.chai.kevin.util.Utils

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
		def reportType = Utils.ReportType.TABLE
		def fctTable = null
		refresh()
		
		when:
		fctTable = fctService.getFctTable(location, program, target, period, dataLocationTypes, reportType)
		
		then:
		fctTable.getReportValue(Location.findByCode(NORTH), targetOption).getValue().numberValue == 2d
		fctTable.getReportValue(Location.findByCode(NORTH), targetOption).getAverage().numberValue == 1d
		
		when:
		dataLocationTypes = new HashSet([DataLocationType.findByCode(DISTRICT_HOSPITAL_GROUP)])
		fctTable = fctService.getFctTable(location, program, target, period, dataLocationTypes, reportType)
		
		then:
		fctTable.getReportValue(Location.findByCode(NORTH), targetOption).getValue().numberValue == 1d
		fctTable.getReportValue(Location.findByCode(NORTH), targetOption).getAverage().numberValue == 1d
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
		def reportType = Utils.ReportType.TABLE
		def fctTable = null
		
		when:	
		def dummy = newLocation([:], "dummy", location, level)
		refresh()
		fctTable = fctService.getFctTable(location, program, target, period, dataLocationTypes, reportType)
		
		then:
		fctTable.getReportValue(Location.findByCode("dummy"), targetOption) == null
		fctTable.getReportValue(Location.findByCode(NORTH), targetOption).getValue().numberValue == 2d
		fctTable.getReportValue(Location.findByCode(NORTH), targetOption).getAverage().numberValue == 1d
				
	}
	
	def "test fct top level locations"() {
		setup:
		setupLocationTree()
		def period = newPeriod()
		def program = newReportProgram(CODE(1))
		def location = Location.findByCode(RWANDA)
		def lessThan100 = newNormalizedDataElement(CODE(2), Type.TYPE_NUMBER(), [(period.id+''):[(DISTRICT_HOSPITAL_GROUP):"1", (HEALTH_CENTER_GROUP):"1"]])
		def sum = newSum("\$"+lessThan100.id, CODE(3))
		def target = newFctTarget(CODE(4), 1, program)
		def targetOption = newFctTargetOption(CODE(5), 1, target, sum)
		def dataLocationTypes = new HashSet([DataLocationType.findByCode(DISTRICT_HOSPITAL_GROUP), DataLocationType.findByCode(HEALTH_CENTER_GROUP)])
		def reportType = Utils.ReportType.TABLE
		def fctTable = null
		refresh()
		
		when:
		fctTable = fctService.getFctTable(location, program, target, period, dataLocationTypes, reportType)
		
		then:
		fctTable.topLevelLocations.equals([Location.findByCode(NORTH)])
		
		when:
		location = Location.findByCode(BURERA)
		fctTable = fctService.getFctTable(location, program, target, period, dataLocationTypes, reportType)
		
		then:
		fctTable.topLevelLocations.equals([DataLocation.findByCode(BUTARO), DataLocation.findByCode(KIVUYE)])
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
		def reportType = Utils.ReportType.TABLE
		refresh()
		
		when: "total report average = 1"
		def fctTable = fctService.getFctTable(location, program, target, period, dataLocationTypes, reportType)
		
		then:
		fctTable.getTotalAverage(location) == 1
		
		when: "add another data location such that total report average < 1"
		def dummy = newDataLocation(["en":"dummy"], "dummy", Location.findByCode(BURERA), DataLocationType.findByCode(HEALTH_CENTER_GROUP))
		def ndeHC = newNormalizedDataElement(CODE(6), Type.TYPE_NUMBER(), [(period.id+''):[(DISTRICT_HOSPITAL_GROUP):"0", (HEALTH_CENTER_GROUP):"1"]])
		def sumHC = newSum("\$"+ndeHC.id, CODE(7))
		def targetHC = newFctTarget(CODE(8), 1, program)
		def targetOptionHC = newFctTargetOption(CODE(9), 1, targetHC, sumHC)
		refresh()
		fctTable = fctService.getFctTable(location, program, targetHC, period, dataLocationTypes, reportType)
		
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
		fctTable = fctService.getFctTable(location, program, targetHC, period, dataLocationTypes, reportType)
		
		then:
		fctTable.getReportValue(location, targetOptionHC).getNumberOfDataLocations() == 33
		fctTable.getReportValue(location, targetOptionHC).getAverage().numberValue.round(2) == 0.06
		Utils.formatNumber("#.##", fctTable.getReportValue(location, targetOptionHC).getAverage().numberValue) == "0.06"
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
		def reportType = Utils.ReportType.TABLE
		def fctTable = null
		refresh()
		
		when:
		fctTable = fctService.getFctTable(location, program, target, period, dataLocationTypes, reportType)
		
		then:
		fctTable.getTableReportValue(Location.findByCode(NORTH), targetOption).getValue().numberValue == 2d
		fctTable.getTableReportValue(Location.findByCode(NORTH), targetOption).getAverage().numberValue == 1d
		
		when:
		dataLocationTypes = new HashSet([DataLocationType.findByCode(DISTRICT_HOSPITAL_GROUP)])
		fctTable = fctService.getFctTable(location, program, target, period, dataLocationTypes, reportType)
		
		then:
		fctTable.getTableReportValue(Location.findByCode(NORTH), targetOption).getValue().numberValue == 1d
		fctTable.getTableReportValue(Location.findByCode(NORTH), targetOption).getAverage().numberValue == 1d
	}
	
	def "get fct map report value"() {
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
		def reportType = Utils.ReportType.TABLE
		def fctTable = null
		refresh()
		
		when:
		fctTable = fctService.getFctTable(location, program, target, period, dataLocationTypes, reportType)
		
		then:
		fctTable.getMapReportValue(Location.findByCode(NORTH), targetOption).numberValue == 2d
		
		when:
		dataLocationTypes = new HashSet([DataLocationType.findByCode(DISTRICT_HOSPITAL_GROUP)])
		fctTable = fctService.getFctTable(location, program, target, period, dataLocationTypes, reportType)
		
		then:
		fctTable.getMapReportValue(Location.findByCode(NORTH), targetOption).numberValue == 1d
	}

	def "get fct map report percentage"() {
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
		def reportType = Utils.ReportType.TABLE
		def fctTable = null
		refresh()
		
		when:
		fctTable = fctService.getFctTable(location, program, target, period, dataLocationTypes, reportType)
		
		then:
		fctTable.getMapReportPercentage(Location.findByCode(NORTH), targetOption).numberValue == 1d
		
		when:
		dataLocationTypes = new HashSet([DataLocationType.findByCode(DISTRICT_HOSPITAL_GROUP)])
		fctTable = fctService.getFctTable(location, program, target, period, dataLocationTypes, reportType)
		
		then:
		fctTable.getMapReportPercentage(Location.findByCode(NORTH), targetOption).numberValue == 1d
	}
	
//	TODO fix tests
//	def "get fct with sorted target options"(){
//		setup:
//		setupLocationTree()
//		def period = newPeriod()
//		def normalizedDataElement = newNormalizedDataElement(CODE(1), Type.TYPE_NUMBER(), [(period.id+''):[(DISTRICT_HOSPITAL_GROUP):"1", (HEALTH_CENTER_GROUP):"1"]])
//		def program = newReportProgram(CODE(2))
//		def sum = newSum("\$"+normalizedDataElement.id, CODE(3))
//		def target = newFctTarget(CODE(4), 1, [DISTRICT_HOSPITAL_GROUP, HEALTH_CENTER_GROUP], program)
//		def targetOption1 = newFctTargetOption(CODE(5), 1, target, sum)
//		def targetOption2 = newFctTargetOption(CODE(6), 2, target, sum)
//		refresh()
//		def location = Location.findByCode(RWANDA)
//		def level = locationService.getLevelAfter(location.getLevel(), new HashSet([LocationLevel.findByCode(SECTOR)]))
//		def dataLocationTypes = new HashSet([DataLocationType.findByCode(DISTRICT_HOSPITAL_GROUP), DataLocationType.findByCode(HEALTH_CENTER_GROUP)])
//
//		when:
//		def fctTable = fctService.getFctTable(location, program, target, period, level, dataLocationTypes)
//
//		then:
//		fctTable.targetOptions[0].equals(FctTargetOption.findByCode(CODE(5)))
//		fctTable.targetOptions[1].equals(FctTargetOption.findByCode(CODE(6)))
//
//		when:
//		FctTargetOption.findByCode(CODE(5)).order = 2
//		FctTargetOption.findByCode(CODE(6)).order = 1
//		refresh()
//		fctTable = fctService.getFctTable(location, program, target, period, level, dataLocationTypes)
//
//		then:
//		fctTable.targetOptions[0].equals(FctTargetOption.findByCode(CODE(6)))
//		fctTable.targetOptions[1].equals(FctTargetOption.findByCode(CODE(5)))
//	}

//	def "get fct with sorted targets"(){
//		setup:
//		setupLocationTree()
//		def period = newPeriod()
//		def normalizedDataElement = newNormalizedDataElement(CODE(1), Type.TYPE_NUMBER(), [(period.id+''):[(DISTRICT_HOSPITAL_GROUP):"1", (HEALTH_CENTER_GROUP):"1"]])
//		def program = newReportProgram(CODE(2))
//		def sum = newSum("\$"+normalizedDataElement.id, CODE(3))
//		def target1 = newFctTarget(CODE(4), 1, [DISTRICT_HOSPITAL_GROUP, HEALTH_CENTER_GROUP], program)
//		def targetOption1 = newFctTargetOption(CODE(5), 1, target1, sum)
//		def target2 = newFctTarget(CODE(6), 2, [DISTRICT_HOSPITAL_GROUP, HEALTH_CENTER_GROUP], program)
//		def targetOption2 = newFctTargetOption(CODE(7), 1, target2, sum)
//		refresh()
//		def location = Location.findByCode(RWANDA)
//		def level = locationService.getLevelAfter(location.getLevel(), new HashSet([LocationLevel.findByCode(SECTOR)]))
//		def dataLocationTypes = new HashSet([DataLocationType.findByCode(DISTRICT_HOSPITAL_GROUP), DataLocationType.findByCode(HEALTH_CENTER_GROUP)])
//
//		when:
//		def fctTable = fctService.getFctTable(location, program, target1, period, level, dataLocationTypes)
//
//		then:
//		fctTable.targets[0].equals(FctTarget.findByCode(CODE(4)))
//		fctTable.targets[1].equals(FctTarget.findByCode(CODE(6)))
//
//		when:
//		FctTarget.findByCode(CODE(4)).order = 2
//		FctTarget.findByCode(CODE(6)).order = 1
//		fctTable = fctService.getFctTable(location, program, target1, period, level, dataLocationTypes)
//
//		then:
//		fctTable.targets[0].equals(FctTarget.findByCode(CODE(6)))
//		fctTable.targets[1].equals(FctTarget.findByCode(CODE(4)))
//	}
		
	def "get fct skip levels"(){
		setup:
		setupLocationTree()
		
		when:
		def fctSkipLevels = fctService.getSkipLocationLevels()
		
		then:
		fctSkipLevels.equals(s([LocationLevel.findByCode(SECTOR)]))
	}
}
