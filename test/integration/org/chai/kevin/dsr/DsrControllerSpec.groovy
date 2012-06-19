package org.chai.kevin.dsr

import org.chai.kevin.data.Type
import org.chai.kevin.location.DataLocationType
import org.chai.kevin.location.Location
import org.chai.kevin.reports.ReportProgram

class DsrControllerSpec extends DsrIntegrationTests {

	def dsrController
	
	def "get dsr"() {
		setup:
		setupLocationTree()
		def period = newPeriod()
		def program = newReportProgram(ROOT)
		def category = newDsrTargetCategory(CATEGORY1, 0)
		def dataElement = newRawDataElement(CODE(3), Type.TYPE_NUMBER())
		def target = newDsrTarget(CODE(4), 1, dataElement, program, category)
		
		when: "valid table"
		dsrController = new DsrController()
		dsrController.params.period = period.id
		dsrController.params.program = program.id
		dsrController.params.location = Location.findByCode(RWANDA).id
		def model = dsrController.view()
		
		then:
		model.currentPeriod.equals(period)
		model.currentProgram.equals(program)
		model.currentLocation.equals(Location.findByCode(RWANDA))
		model.currentLocationTypes.equals(s([DataLocationType.findByCode(HEALTH_CENTER_GROUP), DataLocationType.findByCode(DISTRICT_HOSPITAL_GROUP)]))
		model.currentCategory.equals(category)
		model.dsrTable != null		
		model.dsrTable.valueMap.isEmpty() == false
		model.dsrTable.hasData() == true
	}
	
	def "get dsr with no category"() {
		setup:
		setupLocationTree()
		def period = newPeriod()
		def program = newReportProgram(ROOT)
		def dataElement = newRawDataElement(CODE(3), Type.TYPE_NUMBER())
		def target = newDsrTarget(CODE(4), 1, dataElement, program)
		
		when: "valid table"
		dsrController = new DsrController()
		dsrController.params.period = period.id
		dsrController.params.program = program.id
		dsrController.params.location = Location.findByCode(RWANDA).id
		def model = dsrController.view()
		
		then:
		model.currentPeriod.equals(period)
		model.currentProgram.equals(program)
		model.currentLocation.equals(Location.findByCode(RWANDA))
		model.currentLocationTypes.equals(s([DataLocationType.findByCode(HEALTH_CENTER_GROUP), DataLocationType.findByCode(DISTRICT_HOSPITAL_GROUP)]))
		model.currentCategory == null
		model.dsrTable != null
		model.dsrTable.valueMap.isEmpty() == false
		model.dsrTable.hasData() == true
		model.dsrTable.targets.equals([target])
		model.dsrTable.targetCategories.equals([])
	}

	def "get dsr with no targets"() {
		setup:
		setupLocationTree()
		def period = newPeriod()
		def program = newReportProgram(ROOT)
		
		when:
		dsrController = new DsrController()
		dsrController.params.period = period.id
		dsrController.params.program = program.id
		dsrController.params.location = Location.findByCode(BURERA).id
		def model = dsrController.view()
		
		then:
		model.currentPeriod.id == period.id
		model.currentProgram.id == program.id
		model.currentLocation.id == Location.findByCode(BURERA).id
		model.currentLocationTypes.equals(s([DataLocationType.findByCode(HEALTH_CENTER_GROUP), DataLocationType.findByCode(DISTRICT_HOSPITAL_GROUP)]))
		model.currentCategory == null
		model.dsrTable != null
		model.dsrTable.hasData() == false
	}
			
	def "get dsr with no parameters, default to period, root program, root location, location types, category, and target"() {
		setup:
		setupLocationTree()
		def period = newPeriod()
		def program = newReportProgram(ROOT)
		def category = newDsrTargetCategory(CATEGORY1, 0)
		def dataElement = newRawDataElement(CODE(3), Type.TYPE_NUMBER())
		def target = newDsrTarget(CODE(4), 1, dataElement, program, category)
		
		when: "no program and no location"
		dsrController = new DsrController()
		dsrController.params.period = period.id
		def model = dsrController.view()
		
		then:
		model.currentPeriod.equals(period)
		model.currentProgram.equals(ReportProgram.findByCode(ROOT))
		model.currentLocation.equals(Location.findByCode(RWANDA))
		model.currentLocationTypes.equals(s([DataLocationType.findByCode(HEALTH_CENTER_GROUP), DataLocationType.findByCode(DISTRICT_HOSPITAL_GROUP)]))
		model.currentCategory.equals(DsrTargetCategory.findByCode(CATEGORY1))
		model.dsrTable != null
		model.dsrTable.valueMap.isEmpty() == false
		model.dsrTable.hasData() == true
		model.dsrTable.targets.equals([target])
		model.dsrTable.targetCategories.equals([category])
		
	}	
	
	def "get dsr with invalid parameters, default to period, root program, root location, location types, category, and target"() {
		setup:
		setupLocationTree()
		def period = newPeriod()
		def program = newReportProgram(ROOT)
		def category = newDsrTargetCategory(CATEGORY1, 0)
		def dataElement = newRawDataElement(CODE(3), Type.TYPE_NUMBER())
		def target = newDsrTarget(CODE(4), 1, dataElement, program, category)
		
		when:
		dsrController = new DsrController()
		dsrController.params.period = period.id+1
		dsrController.params.program = program.id+1
		dsrController.params.location = -1
		dsrController.params.dsrCategory = category.id+1
		def model = dsrController.view()
		
		then:
		model.currentPeriod.id == period.id
		model.currentProgram.id == program.id
		model.currentLocation.id == Location.findByCode(RWANDA).id
		model.currentLocationTypes.equals(s([DataLocationType.findByCode(HEALTH_CENTER_GROUP), DataLocationType.findByCode(DISTRICT_HOSPITAL_GROUP)]))
		model.currentCategory.id == category.id
		model.dsrTable != null
		model.dsrTable.hasData() == true
		model.dsrTable.targets.equals([target])
		model.dsrTable.targetCategories.equals([category])
	}
	
	def "get dsr with invalid parameters, redirect to valid ones"(){
		//TODO
	}
	
	def "get dsr for only district hospitals"() {
		setup:
		setupLocationTree()
		def period = newPeriod()
		def program = newReportProgram(ROOT)
		def dataElement = newRawDataElement(CODE(3), Type.TYPE_NUMBER())
		def target = newDsrTarget(CODE(4), 1, dataElement, program)
		
		when: "valid table"
		dsrController = new DsrController()
		dsrController.params.period = period.id
		dsrController.params.program = program.id
		dsrController.params.location = Location.findByCode(RWANDA).id
		dsrController.params.dataLocationTypes = [DataLocationType.findByCode(DISTRICT_HOSPITAL_GROUP).id]
		def model = dsrController.view()
		
		then:
		model.currentPeriod.equals(period)
		model.currentProgram.equals(program)
		model.currentLocation.equals(Location.findByCode(RWANDA))
		model.currentLocationTypes.equals(s([DataLocationType.findByCode(DISTRICT_HOSPITAL_GROUP)]))
		model.currentCategory == null
		model.dsrTable != null
		model.dsrTable.valueMap.isEmpty() == false
		model.dsrTable.hasData() == true
		model.dsrTable.targets.equals([target])
		model.dsrTable.targetCategories.equals([])
	}
	
}