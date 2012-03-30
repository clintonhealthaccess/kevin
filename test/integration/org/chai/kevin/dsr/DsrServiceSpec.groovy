package org.chai.kevin.dsr

import org.chai.kevin.data.Type
import org.chai.kevin.location.DataLocation;
import org.chai.kevin.location.DataLocationType;
import org.chai.kevin.location.Location;
import org.chai.kevin.location.LocationLevel;
import org.chai.kevin.value.Value;

class DsrServiceSpec extends DsrIntegrationTests {

	def dsrService
	
	
	def "test normal dsr service"() {
		setup:
		setupLocationTree()
		def period = newPeriod()
		def program = newReportProgram(CODE(1))
		def dataElement = newRawDataElement(CODE(3), Type.TYPE_NUMBER())
		def target = newDsrTarget(CODE(2), dataElement, [DISTRICT_HOSPITAL_GROUP], program)
		def location = Location.findByCode(BURERA)
		def types = new HashSet([DataLocationType.findByCode(DISTRICT_HOSPITAL_GROUP), DataLocationType.findByCode(HEALTH_CENTER_GROUP)])
		def dsrTable = null
		
		when:
		dsrTable = dsrService.getDsrTable(location, program, period, types, null)
		
		then:
		dsrTable.getReportValue(DataLocation.findByCode(BUTARO), target) == null
		
		when:
		newRawDataElementValue(dataElement, period, DataLocation.findByCode(BUTARO), Value.VALUE_NUMBER(10d))
		dsrTable = dsrService.getDsrTable(location, program, period, types, null)
		
		then:
		dsrTable.getReportValue(DataLocation.findByCode(BUTARO), target).getNumberValue() == 10d	

	}
	
	def "test dsr with types"() {
		when:
		setupLocationTree()
		def period = newPeriod()
		def normalizedDataElement = newNormalizedDataElement(CODE(1), Type.TYPE_NUMBER(), e([(period.id+''):[(DISTRICT_HOSPITAL_GROUP):"10",(HEALTH_CENTER_GROUP):"10"]]))
		def program = newReportProgram(CODE(2))
		def target = newDsrTarget(CODE(3), normalizedDataElement, [DISTRICT_HOSPITAL_GROUP], program)
		refreshNormalizedDataElement()
		def location = Location.findByCode(BURERA)
		def types = new HashSet([DataLocationType.findByCode(DISTRICT_HOSPITAL_GROUP), DataLocationType.findByCode(HEALTH_CENTER_GROUP)])
		def dsrTable = dsrService.getDsrTable(location, program, period, types, null)
		
		then:
		dsrTable.getReportValue(DataLocation.findByCode(BUTARO), target).getNumberValue() == 10d	
		dsrTable.getReportValue(DataLocation.findByCode(KIVUYE), target) == null
		
	}
	
	def "test dsr with normalized data element and no expression"() {
		when:
		setupLocationTree()
		def period = newPeriod()
		def normalizedDataElement = newNormalizedDataElement(CODE(1), Type.TYPE_NUMBER(), e([(period.id+''):[(DISTRICT_HOSPITAL_GROUP):"10"]]))
		def program = newReportProgram(CODE(2))
		def target = newDsrTarget(CODE(3), normalizedDataElement, [DISTRICT_HOSPITAL_GROUP, HEALTH_CENTER_GROUP], program)
		refreshNormalizedDataElement()
		def location = Location.findByCode(BURERA)
		def types = new HashSet([DataLocationType.findByCode(DISTRICT_HOSPITAL_GROUP), DataLocationType.findByCode(HEALTH_CENTER_GROUP)])
		def dsrTable = dsrService.getDsrTable(location, program, period, types, null)
		
		then:
		dsrTable.getReportValue(DataLocation.findByCode(BUTARO), target).getNumberValue() == 10d
		dsrTable.getReportValue(DataLocation.findByCode(KIVUYE), target).isNull()
		
	}
	
	def "test dsr with category"(){
		//TODO
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
