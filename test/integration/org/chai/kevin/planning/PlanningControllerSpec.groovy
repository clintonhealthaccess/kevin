package org.chai.kevin.planning

import org.chai.kevin.data.Type;
import org.chai.kevin.location.DataLocationEntity;
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
	
	def "access budget page when budget is not updated redirects to update budget"() {
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
		planningController.params.location = DataLocationEntity.findByCode(BUTARO).id
		planningController.params.planning = planning.id
		planningController.budget()
		
		then:
		planningController.response.redirectedUrl == '/planning/updateBudget/'+DataLocationEntity.findByCode(BUTARO).id+'?planning='+planning.id
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
	
	def "update budget sets budget updated to true and redirects to budget page"() {
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
		planningController.params.location = DataLocationEntity.findByCode(BUTARO).id
		planningController.params.planning = planning.id
		planningController.updateBudget()
		
		then:
		planningController.response.redirectedUrl == '/planning/budget/'+DataLocationEntity.findByCode(BUTARO).id+'?planning='+planning.id
		RawDataElementValue.count() == 1
		RawDataElementValue.list()[0].value.listValue[0].getAttribute("budget_updated") == 'true'
		
	}
	
}
