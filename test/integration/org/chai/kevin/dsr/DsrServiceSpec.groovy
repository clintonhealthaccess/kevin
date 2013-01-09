package org.chai.kevin.dsr

import org.chai.kevin.data.Type
import org.chai.location.DataLocation;
import org.chai.location.DataLocationType;
import org.chai.location.Location;
import org.chai.location.LocationLevel;
import org.chai.kevin.util.Utils;
import org.chai.kevin.value.Value;
import org.chai.kevin.reports.ReportService.ReportType;

class DsrServiceSpec extends DsrIntegrationTests {

	def dsrService
	def reportService
	
	def "get dsr"() {
		setup:
		setupLocationTree()
		def period = newPeriod()
		def program = newReportProgram(CODE(1))
		def location = Location.findByCode(BURERA)
		def dataElement = newRawDataElement(CODE(2), Type.TYPE_NUMBER())
		def category = newDsrTargetCategory(CODE(2), program, 1)
		def target = newDsrTarget(CODE(3), 1, dataElement, category)
		def types = new HashSet([
			DataLocationType.findByCode(DISTRICT_HOSPITAL_GROUP),
			DataLocationType.findByCode(HEALTH_CENTER_GROUP)
		])
		def reportType = ReportType.TABLE

		when:
		def dsrTable = dsrService.getDsrTable(location, period, types, category, reportType)

		then:
		dsrTable.getTableReportValue(DataLocation.findByCode(BUTARO), target) == null

		when:
		newRawDataElementValue(dataElement, period, DataLocation.findByCode(BUTARO), Value.VALUE_NUMBER(10d))
		reportService.flushCaches()
		dsrTable = dsrService.getDsrTable(location, period, types, category, reportType)

		then:
		dsrTable.getTableReportValue(DataLocation.findByCode(BUTARO), target).value.getNumberValue() == 10d
	}
		
	def "get dsr with average data"(){
		setup:
		setupLocationTree()
		def period = newPeriod()
		def program = newReportProgram(CODE(1))
		def burera = Location.findByCode(BURERA)
		def average = newSum("1", CODE(2))
		def isAverage = true
		def category = newDsrTargetCategory(CODE(2), program, 1)
		def target = newDsrTarget(CODE(3), 1, average, isAverage, category)
		def types = new HashSet([
			DataLocationType.findByCode(DISTRICT_HOSPITAL_GROUP),
			DataLocationType.findByCode(HEALTH_CENTER_GROUP)
		])
		def reportType = ReportType.TABLE
		refreshCalculation()

		when:
		def dsrTable = dsrService.getDsrTable(burera, period, types, category, reportType)

		then:
		dsrTable != null
		dsrTable.hasData() == true
		dsrTable.getTableReportValue(DataLocation.findByCode(BUTARO), target).value.getNumberValue() == 1
		dsrTable.getTableReportValue(DataLocation.findByCode(KIVUYE), target).value.getNumberValue() == 1
		dsrTable.getTableReportValue(Location.findByCode(BURERA), target).value.getNumberValue() == 2
	}

	def "get dsr with null average data, default to sum data"(){
		setup:
		setupLocationTree()
		def period = newPeriod()
		def program = newReportProgram(CODE(1))
		def burera = Location.findByCode(BURERA)
		def average = newSum("1", CODE(2))
		def isAverage = null
		def category = newDsrTargetCategory(CODE(3), program, 1)
		def target = newDsrTarget(CODE(4), 1, average, isAverage, category)
		def types = new HashSet([
			DataLocationType.findByCode(DISTRICT_HOSPITAL_GROUP),
			DataLocationType.findByCode(HEALTH_CENTER_GROUP)
		])
		def reportType = ReportType.TABLE
		refreshCalculation()

		when:
		def dsrTable = dsrService.getDsrTable(burera, period, types, category, reportType)

		then:
		dsrTable != null
		dsrTable.hasData() == true
		dsrTable.getTableReportValue(DataLocation.findByCode(BUTARO), target).value.getNumberValue() == 1
		dsrTable.getTableReportValue(DataLocation.findByCode(KIVUYE), target).value.getNumberValue() == 1
		dsrTable.getTableReportValue(Location.findByCode(BURERA), target).value.getNumberValue() == 2
	}

	def "get dsr with sum data"(){
		setup:
		setupLocationTree()
		def period = newPeriod()
		def program = newReportProgram(CODE(1))
		def burera = Location.findByCode(BURERA)
		def sum = newSum("1", CODE(2))
		def category = newDsrTargetCategory(CODE(3), program, 1)
		def target = newDsrTarget(CODE(4), sum, category)
		def types = new HashSet([
			DataLocationType.findByCode(DISTRICT_HOSPITAL_GROUP),
			DataLocationType.findByCode(HEALTH_CENTER_GROUP)
		])
		def reportType = ReportType.TABLE
		refreshCalculation()

		when:
		def dsrTable = dsrService.getDsrTable(burera, period, types, category, reportType)

		then:
		dsrTable != null
		dsrTable.hasData() == true
		dsrTable.getTableReportValue(DataLocation.findByCode(BUTARO), target).value.getNumberValue() == 1
		dsrTable.getTableReportValue(DataLocation.findByCode(KIVUYE), target).value.getNumberValue() == 1
		dsrTable.getTableReportValue(Location.findByCode(BURERA), target).value.getNumberValue() == 2		
	}

	def "get dsr with mode data"(){
		setup:
		setupLocationTree()
		def period = newPeriod()
		def program = newReportProgram(CODE(1))
		def burera = Location.findByCode(BURERA)
		def mode = newMode("1", CODE(2), Type.TYPE_NUMBER())
		def category = newDsrTargetCategory(CODE(3), program, 1)
		def target = newDsrTarget(CODE(4), mode, category)
		def types = new HashSet([
			DataLocationType.findByCode(DISTRICT_HOSPITAL_GROUP),
			DataLocationType.findByCode(HEALTH_CENTER_GROUP)
		])
		def reportType = ReportType.TABLE
		refreshCalculation()

		when:
		def dsrTable = dsrService.getDsrTable(burera, period, types, category, reportType)

		then:
		dsrTable != null
		dsrTable.hasData() == true
		dsrTable.getModeList(DataLocation.findByCode(BUTARO), target) == [v(1)]
		dsrTable.getModeList(DataLocation.findByCode(KIVUYE), target) == [v(1)]
		dsrTable.getModeList(Location.findByCode(BURERA), target) == [v(1)]
	}
	
	def "get dsr with raw data element data"(){
		setup:
		setupLocationTree()
		def period = newPeriod()
		def program = newReportProgram(CODE(1))
		def location = Location.findByCode(BURERA)
		def types = new HashSet([
			DataLocationType.findByCode(DISTRICT_HOSPITAL_GROUP),
			DataLocationType.findByCode(HEALTH_CENTER_GROUP)
		])
		def rawDataElement = newRawDataElement(CODE(2), Type.TYPE_NUMBER())
		def category = newDsrTargetCategory(CODE(2), program, 1)
		def target = newDsrTarget(CODE(3), 1, rawDataElement, category)
		newRawDataElementValue(rawDataElement, period, DataLocation.findByCode(BUTARO), Value.VALUE_NUMBER(10d))
		def reportType = ReportType.TABLE

		when:
		def dsrTable = dsrService.getDsrTable(location, period, types, category, reportType)

		then:
		dsrTable != null
		dsrTable.hasData() == true
		dsrTable.getTableReportValue(DataLocation.findByCode(BUTARO), target).value.getNumberValue() == 10d
		dsrTable.getTableReportValue(DataLocation.findByCode(KIVUYE), target) == null

		when:
		newRawDataElementValue(rawDataElement, period, DataLocation.findByCode(KIVUYE), Value.VALUE_NUMBER(10d))
		reportService.flushCaches()
		dsrTable = dsrService.getDsrTable(location, period, types, category, reportType)

		then:
		dsrTable != null
		dsrTable.hasData() == true
		dsrTable.getTableReportValue(DataLocation.findByCode(BUTARO), target).value.getNumberValue() == 10d
		dsrTable.getTableReportValue(DataLocation.findByCode(KIVUYE), target).value.getNumberValue() == 10d
	}

	def "get dsr with normalized data element data"(){
		setupLocationTree()
		def period = newPeriod()
		def program = newReportProgram(CODE(1))
		def location = Location.findByCode(BURERA)
		def types = new HashSet([
			DataLocationType.findByCode(DISTRICT_HOSPITAL_GROUP),
			DataLocationType.findByCode(HEALTH_CENTER_GROUP)
		])
		def normalizedDataElement = newNormalizedDataElement(CODE(1), Type.TYPE_NUMBER(), [(period.id+''):[(DISTRICT_HOSPITAL_GROUP):"10",(HEALTH_CENTER_GROUP):"10"]])
		def category = newDsrTargetCategory(CODE(2), program, 1)
		def target = newDsrTarget(CODE(3), 1, normalizedDataElement, category)
		def reportType = ReportType.TABLE
		refreshNormalizedDataElement()

		when:
		def dsrTable = dsrService.getDsrTable(location, period, types, category, reportType)

		then:
		dsrTable != null
		dsrTable.hasData() == true
		dsrTable.getTableReportValue(DataLocation.findByCode(BUTARO), target).value.getNumberValue() == 10d
		dsrTable.getTableReportValue(DataLocation.findByCode(KIVUYE), target).value.getNumberValue() == 10d
	}

	def "get dsr with normalized data element data and no expression"(){
		setupLocationTree()
		def period = newPeriod()
		def program = newReportProgram(CODE(1))
		def location = Location.findByCode(BURERA)
		def types = new HashSet([
			DataLocationType.findByCode(DISTRICT_HOSPITAL_GROUP),
			DataLocationType.findByCode(HEALTH_CENTER_GROUP)
		])
		def normalizedDataElement = newNormalizedDataElement(CODE(2), Type.TYPE_NUMBER(), [(period.id+''):[(DISTRICT_HOSPITAL_GROUP):"10"]])
		def category = newDsrTargetCategory(CODE(2), program, 1)
		def target = newDsrTarget(CODE(3), 1, normalizedDataElement, category)
		def reportType = ReportType.TABLE
		refreshNormalizedDataElement()

		when:
		def dsrTable = dsrService.getDsrTable(location, period, types, category, reportType)

		then:
		dsrTable != null
		dsrTable.hasData() == true
		dsrTable.getTableReportValue(DataLocation.findByCode(BUTARO), target).value.getNumberValue() == 10d
		dsrTable.getTableReportValue(DataLocation.findByCode(KIVUYE), target).value.isNull()
	}

	def "get dsr with types"() {
		setup:
		setupLocationTree()
		def period = newPeriod()
		def program = newReportProgram(CODE(2))
		def location = Location.findByCode(BURERA)
		def types = new HashSet([
			DataLocationType.findByCode(DISTRICT_HOSPITAL_GROUP),
			DataLocationType.findByCode(HEALTH_CENTER_GROUP)
		])
		def normalizedDataElement = newNormalizedDataElement(CODE(1), Type.TYPE_NUMBER(), [(period.id+''):[(DISTRICT_HOSPITAL_GROUP):"10",(HEALTH_CENTER_GROUP):"10"]])
		def category = newDsrTargetCategory(CODE(3), program, 1)
		def target = newDsrTarget(CODE(4), 1, normalizedDataElement, category)
		def reportType = ReportType.TABLE
		refreshNormalizedDataElement()

		when:
		def dsrTable = dsrService.getDsrTable(location, period, types, category, reportType)

		then:
		dsrTable.getTableReportValue(DataLocation.findByCode(BUTARO), target).value.getNumberValue() == 10d
		dsrTable.getTableReportValue(DataLocation.findByCode(KIVUYE), target).value.getNumberValue() == 10d
	}

	def "get dsr with category"() {
		setup:
		setupLocationTree()
		def period = newPeriod()
		def program = newReportProgram(ROOT)
		def location = Location.findByCode(BURERA)
		def category1 = newDsrTargetCategory(CODE(2), program, 1)
		def dataElement1 = newRawDataElement(CODE(3), Type.TYPE_NUMBER())
		def target1 = newDsrTarget(CODE(4), 1, dataElement1, category1)
		def category2 = newDsrTargetCategory(CODE(5), program, 2)
		def dataElement2 = newRawDataElement(CODE(6), Type.TYPE_NUMBER())
		def target2 = newDsrTarget(CODE(7), 2, dataElement2, category2)
		def types = new HashSet([
			DataLocationType.findByCode(DISTRICT_HOSPITAL_GROUP),
			DataLocationType.findByCode(HEALTH_CENTER_GROUP)
		])
		def reportType = ReportType.TABLE
		refresh()

		when:
		def dsrTable = dsrService.getDsrTable(location, period, types, category1, reportType)

		then:
		dsrTable.indicators.size == 1
		dsrTable.indicators.equals([target1])
	}

	def "get dsr with sorted targets"() {
		setup:
		setupLocationTree()
		def period = newPeriod()
		def program = newReportProgram(ROOT)
		def location = Location.findByCode(BURERA)
		def types = new HashSet([
			DataLocationType.findByCode(DISTRICT_HOSPITAL_GROUP),
			DataLocationType.findByCode(HEALTH_CENTER_GROUP)
		])
		def category = newDsrTargetCategory(CODE(1), program, 1)
		def dataElement1 = newRawDataElement(CODE(2), Type.TYPE_NUMBER())
		def target1 = newDsrTarget(CODE(3), 1, dataElement1, category)
		def dataElement2 = newRawDataElement(CODE(4), Type.TYPE_NUMBER())
		def target2 = newDsrTarget(CODE(5), 2, dataElement2, category)
		def reportType = ReportType.TABLE
		refresh()

		when:
		def dsrTable = dsrService.getDsrTable(location, period, types, category, reportType)

		then:
		dsrTable.indicators[0].equals(DsrTarget.findByCode(CODE(3)))
		dsrTable.indicators[1].equals(DsrTarget.findByCode(CODE(5)))

		when:
		DsrTarget.findByCode(CODE(3)).order = 2
		DsrTarget.findByCode(CODE(5)).order = 1
		dsrTable = dsrService.getDsrTable(location, period, types, category, reportType)

		then:
		dsrTable.indicators[0].equals(DsrTarget.findByCode(CODE(5)))
		dsrTable.indicators[1].equals(DsrTarget.findByCode(CODE(3)))
	}

	def "get dsr skip location levels"(){
		setup:
		setupLocationTree()

		when:
		def dsrSkipLevels = dsrService.getSkipLocationLevels()

		then:
		dsrSkipLevels.equals(s([
			LocationLevel.findByCode(SECTOR)
		]))
	}
	
	def "get dsr skip view levels"(){
		setup:
		setupLocationTree()

		when:
		def dsrSkipLevels = dsrService.getSkipViewLevels(ReportType.MAP)

		then:
		dsrSkipLevels.equals(s([
			LocationLevel.findByCode(NATIONAL),
			LocationLevel.findByCode(PROVINCE),
			LocationLevel.findByCode(SECTOR)
		]))
		
		when:
		dsrSkipLevels = dsrService.getSkipViewLevels(ReportType.TABLE)
		
		then:
		dsrSkipLevels.equals(s([
			LocationLevel.findByCode(SECTOR)
		]))
	}

}