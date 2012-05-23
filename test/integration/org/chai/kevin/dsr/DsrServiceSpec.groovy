package org.chai.kevin.dsr

import org.chai.kevin.data.Type
import org.chai.kevin.location.DataLocation;
import org.chai.kevin.location.DataLocationType;
import org.chai.kevin.location.Location;
import org.chai.kevin.location.LocationLevel;
import org.chai.kevin.value.Value;

class DsrServiceSpec extends DsrIntegrationTests {

	def dsrService	
	
	def "normal dsr service"() {
		setup:
		setupLocationTree()
		def period = newPeriod()
		def program = newReportProgram(CODE(1))
		def dataElement = newRawDataElement(CODE(2), Type.TYPE_NUMBER())
		def target = newDsrTarget(CODE(3), 1, dataElement, program)		
		
		when:
		def location = Location.findByCode(BURERA)
		def types = new HashSet([DataLocationType.findByCode(DISTRICT_HOSPITAL_GROUP), DataLocationType.findByCode(HEALTH_CENTER_GROUP)])
		def dsrTable = dsrService.getDsrTable(location, program, period, types, null)
		
		then:
		dsrTable.getReportValue(DataLocation.findByCode(BUTARO), target) == null
		
		when:
		newRawDataElementValue(dataElement, period, DataLocation.findByCode(BUTARO), Value.VALUE_NUMBER(10d))
		dsrTable = dsrService.getDsrTable(location, program, period, types, null)
		
		then:
		dsrTable.getReportValue(DataLocation.findByCode(BUTARO), target).getNumberValue() == 10d	

	}
	
	def "get dsr with sorted targets"() {
		setup:
		setupLocationTree()
		def period = newPeriod()
		def program = newReportProgram(ROOT)
		def dataElement1 = newRawDataElement(CODE(2), Type.TYPE_NUMBER())
		def target1 = newDsrTarget(CODE(3), 1, dataElement1, program)
		def dataElement2 = newRawDataElement(CODE(4), Type.TYPE_NUMBER())
		def target2 = newDsrTarget(CODE(5), 2, dataElement2, program)
		refresh()
		
		when:
		def location = Location.findByCode(BURERA)
		def types = new HashSet([DataLocationType.findByCode(DISTRICT_HOSPITAL_GROUP), DataLocationType.findByCode(HEALTH_CENTER_GROUP)])
		def dsrTable = dsrService.getDsrTable(location, program, period, types, null)
		
		then:
		dsrTable.targets[0].equals(DsrTarget.findByCode(CODE(3)))
		dsrTable.targets[1].equals(DsrTarget.findByCode(CODE(5)))
		
		when:
		DsrTarget.findByCode(CODE(3)).order = 2
		DsrTarget.findByCode(CODE(5)).order = 1
		dsrTable = dsrService.getDsrTable(location, program, period, types, null)
		
		then:
		dsrTable.targets[0].equals(DsrTarget.findByCode(CODE(5)))
		dsrTable.targets[1].equals(DsrTarget.findByCode(CODE(3)))
	}
	
	def "get dsr with sorted target categories"() {
		setup:
		setupLocationTree()
		def period = newPeriod()
		def program = newReportProgram(ROOT)
		def cat1 = newDsrTargetCategory(CODE(2), 1)
		def dataElement1 = newRawDataElement(CODE(3), Type.TYPE_NUMBER())
		def target1 = newDsrTarget(CODE(4), 1, dataElement1, program, cat1)
		def cat2 = newDsrTargetCategory(CODE(5), 2)
		def dataElement2 = newRawDataElement(CODE(6), Type.TYPE_NUMBER())
		def target2 = newDsrTarget(CODE(7), 2, dataElement2, program, cat2)
		refresh()
		
		when:
		def location = Location.findByCode(BURERA)
		def types = new HashSet([DataLocationType.findByCode(DISTRICT_HOSPITAL_GROUP), DataLocationType.findByCode(HEALTH_CENTER_GROUP)])
		def dsrTable = dsrService.getDsrTable(location, program, period, types, null)
		
		then:
		dsrTable.targetCategories[0].equals(DsrTargetCategory.findByCode(CODE(2)))
		dsrTable.targetCategories[1].equals(DsrTargetCategory.findByCode(CODE(5)))
		
		when:
		DsrTargetCategory.findByCode(CODE(2)).order = 2
		DsrTargetCategory.findByCode(CODE(5)).order = 1
		dsrTable = dsrService.getDsrTable(location, program, period, types, null)
		
		then:
		dsrTable.targetCategories[0].equals(DsrTargetCategory.findByCode(CODE(5)))
		dsrTable.targetCategories[1].equals(DsrTargetCategory.findByCode(CODE(2)))
	}
	
	def "get dsr with types"() {
		setup:
		setupLocationTree()
		def period = newPeriod()
		def normalizedDataElement = newNormalizedDataElement(CODE(1), Type.TYPE_NUMBER(), e([(period.id+''):[(DISTRICT_HOSPITAL_GROUP):"10",(HEALTH_CENTER_GROUP):"10"]]))
		def program = newReportProgram(CODE(2))
		def target = newDsrTarget(CODE(3), 1, normalizedDataElement, program)	
		refreshNormalizedDataElement()
		def location = Location.findByCode(BURERA)
		def types = new HashSet([DataLocationType.findByCode(DISTRICT_HOSPITAL_GROUP), DataLocationType.findByCode(HEALTH_CENTER_GROUP)])
		
		when:
		def dsrTable = dsrService.getDsrTable(location, program, period, types, null)
		
		then:
		dsrTable.getReportValue(DataLocation.findByCode(BUTARO), target).getNumberValue() == 10d	
		dsrTable.getReportValue(DataLocation.findByCode(KIVUYE), target).getNumberValue() == 10d
		
	}
	
	def "get dsr with normalized data element and no expression"() {
		setup:
		setupLocationTree()
		def period = newPeriod()
		def normalizedDataElement = newNormalizedDataElement(CODE(1), Type.TYPE_NUMBER(), e([(period.id+''):[(DISTRICT_HOSPITAL_GROUP):"10"]]))
		def program = newReportProgram(CODE(2))
		def target = newDsrTarget(CODE(3), 1, normalizedDataElement, program)
		refreshNormalizedDataElement()
		def location = Location.findByCode(BURERA)
		def types = new HashSet([DataLocationType.findByCode(DISTRICT_HOSPITAL_GROUP), DataLocationType.findByCode(HEALTH_CENTER_GROUP)])
		
		when:
		def dsrTable = dsrService.getDsrTable(location, program, period, types, null)		
		
		then:
		dsrTable.getReportValue(DataLocation.findByCode(BUTARO), target).getNumberValue() == 10d
		dsrTable.getReportValue(DataLocation.findByCode(KIVUYE), target).isNull()
		
	}
	
	def "get dsr skip levels"(){
		setup:
		setupLocationTree()
		
		when:
		def dsrSkipLevels = dsrService.getSkipLocationLevels()
		
		then:
		dsrSkipLevels.equals(s([LocationLevel.findByCode(SECTOR)])) 
	}
}
