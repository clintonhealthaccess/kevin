package org.chai.kevin.fct

import org.chai.kevin.LocationService
import org.chai.kevin.IntegrationTests
import org.chai.kevin.location.DataLocationType;
import org.chai.kevin.location.Location;
import org.chai.kevin.location.LocationLevel;

class FctControllerSpec extends FctIntegrationTests {

	def fctController
	
	def "get fct"() {
		setup:
		setupLocationTree()
		def period = newPeriod()
		def program = newReportProgram(CODE(2))
		def target = newFctTarget(CODE(3), 1, [DISTRICT_HOSPITAL_GROUP, HEALTH_CENTER_GROUP], program)
		def sum = newSum("1", CODE(2))
		def targetOption1 = newFctTargetOption(CODE(4), 1, target, sum)
		
		when: "valid table"
		fctController = new FctController()
		fctController.params.period = period.id
		fctController.params.program = program.id
		fctController.params.location = Location.findByCode(RWANDA).id
		fctController.params.dataLocationTypes = [DataLocationType.findByCode(HEALTH_CENTER_GROUP).id, DataLocationType.findByCode(DISTRICT_HOSPITAL_GROUP).id]
		fctController.params.fctTarget = target.id
		def model = fctController.view()
		
		then:
		model.currentPeriod.equals(period)		
		model.currentProgram.equals(program)
		model.currentLocation.equals(Location.findByCode(RWANDA))		
		model.currentLocationTypes.equals(s([DataLocationType.findByCode(HEALTH_CENTER_GROUP), DataLocationType.findByCode(DISTRICT_HOSPITAL_GROUP)]))
		model.currentChildLevel.equals(LocationLevel.findByCode(PROVINCE))
		model.currentTarget.equals(target)
		model.fctTable != null
		model.fctTable.hasData() == true
	}
	
	def "get fct with no parameters, default to period, root program, root location, location types, and target"(){
		setup:
		setupLocationTree()
		def period = newPeriod()
		def program = newReportProgram(CODE(2))
		def target = newFctTarget(CODE(3), 1, [DISTRICT_HOSPITAL_GROUP, HEALTH_CENTER_GROUP], program)
		def sum = newSum("1", CODE(2))
		def targetOption1 = newFctTargetOption(CODE(4), 1, target, sum)
		
		when: "no parameters"
		fctController = new FctController()
		def model = fctController.view()
		
		then:
		model.currentPeriod.equals(period)
		model.currentProgram.equals(program)
		model.currentLocation.equals(Location.findByCode(RWANDA))		
		model.currentLocationTypes.equals(s([DataLocationType.findByCode(HEALTH_CENTER_GROUP), DataLocationType.findByCode(DISTRICT_HOSPITAL_GROUP)]))
		model.currentChildLevel.equals(LocationLevel.findByCode(PROVINCE))
		model.currentTarget.equals(target)
		model.fctTable != null
		model.fctTable.hasData() == true
	}
		
	def "get fct with with invalid parameters, default to period, root program, root location, location types, and target"(){
		setup:
		setupLocationTree()
		def period = newPeriod()
		def program = newReportProgram(CODE(2))
		def target = newFctTarget(CODE(3), 1, [DISTRICT_HOSPITAL_GROUP, HEALTH_CENTER_GROUP], program)
		def sum = newSum("1", CODE(2))
		def targetOption1 = newFctTargetOption(CODE(4), 1, target, sum)
		
		when: "invalid parameters"
		fctController = new FctController()
		fctController.params.period = -1
		fctController.params.program = -1
		fctController.params.location = -1
		fctController.params.dataLocationTypes = [-1, -2]
		fctController.params.fctTarget = -1
		def model = fctController.view()
		
		then:
		fctController.response.redirectedUrl.contains("/fct/view/")
		fctController.response.redirectedUrl.contains(period.id+"/"+program.id+"/"+Location.findByCode(RWANDA).id+"?")
		fctController.response.redirectedUrl.contains("fctTarget="+target.id)
		fctController.response.redirectedUrl.contains("dataLocationTypes="+DataLocationType.findByCode(HEALTH_CENTER_GROUP).id)
		fctController.response.redirectedUrl.contains("dataLocationTypes="+DataLocationType.findByCode(DISTRICT_HOSPITAL_GROUP).id)
	}	
	
	def "get fct with with invalid parameters, redirect with correct parameter"(){
		setup:
		setupLocationTree()
		def period = newPeriod()
		def program = newReportProgram(CODE(2))
		def target = newFctTarget(CODE(3), 1, [DISTRICT_HOSPITAL_GROUP, HEALTH_CENTER_GROUP], program)
		def sum = newSum("1", CODE(2))
		def targetOption1 = newFctTargetOption(CODE(4), 1, target, sum)
		
		when: "valid location parameter"
		fctController = new FctController()
		fctController.params.period = -1
		fctController.params.program = -1
		fctController.params.location = Location.findByCode(BURERA).id
		fctController.params.dataLocationTypes = [-1, -2]
		fctController.params.fctTarget = -1
		def model = fctController.view()
		
		then:
		fctController.response.redirectedUrl.contains("/fct/view/")
		fctController.response.redirectedUrl.contains(period.id+"/"+program.id+"/"+Location.findByCode(BURERA).id+"?")
		fctController.response.redirectedUrl.contains("fctTarget="+target.id)
		fctController.response.redirectedUrl.contains("dataLocationTypes="+DataLocationType.findByCode(HEALTH_CENTER_GROUP).id)
		fctController.response.redirectedUrl.contains("dataLocationTypes="+DataLocationType.findByCode(DISTRICT_HOSPITAL_GROUP).id)
	}
	
	def "get fct with no targets"() {
		setup:
		setupLocationTree()
		def period = newPeriod()
		def program = newReportProgram(CODE(2))
		
		when:
		fctController = new FctController()
		fctController.params.period = period.id
		fctController.params.program = program.id
		fctController.params.location = Location.findByCode(BURERA).id		
		fctController.params.dataLocationTypes = [DataLocationType.findByCode(HEALTH_CENTER_GROUP).id, DataLocationType.findByCode(DISTRICT_HOSPITAL_GROUP).id]
		def model = fctController.view()
		
		then:
		model.fctTable != null
		model.fctTable.hasData() == false		
	}
	
	def "get fct with target with no target options"() {
		setup:
		setupLocationTree()
		def period = newPeriod()
		def program = newReportProgram(CODE(2))
		def sum = newSum("1", CODE(2))
		def target = newFctTarget(CODE(3), 1, [DISTRICT_HOSPITAL_GROUP, HEALTH_CENTER_GROUP], program)
		
		when:
		fctController = new FctController()
		fctController.params.period = period.id
		fctController.params.program = program.id
		fctController.params.location = Location.findByCode(BURERA).id
		fctController.params.dataLocationTypes = [DataLocationType.findByCode(HEALTH_CENTER_GROUP).id, DataLocationType.findByCode(DISTRICT_HOSPITAL_GROUP).id]
		fctController.params.fctTarget = target.id
		def model = fctController.view()
		
		then:
		model.fctTable != null
		model.fctTable.hasData() == false
	}
	
	def "get fct with skipped level parameter"() {
		setup:
		setupLocationTree()
		def period = newPeriod()
		def program = newReportProgram(CODE(2))
		def sum = newSum("1", CODE(2))
		def target = newFctTarget(CODE(3), 1, [DISTRICT_HOSPITAL_GROUP, HEALTH_CENTER_GROUP], program)
		def targetOption1 = newFctTargetOption(CODE(4), 1, target, sum)
		
		when:
		fctController = new FctController()
		fctController.params.location = Location.findByCode(BURERA).id
		def model = fctController.view()
		
		then:
		model.currentLocation.equals(Location.findByCode(BURERA))
		model.currentChildLevel.equals(null)
		model.fctTable != null
		model.fctTable.hasData() == true
	}
}

