package org.chai.kevin.dsr

import org.chai.kevin.data.Type
import org.chai.kevin.location.DataEntity;
import org.chai.kevin.location.DataEntityType;
import org.chai.kevin.location.LocationEntity;

class DsrServiceSpec extends DsrIntegrationTests {

	def dsrService
	
	def "test normal dsr service"() {
		setup:
		setupLocationTree()
		def period = newPeriod()
		def objective = newReportObjective(CODE(1))
		def dataElement = newRawDataElement(CODE(3), Type.TYPE_NUMBER())
		def target = newDsrTarget(CODE(2), dataElement, [], objective)
		def organisation = LocationEntity.findByCode(BURERA)
		def dsrTable = null
		
		when:
		dsrTable = dsrService.getDsrTable(organisation, objective, period, new HashSet([DataEntityType.findByCode(DISTRICT_HOSPITAL_GROUP), DataEntityType.findByCode(HEALTH_CENTER_GROUP)]))
		
		then:
		dsrTable.getReportValue(DataEntity.findByCode(BUTARO), target) != null
		dsrTable.getOrganisationMap().get(LocationEntity.findByCode(BURERA)).equals([DataEntity.findByCode(BUTARO), DataEntity.findByCode(KIVUYE)])

		when:
		dsrTable = dsrService.getDsrTable(organisation, objective, period, new HashSet([DataEntityType.findByCode(DISTRICT_HOSPITAL_GROUP)]))
		
		then:
		dsrTable.getOrganisationMap().get(LocationEntity.findByCode(BURERA)).equals([DataEntity.findByCode(BUTARO)])

	}
	
	def "test dsr with non-existing enum option"() {
		setup:
		setupLocationTree()
		def period = newPeriod()
		def objective = newReportObjective(CODE(1))
		def enume = newEnume("enum")
		def dataElement = newRawDataElement(CODE(3), Type.TYPE_ENUM("enum"))
		def target = newDsrTarget(CODE(2), dataElement, [DISTRICT_HOSPITAL_GROUP, HEALTH_CENTER_GROUP], objective)
		def dsrTable = null
		
		when:
		newRawDataElementValue(dataElement, period, DataEntity.findByCode(BUTARO), v("\"option\""))
		dsrTable = dsrService.getDsrTable(LocationEntity.findByCode(BURERA), objective, period, new HashSet([DataEntityType.findByCode(DISTRICT_HOSPITAL_GROUP), DataEntityType.findByCode(HEALTH_CENTER_GROUP)]))
		
		then:
		dsrTable.getReportValue(DataEntity.findByCode(BUTARO), target).value == "option"
	}
	
	def "test dsr formatting"() {
		when:
		setupLocationTree()
		def period = newPeriod()
		def normalizedDataElement = newNormalizedDataElement(CODE(1), Type.TYPE_NUMBER(), e([(period.id+''):[(DISTRICT_HOSPITAL_GROUP):"10",(HEALTH_CENTER_GROUP):"10"]]))
		def objective = newReportObjective(CODE(2))
		def target = newDsrTarget(CODE(3), normalizedDataElement, format, [DISTRICT_HOSPITAL_GROUP, HEALTH_CENTER_GROUP], objective)
		refreshNormalizedDataElement()
		def organisation = LocationEntity.findByCode(BURERA)
		def dsrTable = dsrService.getDsrTable(organisation, objective, period, new HashSet([DataEntityType.findByCode(DISTRICT_HOSPITAL_GROUP), DataEntityType.findByCode(HEALTH_CENTER_GROUP)]))
		
		then:
		dsrTable.getReportValue(DataEntity.findByCode(BUTARO), target).value == value
		
		where:
		format	| value
		"#"		| "10"
		""		| "10"
		"#.0"	| "10.0"
		
	}

	def "test dsr with no groups should return no value"() {
		when:
		setupLocationTree()
		def period = newPeriod()
		def normalizedDataElement = newNormalizedDataElement(CODE(1), Type.TYPE_NUMBER(), e([(period.id+''):[(DISTRICT_HOSPITAL_GROUP):"10",(HEALTH_CENTER_GROUP):"10"]]))
		def objective = newReportObjective(CODE(2))
		def target = newDsrTarget(CODE(3), normalizedDataElement, [], objective)
		refreshNormalizedDataElement()
		def organisation = LocationEntity.findByCode(BURERA)
		def dsrTable = dsrService.getDsrTable(organisation, objective, period, new HashSet([DataEntityType.findByCode(DISTRICT_HOSPITAL_GROUP), DataEntityType.findByCode(HEALTH_CENTER_GROUP)]))
		
		then:
		dsrTable.getReportValue(DataEntity.findByCode(organisationName), target).value == null
		
		where:
		organisationName << [BUTARO, KIVUYE]
		
	}
	
	def "test dsr with groups"() {
		when:
		setupLocationTree()
		def period = newPeriod()
		def normalizedDataElement = newNormalizedDataElement(CODE(1), Type.TYPE_NUMBER(), e([(period.id+''):[(DISTRICT_HOSPITAL_GROUP):"10",(HEALTH_CENTER_GROUP):"10"]]))
		def objective = newReportObjective(CODE(2))
		def target = newDsrTarget(CODE(3), normalizedDataElement, [DISTRICT_HOSPITAL_GROUP], objective)
		refreshNormalizedDataElement()
		def organisation = LocationEntity.findByCode(BURERA)
		def dsrTable = dsrService.getDsrTable(organisation, objective, period, new HashSet([DataEntityType.findByCode(DISTRICT_HOSPITAL_GROUP), DataEntityType.findByCode(HEALTH_CENTER_GROUP)]))
		
		then:
		dsrTable.getReportValue(DataEntity.findByCode(BUTARO), target).value == "10"		
		dsrTable.getReportValue(DataEntity.findByCode(KIVUYE), target).value == null
		
	}
	
}
