package org.chai.kevin.fct

import org.chai.kevin.Period;
import org.chai.kevin.data.Type;
import org.chai.kevin.location.DataLocationType;
import org.chai.kevin.location.Location;
import org.chai.kevin.location.LocationLevel;

class FctServiceSpec extends FctIntegrationTests { 

	def fctService
	def locationService
	
	def "test normal fct service"() {
		setup:
		setupLocationTree()
		def period = newPeriod()
		def normalizedDataElement = newNormalizedDataElement(CODE(1), Type.TYPE_NUMBER(), e([(period.id+''):[(DISTRICT_HOSPITAL_GROUP):"1", (HEALTH_CENTER_GROUP):"1"]]))
		def program = newReportProgram(CODE(2))
		def sum = newSum("\$"+normalizedDataElement.id, CODE(2))
		def target = newFctTarget(CODE(3), [DISTRICT_HOSPITAL_GROUP, HEALTH_CENTER_GROUP], program)
		def targetOption = newFctTargetOption(CODE(4), target, sum, 1)
		def location = Location.findByCode(RWANDA)
		def level = locationService.getLevelAfter(location.getLevel(), new HashSet([LocationLevel.findByCode(SECTOR)]))
		def dataLocationTypes = new HashSet([DataLocationType.findByCode(DISTRICT_HOSPITAL_GROUP), DataLocationType.findByCode(HEALTH_CENTER_GROUP)])
		def fctTable = null
		refresh()
		
		when:
		fctTable = fctService.getFctTable(location, program, target, period, level, dataLocationTypes)
		
		then:
		fctTable.getReportValue(Location.findByCode(NORTH), targetOption).value == "2.0"
		
		when:
		dataLocationTypes = new HashSet([DataLocationType.findByCode(DISTRICT_HOSPITAL_GROUP)])
		fctTable = fctService.getFctTable(location, program, target, period, level, dataLocationTypes)
		
		then:
		fctTable.getReportValue(Location.findByCode(NORTH), targetOption).value == "1.0"
	}
		
	def "test normal fct service with dummy location"() {
		setup:
		setupLocationTree()
		def period = newPeriod()
		def normalizedDataElement = newNormalizedDataElement(CODE(1), Type.TYPE_NUMBER(), e([(period.id+''):[(DISTRICT_HOSPITAL_GROUP):"1", (HEALTH_CENTER_GROUP):"1"]]))
		def program = newReportProgram(CODE(2))
		def sum = newSum("\$"+normalizedDataElement.id, CODE(2))
		def target = newFctTarget(CODE(3), [DISTRICT_HOSPITAL_GROUP, HEALTH_CENTER_GROUP], program)
		def targetOption = newFctTargetOption(CODE(4), target, sum, 1)
		def location = Location.findByCode(RWANDA)
		def level = locationService.getLevelAfter(location.getLevel(), new HashSet([LocationLevel.findByCode(SECTOR)]))
		def dataLocationTypes = new HashSet([DataLocationType.findByCode(DISTRICT_HOSPITAL_GROUP), DataLocationType.findByCode(HEALTH_CENTER_GROUP)])
		def fctTable = null
		
		when:
		def dummy = newLocation("dummy", location, level)
		refresh()
		fctTable = fctService.getFctTable(location, program, target, period, level, dataLocationTypes)
		
		then:
		fctTable.getReportValue(Location.findByCode("dummy"), targetOption) == null
		fctTable.getReportValue(Location.findByCode(NORTH), targetOption).value == "2.0"
				
	}
	
}
