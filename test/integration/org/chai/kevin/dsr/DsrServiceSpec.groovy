package org.chai.kevin.dsr

import org.chai.kevin.data.Type
import org.chai.kevin.location.DataLocation;
import org.chai.kevin.location.DataLocationType;
import org.chai.kevin.location.Location;
import org.chai.kevin.location.LocationLevel;
import org.chai.kevin.util.Utils;
import org.chai.kevin.value.Value;

class DsrServiceSpec extends DsrIntegrationTests {

	def dsrService

	def "get dsr"() {
		setup:
		setupLocationTree()
		def period = newPeriod()
		def program = newReportProgram(CODE(1))
		def location = Location.findByCode(BURERA)
		def dataElement = newRawDataElement(CODE(2), Type.TYPE_NUMBER())
		def category = newDsrTargetCategory(CODE(1), 1)
		def target = newDsrTarget(CODE(3), 1, dataElement, program, category)
		def types = new HashSet([
			DataLocationType.findByCode(DISTRICT_HOSPITAL_GROUP),
			DataLocationType.findByCode(HEALTH_CENTER_GROUP)
		])
		def reportType = Utils.ReportType.TABLE

		when:
		def dsrTable = dsrService.getDsrTable(location, program, period, types, category, reportType)

		then:
		dsrTable.getReportValue(DataLocation.findByCode(BUTARO), target) == null

		when:
		newRawDataElementValue(dataElement, period, DataLocation.findByCode(BUTARO), Value.VALUE_NUMBER(10d))
		dsrTable = dsrService.getDsrTable(location, program, period, types, category, reportType)

		then:
		dsrTable.getReportValue(DataLocation.findByCode(BUTARO), target).getNumberValue() == 10d
	}
		
	def "get dsr with average calculation element"(){
		setup:
		setupLocationTree()
		def period = newPeriod()
		def program = newReportProgram(CODE(1))
		def burera = Location.findByCode(BURERA)
		def average = newSum("1", CODE(2))
		def isAverage = true
		def category = newDsrTargetCategory(CODE(1), 1)
		def target = newDsrTarget(CODE(3), 1, average, isAverage, program, category)
		def types = new HashSet([
			DataLocationType.findByCode(DISTRICT_HOSPITAL_GROUP),
			DataLocationType.findByCode(HEALTH_CENTER_GROUP)
		])
		def reportType = Utils.ReportType.TABLE
		refreshCalculation()

		when:
		def dsrTable = dsrService.getDsrTable(burera, program, period, types, category, reportType)

		then:
		dsrTable != null
		dsrTable.hasData() == true
		dsrTable.locations.findAll{ it -> it instanceof DataLocation }.size() == 2
		dsrTable.locations.findAll{ it -> it instanceof Location }.size() == 1
		dsrTable.getReportValue(DataLocation.findByCode(BUTARO), target).getNumberValue() == 1
		dsrTable.getReportValue(DataLocation.findByCode(KIVUYE), target).getNumberValue() == 1
		dsrTable.getReportValue(Location.findByCode(BURERA), target).getNumberValue() == 1
	}

	def "get dsr with null average calculation element, default to sum calculation element"(){
		setup:
		setupLocationTree()
		def period = newPeriod()
		def program = newReportProgram(CODE(1))
		def burera = Location.findByCode(BURERA)
		def average = newSum("1", CODE(2))
		def isAverage = null
		def category = newDsrTargetCategory(CODE(1), 1)
		def target = newDsrTarget(CODE(3), 1, average, isAverage, program, category)
		def types = new HashSet([
			DataLocationType.findByCode(DISTRICT_HOSPITAL_GROUP),
			DataLocationType.findByCode(HEALTH_CENTER_GROUP)
		])
		def reportType = Utils.ReportType.TABLE
		refreshCalculation()

		when:
		def dsrTable = dsrService.getDsrTable(burera, program, period, types, category, reportType)

		then:
		dsrTable != null
		dsrTable.hasData() == true
		dsrTable.locations.findAll{ it -> it instanceof DataLocation }.size() == 2
		dsrTable.locations.findAll{ it -> it instanceof Location }.size() == 1
		dsrTable.getReportValue(DataLocation.findByCode(BUTARO), target).getNumberValue() == 1
		dsrTable.getReportValue(DataLocation.findByCode(KIVUYE), target).getNumberValue() == 1
		dsrTable.getReportValue(Location.findByCode(BURERA), target).getNumberValue() == 2
	}

	def "get dsr with sum calculation element"(){
		setup:
		setupLocationTree()
		def period = newPeriod()
		def program = newReportProgram(CODE(1))
		def burera = Location.findByCode(BURERA)
		def sum = newSum("1", CODE(2))
		def category = newDsrTargetCategory(CODE(1), 1)
		def target = newDsrTarget(CODE(3), 1, sum, program, category)
		def types = new HashSet([
			DataLocationType.findByCode(DISTRICT_HOSPITAL_GROUP),
			DataLocationType.findByCode(HEALTH_CENTER_GROUP)
		])
		def reportType = Utils.ReportType.TABLE
		refreshCalculation()

		when:
		def dsrTable = dsrService.getDsrTable(burera, program, period, types, category, reportType)

		then:
		dsrTable != null
		dsrTable.hasData() == true
		dsrTable.locations.findAll{ it -> it instanceof DataLocation }.size() == 2
		dsrTable.locations.findAll{ it -> it instanceof Location }.size() == 1
		dsrTable.getReportValue(DataLocation.findByCode(BUTARO), target).getNumberValue() == 1
		dsrTable.getReportValue(DataLocation.findByCode(KIVUYE), target).getNumberValue() == 1
		dsrTable.getReportValue(Location.findByCode(BURERA), target).getNumberValue() == 2		
	}

	def "get dsr with raw data element calculation element"(){
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
		def category = newDsrTargetCategory(CODE(1), 1)
		def target = newDsrTarget(CODE(3), 1, rawDataElement, program, category)
		newRawDataElementValue(rawDataElement, period, DataLocation.findByCode(BUTARO), Value.VALUE_NUMBER(10d))
		def reportType = Utils.ReportType.TABLE

		when:
		def dsrTable = dsrService.getDsrTable(location, program, period, types, category, reportType)

		then:
		dsrTable != null
		dsrTable.hasData() == true
		dsrTable.locations.findAll{ it -> it instanceof DataLocation }.size() == 2
		dsrTable.locations.findAll{ it -> it instanceof Location }.size() == 0
		dsrTable.getReportValue(DataLocation.findByCode(BUTARO), target).getNumberValue() == 10d
		dsrTable.getReportValue(DataLocation.findByCode(KIVUYE), target) == null

		when:
		newRawDataElementValue(rawDataElement, period, DataLocation.findByCode(KIVUYE), Value.VALUE_NUMBER(10d))
		dsrTable = dsrService.getDsrTable(location, program, period, types, category, reportType)

		then:
		dsrTable != null
		dsrTable.hasData() == true
		dsrTable.locations.findAll{ it -> it instanceof DataLocation }.size() == 2
		dsrTable.locations.findAll{ it -> it instanceof Location }.size() == 0
		dsrTable.getReportValue(DataLocation.findByCode(BUTARO), target).getNumberValue() == 10d
		dsrTable.getReportValue(DataLocation.findByCode(KIVUYE), target).getNumberValue() == 10d
	}

	def "get dsr with normalized data element calculation element"(){
		setupLocationTree()
		def period = newPeriod()
		def program = newReportProgram(CODE(1))
		def location = Location.findByCode(BURERA)
		def types = new HashSet([
			DataLocationType.findByCode(DISTRICT_HOSPITAL_GROUP),
			DataLocationType.findByCode(HEALTH_CENTER_GROUP)
		])
		def normalizedDataElement = newNormalizedDataElement(CODE(1), Type.TYPE_NUMBER(), e([(period.id+''):[(DISTRICT_HOSPITAL_GROUP):"10",(HEALTH_CENTER_GROUP):"10"]]))
		def category = newDsrTargetCategory(CODE(1), 1)
		def target = newDsrTarget(CODE(3), 1, normalizedDataElement, program, category)
		def reportType = Utils.ReportType.TABLE
		refreshNormalizedDataElement()

		when:
		def dsrTable = dsrService.getDsrTable(location, program, period, types, category, reportType)

		then:
		dsrTable != null
		dsrTable.hasData() == true
		dsrTable.locations.findAll{ it -> it instanceof DataLocation }.size() == 2
		dsrTable.locations.findAll{ it -> it instanceof Location }.size() == 0
		dsrTable.getReportValue(DataLocation.findByCode(BUTARO), target).getNumberValue() == 10d
		dsrTable.getReportValue(DataLocation.findByCode(KIVUYE), target).getNumberValue() == 10d
	}

	def "get dsr with normalized data element calculation element and no expression"(){
		setupLocationTree()
		def period = newPeriod()
		def program = newReportProgram(CODE(1))
		def location = Location.findByCode(BURERA)
		def types = new HashSet([
			DataLocationType.findByCode(DISTRICT_HOSPITAL_GROUP),
			DataLocationType.findByCode(HEALTH_CENTER_GROUP)
		])
		def normalizedDataElement = newNormalizedDataElement(CODE(2), Type.TYPE_NUMBER(), e([(period.id+''):[(DISTRICT_HOSPITAL_GROUP):"10"]]))
		def category = newDsrTargetCategory(CODE(1), 1)
		def target = newDsrTarget(CODE(3), 1, normalizedDataElement, program, category)
		def reportType = Utils.ReportType.TABLE
		refreshNormalizedDataElement()

		when:
		def dsrTable = dsrService.getDsrTable(location, program, period, types, category, reportType)

		then:
		dsrTable != null
		dsrTable.hasData() == true
		dsrTable.locations.findAll{ it -> it instanceof DataLocation }.size() == 2
		dsrTable.locations.findAll{ it -> it instanceof Location }.size() == 0
		dsrTable.getReportValue(DataLocation.findByCode(BUTARO), target).getNumberValue() == 10d
		dsrTable.getReportValue(DataLocation.findByCode(KIVUYE), target) == v(null)
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
		def normalizedDataElement = newNormalizedDataElement(CODE(1), Type.TYPE_NUMBER(), e([(period.id+''):[(DISTRICT_HOSPITAL_GROUP):"10",(HEALTH_CENTER_GROUP):"10"]]))
		def category = newDsrTargetCategory(CODE(1), 1)
		def target = newDsrTarget(CODE(3), 1, normalizedDataElement, program, category)
		def reportType = Utils.ReportType.TABLE
		refreshNormalizedDataElement()

		when:
		def dsrTable = dsrService.getDsrTable(location, program, period, types, category, reportType)

		then:
		dsrTable.getReportValue(DataLocation.findByCode(BUTARO), target).getNumberValue() == 10d
		dsrTable.getReportValue(DataLocation.findByCode(KIVUYE), target).getNumberValue() == 10d
	}

	def "get dsr with category"() {
		setup:
		setupLocationTree()
		def period = newPeriod()
		def program = newReportProgram(ROOT)
		def location = Location.findByCode(BURERA)
		def category1 = newDsrTargetCategory(CODE(2), 1)
		def dataElement1 = newRawDataElement(CODE(3), Type.TYPE_NUMBER())
		def target1 = newDsrTarget(CODE(4), 1, dataElement1, program, category1)
		def category2 = newDsrTargetCategory(CODE(5), 2)
		def dataElement2 = newRawDataElement(CODE(6), Type.TYPE_NUMBER())
		def target2 = newDsrTarget(CODE(7), 2, dataElement2, program, category2)
		def types = new HashSet([
			DataLocationType.findByCode(DISTRICT_HOSPITAL_GROUP),
			DataLocationType.findByCode(HEALTH_CENTER_GROUP)
		])
		def reportType = Utils.ReportType.TABLE
		refresh()

		when:
		def dsrTable = dsrService.getDsrTable(location, program, period, types, category1, reportType)

		then:
		dsrTable.targetCategories.size == 2
		dsrTable.targetCategories.equals([category1, category2])
		dsrTable.targets.size == 1
		dsrTable.targets.equals([target1])
		dsrTable.targets.get(0).category.equals(category1)
	}

	def "get dsr with report type"() {
		setup:
		setupLocationTree()
		def period = newPeriod()
		def program = newReportProgram(ROOT)
		def location = Location.findByCode(NORTH)
		def category1 = newDsrTargetCategory(CODE(2), 1)
		def average = newSum("1", CODE(3))
		def isAverage = true
		def target1 = newDsrTarget(CODE(4), 1, average, program, category1)		
		def types = new HashSet([
			DataLocationType.findByCode(DISTRICT_HOSPITAL_GROUP),
			DataLocationType.findByCode(HEALTH_CENTER_GROUP)
		])
		refresh()

		when: //report type is table
		def dsrTable = dsrService.getDsrTable(location, program, period, types, category1, Utils.ReportType.TABLE)

		then:
		dsrTable.locations.size() == 4
		dsrTable.locations.equals(s([Location.findByCode(NORTH), Location.findByCode(BURERA), DataLocation.findByCode(BUTARO), DataLocation.findByCode(KIVUYE)]))
		
		when: //report type is map
		dsrTable = dsrService.getDsrTable(location, program, period, types, category1, Utils.ReportType.MAP)
		
		then:
		dsrTable.locations.size() == 1
		dsrTable.locations.equals(s([Location.findByCode(BURERA)]))
		
	}

	def "get dsr table report value"() {
		setup:
		setupLocationTree()
		def period = newPeriod()
		def program = newReportProgram(CODE(1))
		def location = Location.findByCode(BURERA)
		def dataElement = newRawDataElement(CODE(2), Type.TYPE_NUMBER())
		def category = newDsrTargetCategory(CODE(1), 1)
		def target = newDsrTarget(CODE(3), 1, dataElement, program, category)
		def types = new HashSet([
			DataLocationType.findByCode(DISTRICT_HOSPITAL_GROUP),
			DataLocationType.findByCode(HEALTH_CENTER_GROUP)
		])
		def reportType = Utils.ReportType.TABLE

		when:
		def dsrTable = dsrService.getDsrTable(location, program, period, types, category, reportType)

		then:
		dsrTable.getTableReportValue(DataLocation.findByCode(BUTARO), target) == null

		when:
		newRawDataElementValue(dataElement, period, DataLocation.findByCode(BUTARO), Value.VALUE_NUMBER(10d))
		dsrTable = dsrService.getDsrTable(location, program, period, types, category, reportType)

		then:
		dsrTable.getTableReportValue(DataLocation.findByCode(BUTARO), target).getNumberValue() == 10d
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
		def category = newDsrTargetCategory(CODE(1), 1)
		def dataElement1 = newRawDataElement(CODE(2), Type.TYPE_NUMBER())
		def target1 = newDsrTarget(CODE(3), 1, dataElement1, program, category)
		def dataElement2 = newRawDataElement(CODE(4), Type.TYPE_NUMBER())
		def target2 = newDsrTarget(CODE(5), 2, dataElement2, program, category)
		def reportType = Utils.ReportType.TABLE
		refresh()

		when:
		def dsrTable = dsrService.getDsrTable(location, program, period, types, category, reportType)

		then:
		dsrTable.targets[0].equals(DsrTarget.findByCode(CODE(3)))
		dsrTable.targets[1].equals(DsrTarget.findByCode(CODE(5)))

		when:
		DsrTarget.findByCode(CODE(3)).order = 2
		DsrTarget.findByCode(CODE(5)).order = 1
		dsrTable = dsrService.getDsrTable(location, program, period, types, category, reportType)

		then:
		dsrTable.targets[0].equals(DsrTarget.findByCode(CODE(5)))
		dsrTable.targets[1].equals(DsrTarget.findByCode(CODE(3)))
	}

	def "get dsr with sorted target categories"() {
		setup:
		setupLocationTree()
		def period = newPeriod()
		def program = newReportProgram(ROOT)
		def location = Location.findByCode(BURERA)
		def types = new HashSet([
			DataLocationType.findByCode(DISTRICT_HOSPITAL_GROUP),
			DataLocationType.findByCode(HEALTH_CENTER_GROUP)
		])
		def category1 = newDsrTargetCategory(CODE(2), 1)
		def dataElement1 = newRawDataElement(CODE(3), Type.TYPE_NUMBER())
		def target1 = newDsrTarget(CODE(4), 1, dataElement1, program, category1)
		def category2 = newDsrTargetCategory(CODE(5), 2)
		def dataElement2 = newRawDataElement(CODE(6), Type.TYPE_NUMBER())
		def target2 = newDsrTarget(CODE(7), 2, dataElement2, program, category2)
		def reportType = Utils.ReportType.TABLE
		refresh()

		when:
		def dsrTable = dsrService.getDsrTable(location, program, period, types, category1, reportType)

		then:
		dsrTable.targetCategories[0].equals(category1)
		dsrTable.targetCategories[1].equals(category2)

		when:
		DsrTargetCategory.findByCode(CODE(2)).order = 2
		DsrTargetCategory.findByCode(CODE(5)).order = 1
		dsrTable = dsrService.getDsrTable(location, program, period, types, category1, reportType)

		then:
		dsrTable.targetCategories[0].equals(category2)
		dsrTable.targetCategories[1].equals(category1)
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
		def dsrSkipLevels = dsrService.getSkipViewLevels(Utils.ReportType.MAP)

		then:
		dsrSkipLevels.equals(s([
			LocationLevel.findByCode(NATIONAL),
			LocationLevel.findByCode(PROVINCE),
			LocationLevel.findByCode(SECTOR)
		]))
		
		when:
		dsrSkipLevels = dsrService.getSkipViewLevels(Utils.ReportType.TABLE)
		
		then:
		dsrSkipLevels.equals(s([
			LocationLevel.findByCode(SECTOR)
		]))
	}

	def "get dsr map report value"() {
		setup:
		setupLocationTree()
		def period = newPeriod()
		def program = newReportProgram(CODE(1))
		def location = Location.findByCode(BURERA)
		def dataElement = newRawDataElement(CODE(2), Type.TYPE_NUMBER())
		def category = newDsrTargetCategory(CODE(1), 1)
		def target = newDsrTarget(CODE(3), 1, dataElement, program, category)
		def types = new HashSet([
			DataLocationType.findByCode(DISTRICT_HOSPITAL_GROUP),
			DataLocationType.findByCode(HEALTH_CENTER_GROUP)
		])
		def reportType = Utils.ReportType.TABLE

		when:
		def dsrTable = dsrService.getDsrTable(location, program, period, types, category, reportType)

		then:
		dsrTable.getMapReportValue(DataLocation.findByCode(BUTARO), target) == null

		when:
		newRawDataElementValue(dataElement, period, DataLocation.findByCode(BUTARO), Value.VALUE_NUMBER(10d))
		dsrTable = dsrService.getDsrTable(location, program, period, types, category, reportType)

		then:
		dsrTable.getMapReportValue(DataLocation.findByCode(BUTARO), target).getNumberValue() == 10d
	}
	
}