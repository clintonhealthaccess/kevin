package org.chai.kevin.dsr

import org.chai.kevin.data.Type
import org.chai.kevin.location.DataLocationEntity;
import org.chai.kevin.location.DataEntityType;
import org.chai.kevin.location.LocationEntity;

class DsrServiceSpec extends DsrIntegrationTests {

	def dsrService
	
	def "test normal dsr service"() {
		setup:
		setupLocationTree()
		def period = newPeriod()
		def program = newReportProgram(CODE(1))
		def dataElement = newRawDataElement(CODE(3), Type.TYPE_NUMBER())
		def target = newDsrTarget(CODE(2), dataElement, [], program)
		def location = LocationEntity.findByCode(BURERA)
		def types = new HashSet([DataEntityType.findByCode(DISTRICT_HOSPITAL_GROUP), DataEntityType.findByCode(HEALTH_CENTER_GROUP)])
		def dsrTable = null
		
		when:
		dsrTable = dsrService.getDsrTable(location, program, period, types, null)
		
		then:
		dsrTable.getReportValue(DataLocationEntity.findByCode(BUTARO), target) != null		

	}
	
	def "test dsr with non-existing enum option"() {
		setup:
		setupLocationTree()
		def period = newPeriod()
		def program = newReportProgram(CODE(1))
		def enume = newEnume("enum")
		def dataElement = newRawDataElement(CODE(3), Type.TYPE_ENUM("enum"))
		def target = newDsrTarget(CODE(2), dataElement, [DISTRICT_HOSPITAL_GROUP, HEALTH_CENTER_GROUP], program)
		def types = new HashSet([DataEntityType.findByCode(DISTRICT_HOSPITAL_GROUP), DataEntityType.findByCode(HEALTH_CENTER_GROUP)])
		def dsrTable = null
		
		when:
		newRawDataElementValue(dataElement, period, DataLocationEntity.findByCode(BUTARO), v("\"option\""))
		dsrTable = dsrService.getDsrTable(LocationEntity.findByCode(BURERA), program, period, types, null)
		
		then:
		dsrTable.getReportValue(DataLocationEntity.findByCode(BUTARO), target).value == "option"
	}
	
	def "test dsr formatting"() {
		when:
		setupLocationTree()
		def period = newPeriod()
		def normalizedDataElement = newNormalizedDataElement(CODE(1), Type.TYPE_NUMBER(), e([(period.id+''):[(DISTRICT_HOSPITAL_GROUP):"10",(HEALTH_CENTER_GROUP):"10"]]))
		def program = newReportProgram(CODE(2))
		def target = newDsrTarget(CODE(3), normalizedDataElement, format, [DISTRICT_HOSPITAL_GROUP, HEALTH_CENTER_GROUP], program)
		refreshNormalizedDataElement()
		def location = LocationEntity.findByCode(BURERA)
		def types = new HashSet([DataEntityType.findByCode(DISTRICT_HOSPITAL_GROUP), DataEntityType.findByCode(HEALTH_CENTER_GROUP)])
		def dsrTable = dsrService.getDsrTable(location, program, period, types, null)
		
		then:
		dsrTable.getReportValue(DataLocationEntity.findByCode(BUTARO), target).value == value
		
		where:
		format	| value
		"#"		| "10"
		""		| "10"
		"#.0"	| "10.0"
		
	}

	def "test dsr with no types should return no value"() {
		when:
		setupLocationTree()
		def period = newPeriod()
		def normalizedDataElement = newNormalizedDataElement(CODE(1), Type.TYPE_NUMBER(), e([(period.id+''):[(DISTRICT_HOSPITAL_GROUP):"10",(HEALTH_CENTER_GROUP):"10"]]))
		def program = newReportProgram(CODE(2))
		def target = newDsrTarget(CODE(3), normalizedDataElement, [], program)
		refreshNormalizedDataElement()
		def location = LocationEntity.findByCode(BURERA)
		def types = new HashSet([DataEntityType.findByCode(DISTRICT_HOSPITAL_GROUP), DataEntityType.findByCode(HEALTH_CENTER_GROUP)])
		def dsrTable = dsrService.getDsrTable(location, program, period, types, null)
		
		then:
		dsrTable.getReportValue(DataLocationEntity.findByCode(locationName), target).value == "N/A"
		
		where:
		locationName << [BUTARO, KIVUYE]
		
	}
	
	def "test dsr with types"() {
		when:
		setupLocationTree()
		def period = newPeriod()
		def normalizedDataElement = newNormalizedDataElement(CODE(1), Type.TYPE_NUMBER(), e([(period.id+''):[(DISTRICT_HOSPITAL_GROUP):"10",(HEALTH_CENTER_GROUP):"10"]]))
		def program = newReportProgram(CODE(2))
		def target = newDsrTarget(CODE(3), normalizedDataElement, [DISTRICT_HOSPITAL_GROUP], program)
		refreshNormalizedDataElement()
		def location = LocationEntity.findByCode(BURERA)
		def types = new HashSet([DataEntityType.findByCode(DISTRICT_HOSPITAL_GROUP), DataEntityType.findByCode(HEALTH_CENTER_GROUP)])
		def dsrTable = dsrService.getDsrTable(location, program, period, types, null)
		
		then:
		dsrTable.getReportValue(DataLocationEntity.findByCode(BUTARO), target).value == "10"		
		dsrTable.getReportValue(DataLocationEntity.findByCode(KIVUYE), target).value == "N/A"
		
	}
	
	def "test dsr with category"(){
		//TODO
	}
	
}
