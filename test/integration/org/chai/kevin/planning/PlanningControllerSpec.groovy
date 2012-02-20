package org.chai.kevin.planning

import org.chai.kevin.data.Type;
import org.chai.kevin.location.DataLocationEntity;
import org.chai.kevin.location.LocationEntity;
import org.chai.kevin.value.RawDataElementValue;
import org.chai.kevin.value.Value;


class PlanningControllerSpec extends PlanningIntegrationTests {
	
	def planningController
	
	def "accessing index page redirects to proper page - normal user to summary page"() {
		setup:
		setupSecurityManager(newUser('test', 'uuid'))
		planningController = new PlanningController()
		
		when:
		planningController.view()
		
		then:
		planningController.response.redirectedUrl == '/planning/summaryPage'
	}

	def "accessing index page redirects to proper page - data entry user to own planning page"() {
		setup:
		setupLocationTree()
		setupSecurityManager(newSurveyUser('test', 'uuid', DataLocationEntity.findByCode(BUTARO).id))
		def period = newPeriod()
		def planning = newPlanning(period, true)
		planningController = new PlanningController()
		
		when:
		planningController.view()
		
		then:
		planningController.response.redirectedUrl == '/planning/overview/'+DataLocationEntity.findByCode(BUTARO).id+'?planning='+planning.id
	}
	def "summary page works when no params"() {
		setup:
		planningController = new PlanningController()
		
		when:
		planningController.summaryPage()
		
		then:
		planningController.modelAndView.model.currentLocation == null
		planningController.modelAndView.model.currentPlanning == null
		planningController.modelAndView.model.summaryPage == null
	}
	
	def "summary page works with params"() {
		setup:
		setupLocationTree()
		def period = newPeriod()
		def dataElement = newRawDataElement(CODE(2),
			Type.TYPE_LIST(Type.TYPE_MAP(["key0":Type.TYPE_ENUM(CODE(1)), "key1":Type.TYPE_NUMBER()])))
		def planning = newPlanning(period)
		def planningType = newPlanningType(dataElement, "[_].key0", planning)
		planningController = new PlanningController()
		
		when:
		planningController.params.location = LocationEntity.findByCode(RWANDA).id
		planningController.params.planning = planning.id
		planningController.summaryPage() 
		
		then:
		planningController.modelAndView.model.currentLocation.equals(LocationEntity.findByCode(RWANDA))
		planningController.modelAndView.model.currentPlanning.equals(planning)
		planningController.modelAndView.model.summaryPage != null
		
	}
	
	def "access budget page when budget is not updated does not redirect"() {
		setup:
		setup:
		setupLocationTree()
		def period = newPeriod()
		def enume = newEnume(CODE(1))
		newEnumOption(enume, "value")
		def dataElement = newRawDataElement(CODE(2),
			Type.TYPE_LIST(Type.TYPE_MAP(["key0":Type.TYPE_ENUM(CODE(1)), "key1":Type.TYPE_NUMBER()])))
		def planning = newPlanning(period)
		def planningType = newPlanningType(dataElement, "[_].key0", planning)
		def elementValue = newRawDataElementValue(dataElement, period, DataLocationEntity.findByCode(BUTARO),
			Value.VALUE_LIST([Value.VALUE_MAP(["key0":Value.VALUE_STRING("value"), "key1":Value.VALUE_NUMBER(10)])])
		)
		planningController = new PlanningController()
		
		when:
		elementValue.value.listValue[0].setAttribute("submitted", "true")
		planningController.params.location = DataLocationEntity.findByCode(BUTARO).id
		planningController.params.planning = planning.id
		planningController.budget()
		
		then:
		planningController.response.redirectedUrl == null
		planningController.modelAndView.model.updatedBudget == false
		
	}
	
	def "access budget when budget updated displays budget without redirect"() {
		setup:
		setup:
		setupLocationTree()
		def period = newPeriod()
		def enume = newEnume(CODE(1))
		newEnumOption(enume, "value")
		def dataElement = newRawDataElement(CODE(2),
			Type.TYPE_LIST(Type.TYPE_MAP(["key0":Type.TYPE_ENUM(CODE(1)), "key1":Type.TYPE_NUMBER()])))
		def planning = newPlanning(period)
		def planningType = newPlanningType(dataElement, "[_].key0", planning)
		def value = Value.VALUE_LIST([Value.VALUE_MAP(["key0":Value.VALUE_STRING("value"), "key1":Value.VALUE_NUMBER(10)])])
		value.listValue[0].setAttribute('budget_updated', 'true')
		def elementValue = newRawDataElementValue(dataElement, period, DataLocationEntity.findByCode(BUTARO), value)
		planningController = new PlanningController()
		
		when:
		planningController.params.location = DataLocationEntity.findByCode(BUTARO).id
		planningController.params.planning = planning.id
		planningController.budget()
		
		then:
		planningController.response.redirectedUrl == null
		planningController.modelAndView.model.updatedBudget == true
		
	}
	
	def "access budget page when no data displays budget without redirect"() {
		setup:
		setup:
		setupLocationTree()
		def period = newPeriod()
		def enume = newEnume(CODE(1))
		newEnumOption(enume, "value")
		def dataElement = newRawDataElement(CODE(2),
			Type.TYPE_LIST(Type.TYPE_MAP(["key0":Type.TYPE_ENUM(CODE(1)), "key1":Type.TYPE_NUMBER()])))
		def planning = newPlanning(period)
		def planningType = newPlanningType(dataElement, "[_].key0", planning)
		planningController = new PlanningController()
		
		when:
		planningController.params.location = DataLocationEntity.findByCode(BUTARO).id
		planningController.params.planning = planning.id
		planningController.budget()
		
		then:
		planningController.response.redirectedUrl == null
		planningController.modelAndView.model.planning.equals (planning)
		planningController.modelAndView.model.location.equals (DataLocationEntity.findByCode(BUTARO))
		
	}
	
	def "update budget sets budget updated to true and redirects to budget - with planning"() {
		setup:
		setupLocationTree()
		def period = newPeriod()
		def enume = newEnume(CODE(1))
		newEnumOption(enume, "value")
		def dataElement = newRawDataElement(CODE(2),
			Type.TYPE_LIST(Type.TYPE_MAP(["key0":Type.TYPE_ENUM(CODE(1)), "key1":Type.TYPE_NUMBER()])))
		def planning = newPlanning(period)
		def planningType = newPlanningType(dataElement, "[_].key0", planning)
		def elementValue = newRawDataElementValue(dataElement, period, DataLocationEntity.findByCode(BUTARO),
			Value.VALUE_LIST([Value.VALUE_MAP(["key0":Value.VALUE_STRING("value"), "key1":Value.VALUE_NUMBER(10)])])
		)
		planningController = new PlanningController()
		
		when:
		elementValue.value.listValue[0].setAttribute("submitted", "true")
		planningController.params.location = DataLocationEntity.findByCode(BUTARO).id
		planningController.params.planning = planning.id
		planningController.updateBudget()
		
		then:
		planningController.response.redirectedUrl == '/planning/budget/'+DataLocationEntity.findByCode(BUTARO).id+'?planning='+planning.id
		RawDataElementValue.count() == 1
		RawDataElementValue.list()[0].value.listValue[0].getAttribute("budget_updated") == 'true'
		
	}
	
	def "udpate budget sets budget updated to true and redirects to budget - with planning type"() {
		setup:
		setupLocationTree()
		def period = newPeriod()
		def enume = newEnume(CODE(1))
		newEnumOption(enume, "value")
		def dataElement = newRawDataElement(CODE(2),
			Type.TYPE_LIST(Type.TYPE_MAP(["key0":Type.TYPE_ENUM(CODE(1)), "key1":Type.TYPE_NUMBER()])))
		def planning = newPlanning(period)
		def planningType = newPlanningType(dataElement, "[_].key0", planning)
		def elementValue = newRawDataElementValue(dataElement, period, DataLocationEntity.findByCode(BUTARO),
			Value.VALUE_LIST([Value.VALUE_MAP(["key0":Value.VALUE_STRING("value"), "key1":Value.VALUE_NUMBER(10)])])
		)
		planningController = new PlanningController()
		
		when:
		elementValue.value.listValue[0].setAttribute("submitted", "true")
		planningController.params.location = DataLocationEntity.findByCode(BUTARO).id
		planningController.params.planningType = planningType.id
		planningController.updateBudget()
		
		then:
		planningController.response.redirectedUrl == '/planning/budget/'+DataLocationEntity.findByCode(BUTARO).id+'?planning='+planning.id
		RawDataElementValue.count() == 1
		RawDataElementValue.list()[0].value.listValue[0].getAttribute("budget_updated") == 'true'
	}
	
	def "submit sets submitted flag to true and redirects to targetURI"() {
		setup:
		setupLocationTree()
		def period = newPeriod()
		def enume = newEnume(CODE(1))
		newEnumOption(enume, "value")
		def dataElement = newRawDataElement(CODE(2),
			Type.TYPE_LIST(Type.TYPE_MAP(["key0":Type.TYPE_ENUM(CODE(1)), "key1":Type.TYPE_NUMBER()])))
		def planning = newPlanning(period)
		def planningType = newPlanningType(dataElement, "[_].key0", planning)
		def elementValue = newRawDataElementValue(dataElement, period, DataLocationEntity.findByCode(BUTARO),
			Value.VALUE_LIST([Value.VALUE_MAP(["key0":Value.VALUE_STRING("value"), "key1":Value.VALUE_NUMBER(10)])])
		)
		planningController = new PlanningController()
		
		when:
		planningController.params.targetURI = '/test'
		planningController.params.location = DataLocationEntity.findByCode(BUTARO).id
		planningController.params.planningType = planningType.id
		planningController.params.lineNumber = 0
		planningController.submit()
		
		then:
		planningController.response.redirectedUrl == '/test'
		RawDataElementValue.count() == 1
		RawDataElementValue.list()[0].value.listValue[0].getAttribute("budget_updated") == 'true'
		RawDataElementValue.list()[0].value.listValue[0].getAttribute("submitted") == 'true'
	}
	
	
	def "unsubmit sets submitted flag to false and redirects to targetURI"() {
		setup:
		setupLocationTree()
		def period = newPeriod()
		def enume = newEnume(CODE(1))
		newEnumOption(enume, "value")
		def dataElement = newRawDataElement(CODE(2),
			Type.TYPE_LIST(Type.TYPE_MAP(["key0":Type.TYPE_ENUM(CODE(1)), "key1":Type.TYPE_NUMBER()])))
		def planning = newPlanning(period)
		def planningType = newPlanningType(dataElement, "[_].key0", planning)
		def elementValue = newRawDataElementValue(dataElement, period, DataLocationEntity.findByCode(BUTARO),
			Value.VALUE_LIST([Value.VALUE_MAP(["key0":Value.VALUE_STRING("value"), "key1":Value.VALUE_NUMBER(10)])])
		)
		planningController = new PlanningController()
		
		when:
		elementValue.value.listValue[0].setAttribute("submitted", "true")
		elementValue.value.listValue[0].setAttribute("budget_updated", "true")
		planningController.params.targetURI = '/test'
		planningController.params.location = DataLocationEntity.findByCode(BUTARO).id
		planningController.params.planningType = planningType.id
		planningController.params.lineNumber = 0
		planningController.unsubmit()
		
		then:
		planningController.response.redirectedUrl == '/test'
		RawDataElementValue.count() == 1
		RawDataElementValue.list()[0].value.listValue[0].getAttribute("budget_updated") == 'true'
		RawDataElementValue.list()[0].value.listValue[0].getAttribute("submitted") == 'false'
	}
	
}
