package org.chai.kevin.fct

import org.chai.kevin.LocationService
import org.chai.kevin.IntegrationTests
import org.chai.kevin.location.DataEntityType;
import org.chai.kevin.location.LocationEntity;
import org.chai.kevin.location.LocationLevel;

class FctControllerSpec extends FctIntegrationTests {

	def fctController
	
	def "get fct"() {
		setup:
		setupLocationTree()
		def period = newPeriod()
		def program = newReportProgram(CODE(2))
		def sum = newSum("1", CODE(2))
		def target = newFctTarget(CODE(3), sum, [DISTRICT_HOSPITAL_GROUP, HEALTH_CENTER_GROUP], program)
		def targetOption1 = newFctTargetOption(CODE(4), target, sum, 1)
		
		when: "valid table"
		fctController = new FctController()
		fctController.params.period = period.id
		fctController.params.location = LocationEntity.findByCode(RWANDA).id
		fctController.params.program = program.id		
//		fctController.params.level = LocationLevel.findByCode(DISTRICT).id
		fctController.params.fctTarget = target.id
		def model = fctController.view()
		
		then:
		model.currentPeriod.equals(period)
		model.currentLocation.equals(LocationEntity.findByCode(RWANDA))
		model.currentProgram.equals(program)
//		model.currentLevel.equals(LocationLevel.findByCode(DISTRICT))
		model.currentTarget.equals(target)
		model.fctTable != null		
		model.fctTable.valueMap.isEmpty() == false
		model.fctTable.hasData() == true
	}
		
	def "get fct with no program and no location, default to root program and root location"() {
		setup:
		setupLocationTree()
		def period = newPeriod()
		def program = newReportProgram(CODE(2))
		def sum = newSum("1", CODE(2))
		def target = newFctTarget(CODE(3), sum, [DISTRICT_HOSPITAL_GROUP, HEALTH_CENTER_GROUP], program)
		def targetOption1 = newFctTargetOption(CODE(4), target, sum, 1)
		
		when: "no program"
		fctController = new FctController()
		fctController.params.period = period.id
//		fctController.params.level = 3
		fctController.params.fctTarget = target.id
		def model = fctController.view()
		
		then:
		model.fctTable != null		
		model.fctTable.valueMap.isEmpty() == false
		model.fctTable.hasData() == true
		
	}
	
	def "get fct with no targets"() {
		setup:
		setupLocationTree()
		def period = newPeriod()
		def program = newReportProgram(CODE(2))
		
		when:
		fctController = new FctController()
		fctController.params.period = period.id
		fctController.params.location = LocationEntity.findByCode(BURERA).id
		fctController.params.program = program.id
		def model = fctController.view()
		
		then:
		model.fctTable == null		
	}
	
	def "get fct with target with no target options"() {
		setup:
		setupLocationTree()
		def period = newPeriod()
		def program = newReportProgram(CODE(2))
		def sum = newSum("1", CODE(2))
		def target = newFctTarget(CODE(3), sum, [DISTRICT_HOSPITAL_GROUP, HEALTH_CENTER_GROUP], program)
		
		when:
		fctController = new FctController()
		fctController.params.period = period.id
		fctController.params.location = LocationEntity.findByCode(BURERA).id
		fctController.params.program = program.id
		def model = fctController.view()
		
		then:
		model.fctTable == null
	}
	
	def "get fct with invalid program parameter"() {
		setup:
		setupLocationTree()
		def period = newPeriod()
		def program = newReportProgram(CODE(2))
		def sum = newSum("1", CODE(2))
		def target = newFctTarget(CODE(3), sum, [DISTRICT_HOSPITAL_GROUP, HEALTH_CENTER_GROUP], program)
		def targetOption1 = newFctTargetOption(CODE(4), target, sum, 1)
		
		when:
		fctController = new FctController()
		fctController.params.program = program.id+1
		def model = fctController.view()
		
		then:
		model.currentProgram.id == program.id
		model.fctTable != null
		model.fctTable.hasData() == true
	}
}

