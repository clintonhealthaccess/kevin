package org.chai.kevin.fct

import org.chai.kevin.data.Type;
import org.chai.kevin.location.DataEntityType;
import org.chai.kevin.location.LocationEntity;
import org.chai.kevin.location.LocationLevel;
import org.hisp.dhis.period.Period;

class FctServiceSpec extends FctIntegrationTests { 

	def fctService
	
	def "test normal fct service"() {
		setup:
		setupLocationTree()
		def period = newPeriod()
		def normalizedDataElement = newNormalizedDataElement(CODE(1), Type.TYPE_NUMBER(), e([(period.id+''):[(DISTRICT_HOSPITAL_GROUP):"1", (HEALTH_CENTER_GROUP):"1"]]))
		def program = newReportProgram(CODE(2))
		def sum = newSum("\$"+normalizedDataElement.id, CODE(2))
		def target = newFctTarget(CODE(3), sum, [DISTRICT_HOSPITAL_GROUP, HEALTH_CENTER_GROUP], program)
		def fctTable = null
		refresh()
		
		when:
		fctTable = fctService.getFctTable(LocationEntity.findByCode(RWANDA), program, period, LocationLevel.findByCode(DISTRICT), new HashSet([DataEntityType.findByCode(DISTRICT_HOSPITAL_GROUP), DataEntityType.findByCode(HEALTH_CENTER_GROUP)]))
		
		then:
//		fctTable.getLocationMap().get(LocationEntity.findByCode(NORTH)).equals([LocationEntity.findByCode(BURERA)])
		fctTable.getReportValue(LocationEntity.findByCode(BURERA), target).value == "2.0"
		fctTable.getTotalValue(target).value == "2.0"
		
		when:
		fctTable = fctService.getFctTable(LocationEntity.findByCode(RWANDA), program, period, LocationLevel.findByCode(DISTRICT), new HashSet([DataEntityType.findByCode(DISTRICT_HOSPITAL_GROUP)]))
		
		then:
//		fctTable.getLocationMap().get(LocationEntity.findByCode(NORTH)).equals([LocationEntity.findByCode(BURERA)])
		fctTable.getReportValue(LocationEntity.findByCode(BURERA), target).value == "1.0"
		fctTable.getTotalValue(target).value == "1.0"
		
//		when:
//		fctTable = fctService.getFctTable(LocationEntity.findByCode(BURERA), program, period, LocationLevel.findByCode(COUNTRY), new HashSet([DataEntityType.findByCode(DISTRICT_HOSPITAL_GROUP), DataEntityType.findByCode(HEALTH_CENTER_GROUP)]))
		
//		then:
//		fctTable.locations.isEmpty()
	}
		
	def "test normal fct service with dummy location"() {
		setup:
		setupLocationTree()
		def period = newPeriod()
		def normalizedDataElement = newNormalizedDataElement(CODE(1), Type.TYPE_NUMBER(), e([(period.id+''):[(DISTRICT_HOSPITAL_GROUP):"1", (HEALTH_CENTER_GROUP):"1"]]))
		def program = newReportProgram(CODE(2))
		def sum = newSum("\$"+normalizedDataElement.id, CODE(2))
		def target = newFctTarget(CODE(3), sum, [DISTRICT_HOSPITAL_GROUP, HEALTH_CENTER_GROUP], program)
		def fctTable = null
		
		when:
		def dummy = newLocationEntity("dummy", LocationEntity.findByCode(NORTH), LocationLevel.findByCode(DISTRICT))
		refresh()
		fctTable = fctService.getFctTable(LocationEntity.findByCode(RWANDA), program, period, LocationLevel.findByCode(DISTRICT), new HashSet([DataEntityType.findByCode(DISTRICT_HOSPITAL_GROUP), DataEntityType.findByCode(HEALTH_CENTER_GROUP)]))
		
		then:
//		fctTable.getLocationMap().get(LocationEntity.findByCode(NORTH)).equals([LocationEntity.findByCode(BURERA), LocationEntity.findByCode("dummy")])
		fctTable.getTotalValue(target).value == "2.0"
		fctTable.getReportValue(LocationEntity.findByCode("dummy"), target).value == "0.0"
		fctTable.getReportValue(LocationEntity.findByCode(BURERA), target).value == "2.0"
				
	}
	
}
