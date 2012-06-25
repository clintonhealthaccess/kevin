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
		dsrController.params.dataLocationTypes = [DataLocationType.findByCode(HEALTH_CENTER_GROUP).id, DataLocationType.findByCode(DISTRICT_HOSPITAL_GROUP).id]
		dsrController.params.dsrCategory = category.id
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
	
	def "get dsr with category belonging to several programs only gets target belonging to the specified category"() {
		setup:
		setupLocationTree()
		def period = newPeriod()
		def program1 = newReportProgram(ROOT)
		def program2 = newReportProgram(PROGRAM1, program1)
		def category = newDsrTargetCategory(CATEGORY1, 0)
		def dataElement = newRawDataElement(CODE(3), Type.TYPE_NUMBER())
		def target1 = newDsrTarget(CODE(4), 1, dataElement, program1, category)
		def target2 = newDsrTarget(CODE(5), 1, dataElement, program2, category)
	
		when: "valid table"
		dsrController = new DsrController()
		dsrController.params.period = period.id
		dsrController.params.program = program1.id
		dsrController.params.location = Location.findByCode(RWANDA).id
		dsrController.params.dataLocationTypes = [DataLocationType.findByCode(HEALTH_CENTER_GROUP).id, DataLocationType.findByCode(DISTRICT_HOSPITAL_GROUP).id]
		dsrController.params.dsrCategory = category.id
		def model = dsrController.view()
		
		then:
		model.currentPeriod.equals(period)
		model.currentProgram.equals(program1)
		model.currentLocation.equals(Location.findByCode(RWANDA))
		model.currentLocationTypes.equals(s([DataLocationType.findByCode(HEALTH_CENTER_GROUP), DataLocationType.findByCode(DISTRICT_HOSPITAL_GROUP)]))
		model.currentCategory.equals(category)
		model.dsrTable != null
		model.dsrTable.targets == [target1]
		model.dsrTable.valueMap.isEmpty() == false
		model.dsrTable.hasData() == true
	}
	
	def "get dsr with no category"() {
		setup:
		setupLocationTree()
		def period = newPeriod()
		def program = newReportProgram(ROOT)
		def dataElement = newRawDataElement(CODE(3), Type.TYPE_NUMBER())
		def category = newDsrTargetCategory(CATEGORY1, 0)
		def target = newDsrTarget(CODE(4), 1, dataElement, program, category)
		
		when: "valid table"
		dsrController = new DsrController()
		dsrController.params.period = period.id
		dsrController.params.program = program.id
		dsrController.params.location = Location.findByCode(RWANDA).id
		dsrController.params.dataLocationTypes = [DataLocationType.findByCode(HEALTH_CENTER_GROUP).id, DataLocationType.findByCode(DISTRICT_HOSPITAL_GROUP).id]
		def model = dsrController.view()
		
		then:
		dsrController.response.redirectedUrl.contains("/dsr/view/")
		dsrController.response.redirectedUrl.contains(period.id+"/"+program.id+"/"+Location.findByCode(RWANDA).id+"/"+category.id)
		dsrController.response.redirectedUrl.contains("dataLocationTypes="+DataLocationType.findByCode(HEALTH_CENTER_GROUP).id)
		dsrController.response.redirectedUrl.contains("dataLocationTypes="+DataLocationType.findByCode(DISTRICT_HOSPITAL_GROUP).id)
	}

	def "get dsr with no targets"() {
		setup:
		setupLocationTree()
		def period = newPeriod()
		def program = newReportProgram(ROOT)
		def category = newDsrTargetCategory(CATEGORY1, 0)
		
		when:
		dsrController = new DsrController()
		dsrController.params.period = period.id
		dsrController.params.program = program.id
		dsrController.params.location = Location.findByCode(BURERA).id
		dsrController.params.dataLocationTypes = [DataLocationType.findByCode(HEALTH_CENTER_GROUP).id, DataLocationType.findByCode(DISTRICT_HOSPITAL_GROUP).id]
		dsrController.params.dsrCategory = category.id
		def model = dsrController.view()
		
		then:
		model.currentPeriod.id == period.id
		model.currentProgram.id == program.id
		model.currentLocation.id == Location.findByCode(BURERA).id
		model.currentLocationTypes.equals(s([DataLocationType.findByCode(HEALTH_CENTER_GROUP), DataLocationType.findByCode(DISTRICT_HOSPITAL_GROUP)]))
		model.currentCategory == category
		model.dsrTable != null
		model.dsrTable.hasData() == false
	}
			
	def "get dsr with no parameters redirects to period, program, location and category"() {
		setup:
		setupLocationTree()
		def period = newPeriod()
		def program = newReportProgram(ROOT)
		def category = newDsrTargetCategory(CATEGORY1, 0)
		def dataElement = newRawDataElement(CODE(3), Type.TYPE_NUMBER())
		def target = newDsrTarget(CODE(4), 1, dataElement, program, category)
		
		when: "no parameters"
		dsrController = new DsrController()
		def model = dsrController.view()
		
		then:
		dsrController.response.redirectedUrl.contains("/dsr/view/")
		dsrController.response.redirectedUrl.contains(period.id+"/"+program.id+"/"+Location.findByCode(RWANDA).id+"/"+category.id)
		dsrController.response.redirectedUrl.contains("dataLocationTypes="+DataLocationType.findByCode(HEALTH_CENTER_GROUP).id)
		dsrController.response.redirectedUrl.contains("dataLocationTypes="+DataLocationType.findByCode(DISTRICT_HOSPITAL_GROUP).id)
	}
	
	def "get dsr with no parameters redirects to period, program and location"() {
		setup:
		setupLocationTree()
		def period = newPeriod()
		def program = newReportProgram(ROOT)
		def dataElement = newRawDataElement(CODE(3), Type.TYPE_NUMBER())
		
		when: "no parameters"
		dsrController = new DsrController()
		def model = dsrController.view()
		
		then:
		dsrController.response.redirectedUrl.contains("/dsr/view/")
		dsrController.response.redirectedUrl.contains(period.id+"/"+program.id+"/"+Location.findByCode(RWANDA).id)
		dsrController.response.redirectedUrl.contains("dataLocationTypes="+DataLocationType.findByCode(HEALTH_CENTER_GROUP).id)
		dsrController.response.redirectedUrl.contains("dataLocationTypes="+DataLocationType.findByCode(DISTRICT_HOSPITAL_GROUP).id)
	}
	
	def "get dsr with invalid parameters, redirect to default period, root program, root location, location types, category, and target"() {
		setup:
		setupLocationTree()
		def period = newPeriod()
		def program = newReportProgram(ROOT)
		def category = newDsrTargetCategory(CATEGORY1, 0)
		def dataElement = newRawDataElement(CODE(3), Type.TYPE_NUMBER())
		def target = newDsrTarget(CODE(4), 1, dataElement, program, category)
		
		when: "invalid parameters"
		dsrController = new DsrController()
		dsrController.params.period = -1
		dsrController.params.program = -1
		dsrController.params.location = -1
		dsrController.params.dsrCategory = -1
		dsrController.params.dataLocationTypes = [-1, -2]
		def model = dsrController.view()
		
		then:
		dsrController.response.redirectedUrl.contains("/dsr/view/")
		dsrController.response.redirectedUrl.contains(period.id+"/"+program.id+"/"+Location.findByCode(RWANDA).id+"/"+category.id)
		dsrController.response.redirectedUrl.contains("dataLocationTypes="+DataLocationType.findByCode(HEALTH_CENTER_GROUP).id)
		dsrController.response.redirectedUrl.contains("dataLocationTypes="+DataLocationType.findByCode(DISTRICT_HOSPITAL_GROUP).id)
	}
	
	def "get dsr with invalid parameters, redirect with correct parameter"() {
		setup:
		setupLocationTree()
		def period = newPeriod()
		def program = newReportProgram(ROOT)
		def category = newDsrTargetCategory(CATEGORY1, 0)
		def dataElement = newRawDataElement(CODE(3), Type.TYPE_NUMBER())
		def target = newDsrTarget(CODE(4), 1, dataElement, program, category)
		
		when: "valid location parameter"
		dsrController = new DsrController()
		dsrController.params.period = -1
		dsrController.params.program = -1
		dsrController.params.location = Location.findByCode(BURERA).id
		dsrController.params.dsrCategory = -1
		dsrController.params.dataLocationTypes = [-1, -2]
		def model = dsrController.view()
		
		then:
		dsrController.response.redirectedUrl.contains("/dsr/view/")
		dsrController.response.redirectedUrl.contains(period.id+"/"+program.id+"/"+Location.findByCode(BURERA).id+"/"+category.id)
		dsrController.response.redirectedUrl.contains("dataLocationTypes="+DataLocationType.findByCode(HEALTH_CENTER_GROUP).id)
		dsrController.response.redirectedUrl.contains("dataLocationTypes="+DataLocationType.findByCode(DISTRICT_HOSPITAL_GROUP).id)
	}
	
	def "get dsr for only district hospitals"() {
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
		dsrController.params.dataLocationTypes = [DataLocationType.findByCode(DISTRICT_HOSPITAL_GROUP).id]
		dsrController.params.dsrCategory = category.id
		def model = dsrController.view()
		
		then:
		model.currentPeriod.equals(period)
		model.currentProgram.equals(program)
		model.currentLocation.equals(Location.findByCode(RWANDA))
		model.currentLocationTypes.equals(s([DataLocationType.findByCode(DISTRICT_HOSPITAL_GROUP)]))
		model.currentCategory == category
		model.dsrTable != null
		model.dsrTable.hasData() == true
		model.dsrTable.targets.equals([target])
		model.dsrTable.targetCategories.equals([category])
	}
	
}