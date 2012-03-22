package org.chai.kevin.planning

import org.chai.kevin.data.Type;
import org.chai.kevin.form.FormElement;
import org.chai.kevin.form.FormEnteredValue;
import org.chai.kevin.location.DataLocationEntity;
import org.chai.kevin.location.LocationEntity;
import org.chai.kevin.util.JSONUtils;
import org.chai.kevin.value.RawDataElementValue;
import org.chai.kevin.value.Value;


class EditPlanningControllerSpec extends PlanningIntegrationTests {
	
	def planningController
	
	def "save value works"() {
		setup:
		setupLocationTree()
		setupSecurityManager(newSurveyUser('test', 'uuid', DataLocationEntity.findByCode(BUTARO).id))
		def period = newPeriod()
		def dataElement = newRawDataElement(CODE(2), Type.TYPE_LIST(Type.TYPE_MAP(["key0":Type.TYPE_ENUM(CODE(1)), "key1":Type.TYPE_NUMBER()])))
		def planning = newPlanning(period)
		// we create 2 form elements so they don't have the same ID as the type
		def formElement1 = newFormElement(dataElement)
		def formElement2 = newFormElement(dataElement)
		def planningType = newPlanningType(formElement2, "[_].key0", "[_].key1", planning)
		planningController = new EditPlanningController()
		
		when:
		planningController.params.location = DataLocationEntity.findByCode(BUTARO).id
		planningController.params.planningType = planningType.id
		planningController.params.element = formElement2.id
		planningController.params.lineNumber = 0
		planningController.params.suffix = '[0].key1'
		planningController.params['elements['+formElement2.id+'].value[0].key1'] = '123'
		planningController.saveValue()
		def jsonResult = JSONUtils.getMapFromJSON(planningController.response.contentAsString)
		
		then:
		FormEnteredValue.count() == 1
		FormEnteredValue.list()[0].value.listValue[0].mapValue['key1'].numberValue == 123d
		FormEnteredValue.list()[0].value.listValue[0].getAttribute('uuid') != null
		FormEnteredValue.list()[0].value.listValue[0].getAttribute('budget_updated') == 'false'
		jsonResult.id == planningType.id
		jsonResult.lineNumber == 0
		jsonResult.complete == false
		jsonResult.valid == true
		jsonResult.budgetUpdated == false
	}
	
	def "validation works when false"() {
		setup:
		setupLocationTree()
		setupSecurityManager(newSurveyUser('test', 'uuid', DataLocationEntity.findByCode(BUTARO).id))
		def period = newPeriod()
		def dataElement = newRawDataElement(CODE(2), Type.TYPE_LIST(Type.TYPE_MAP(["key0":Type.TYPE_ENUM(CODE(1)), "key1":Type.TYPE_NUMBER()])))
		def planning = newPlanning(period)
		def formElement = newFormElement(dataElement)
		def formValidationRule = newFormValidationRule(formElement, "[_].key1", [DISTRICT_HOSPITAL_GROUP, HEALTH_CENTER_GROUP], "\$"+formElement.id+"[_].key1 < 100", [])
		def planningType = newPlanningType(formElement, "[_].key0", "[_].key1", planning)
		planningController = new EditPlanningController()
		def jsonResult
		
		when:
		planningController.params.location = DataLocationEntity.findByCode(BUTARO).id
		planningController.params.planningType = planningType.id
		planningController.params.element = formElement.id
		planningController.params.lineNumber = 0
		planningController.params.suffix = '[0].key1'
		planningController.params['elements['+formElement.id+'].value[0].key1'] = '123'
		planningController.saveValue()
		jsonResult = JSONUtils.getMapFromJSON(planningController.response.contentAsString)
		
		then:
		jsonResult.valid == false
		jsonResult.elements[0].id == formElement.id
		jsonResult.elements[0].invalid.size() == 1
		jsonResult.elements[0].invalid[0].prefix == '[0].key1'
	}
		
	def "validation works when true"() {
		setup:
		setupLocationTree()
		setupSecurityManager(newSurveyUser('test', 'uuid', DataLocationEntity.findByCode(BUTARO).id))
		def period = newPeriod()
		def dataElement = newRawDataElement(CODE(2), Type.TYPE_LIST(Type.TYPE_MAP(["key0":Type.TYPE_ENUM(CODE(1)), "key1":Type.TYPE_NUMBER()])))
		def planning = newPlanning(period)
		def formElement = newFormElement(dataElement)
		def formValidationRule = newFormValidationRule(formElement, "[_].key1", [DISTRICT_HOSPITAL_GROUP, HEALTH_CENTER_GROUP], "\$"+formElement.id+"[_].key1 < 100", [])
		def planningType = newPlanningType(formElement, "[_].key0", "[_].key1", planning)
		planningController = new EditPlanningController()
		def jsonResult
		
		when:
		planningController.params.location = DataLocationEntity.findByCode(BUTARO).id
		planningController.params.planningType = planningType.id
		planningController.params.element = formElement.id
		planningController.params.lineNumber = 0
		planningController.params.suffix = '[0].key1'
		planningController.params['elements['+formElement.id+'].value[0].key1'] = '99'
		planningController.saveValue()
		jsonResult = JSONUtils.getMapFromJSON(planningController.response.contentAsString)
		
		then:
		jsonResult.valid == true
		jsonResult.elements[0].id == formElement.id
		jsonResult.elements[0].invalid.size() == 0
	}
	
	def "skip works"() {
		setup:
		setupLocationTree()
		setupSecurityManager(newSurveyUser('test', 'uuid', DataLocationEntity.findByCode(BUTARO).id))
		def period = newPeriod()
		def dataElement = newRawDataElement(CODE(2), Type.TYPE_LIST(Type.TYPE_MAP(["key0":Type.TYPE_ENUM(CODE(1)), "key1":Type.TYPE_NUMBER()])))
		def planning = newPlanning(period)
		def formElement = newFormElement(dataElement)
		def skipRule = newFormSkipRule("\$"+formElement.id+"[_].key1 == 1", [(formElement):"[_].key1"])
		def planningType = newPlanningType(formElement, "[_].key0", "[_].key1", planning)
		planningController = new EditPlanningController()
		def jsonResult
		
		when:
		planningController.params.location = DataLocationEntity.findByCode(BUTARO).id
		planningController.params.planningType = planningType.id
		planningController.params.element = formElement.id
		planningController.params.lineNumber = 0
		planningController.params.suffix = '[0].key1'
		planningController.params['elements['+formElement.id+'].value[0].key1'] = '1'
		planningController.saveValue()
		jsonResult = JSONUtils.getMapFromJSON(planningController.response.contentAsString)
		
		then:
		jsonResult.valid == true
		jsonResult.elements[0].id == formElement.id
		jsonResult.elements[0].skipped.size() == 1
		jsonResult.elements[0].skipped[0] == '[0].key1'
	}
	
	def "edit planning entry creates line and sets uuid"() {
		setup:
		setupLocationTree()
		setupSecurityManager(newSurveyUser('test', 'uuid', DataLocationEntity.findByCode(BUTARO).id))
		def period = newPeriod()
		def dataElement = newRawDataElement(CODE(2), Type.TYPE_LIST(Type.TYPE_MAP(["key0":Type.TYPE_ENUM(CODE(1)), "key1":Type.TYPE_NUMBER()])))
		def planning = newPlanning(period)
		// we create 2 form elements so they don't have the same ID as the type
		def formElement1 = newFormElement(dataElement)
		def formElement2 = newFormElement(dataElement)
		def planningType = newPlanningType(formElement2, "[_].key0", "[_].key1", planning)
		planningController = new EditPlanningController()
		
		when:
		planningController.params.location = DataLocationEntity.findByCode(BUTARO).id
		planningController.params.planningType = planningType.id
		planningController.params.lineNumber = 0
		planningController.editPlanningEntry()
		
		then:
		FormEnteredValue.count() == 1
		FormEnteredValue.list()[0].value.listValue[0].getAttribute('uuid') != null
	}
	
	def "accessing index page redirects to proper page - normal user to summary page"() {
		setup:
		setupSecurityManager(newUser('test', 'uuid'))
		planningController = new EditPlanningController()
		
		when:
		planningController.view()
		
		then:
		planningController.response.redirectedUrl == '/editPlanning/summaryPage'
	}

	def "accessing index page redirects to proper page - data entry user to own planning page"() {
		setup:
		setupLocationTree()
		setupSecurityManager(newSurveyUser('test', 'uuid', DataLocationEntity.findByCode(BUTARO).id))
		def period = newPeriod()
		def planning = newPlanning(period, true)
		planningController = new EditPlanningController()
		
		when:
		planningController.view()
		
		then:
		planningController.response.redirectedUrl == '/editPlanning/overview/'+DataLocationEntity.findByCode(BUTARO).id+'?planning='+planning.id
	}
	def "summary page works when no params"() {
		setup:
		planningController = new EditPlanningController()
		
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
		def formElement = newFormElement(dataElement)
		def planningType = newPlanningType(formElement, "[_].key0", "[_].key1", planning)
		planningController = new EditPlanningController()
		
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
		def formElement = newFormElement(dataElement)
		def planningType = newPlanningType(formElement, "[_].key0", "[_].key1", planning)
		def elementValue = newRawDataElementValue(dataElement, period, DataLocationEntity.findByCode(BUTARO),
			Value.VALUE_LIST([Value.VALUE_MAP(["key0":Value.VALUE_STRING("value"), "key1":Value.VALUE_NUMBER(10)])])
		)
		planningController = new EditPlanningController()
		
		when:
		elementValue.value.listValue[0].setAttribute("submitted", "true")
		planningController.params.location = DataLocationEntity.findByCode(BUTARO).id
		planningController.params.planning = planning.id
		planningController.budget()
		
		then:
		planningController.response.redirectedUrl == null
		planningController.modelAndView.model.planningTypeBudgets.find {it.planningList.budgetUpdated} == null
		
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
		def formElement = newFormElement(dataElement)
		def planningType = newPlanningType(formElement, "[_].key0", "[_].key1", planning)
		def value = Value.VALUE_LIST([Value.VALUE_MAP(["key0":Value.VALUE_STRING("value"), "key1":Value.VALUE_NUMBER(10)])])
		value.listValue[0].setAttribute('budget_updated', 'true')
		def elementValue = newRawDataElementValue(dataElement, period, DataLocationEntity.findByCode(BUTARO), value)
		planningController = new EditPlanningController()
		
		when:
		planningController.params.location = DataLocationEntity.findByCode(BUTARO).id
		planningController.params.planning = planning.id
		planningController.budget()
		
		then:
		planningController.response.redirectedUrl == null
		planningController.modelAndView.model.planningLists.find {it.budgetUpdated} != null
		
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
		def formElement = newFormElement(dataElement)
		def planningType = newPlanningType(formElement, "[_].key0", "[_].key1", planning)
		planningController = new EditPlanningController()
		
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
		def formElement = newFormElement(dataElement)
		def planningType = newPlanningType(formElement, "[_].key0", "[_].key1", planning)
		def elementValue = newFormEnteredValue(formElement, period, DataLocationEntity.findByCode(BUTARO),
			Value.VALUE_LIST([Value.VALUE_MAP(["key0":Value.VALUE_STRING("value"), "key1":Value.VALUE_NUMBER(10)])])
		)
		planningController = new EditPlanningController()
		
		when:
		elementValue.value.listValue[0].setAttribute("submitted", "true")
		planningController.params.location = DataLocationEntity.findByCode(BUTARO).id
		planningController.params.planning = planning.id
		planningController.updateBudget()
		
		then:
		planningController.response.redirectedUrl == '/editPlanning/budget/'+DataLocationEntity.findByCode(BUTARO).id+'?planning='+planning.id
		RawDataElementValue.count() == 1
		FormEnteredValue.count() == 1
		RawDataElementValue.list()[0].value.listValue[0].mapValue['key1'].numberValue == 10d
		FormEnteredValue.list()[0].value.listValue[0].getAttribute("budget_updated") == 'true'
		
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
		def formElement = newFormElement(dataElement)
		def planningType = newPlanningType(formElement, "[_].key0", "[_].key1", planning)
		def elementValue = newFormEnteredValue(formElement, period, DataLocationEntity.findByCode(BUTARO),
			Value.VALUE_LIST([Value.VALUE_MAP(["key0":Value.VALUE_STRING("value"), "key1":Value.VALUE_NUMBER(10)])])
		)
		planningController = new EditPlanningController()
		
		when:
		elementValue.value.listValue[0].setAttribute("submitted", "true")
		planningController.params.location = DataLocationEntity.findByCode(BUTARO).id
		planningController.params.planningType = planningType.id
		planningController.updateBudget()
		
		then:
		planningController.response.redirectedUrl == '/editPlanning/budget/'+DataLocationEntity.findByCode(BUTARO).id+'?planning='+planning.id
		RawDataElementValue.count() == 1
		FormEnteredValue.count() == 1
		RawDataElementValue.list()[0].value.listValue[0].mapValue['key1'].numberValue == 10d
		FormEnteredValue.list()[0].value.listValue[0].getAttribute("budget_updated") == 'true'
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
		def formElement = newFormElement(dataElement)
		def planningType = newPlanningType(formElement, "[_].key0", "[_].key1", planning)
		def elementValue = newFormEnteredValue(formElement, period, DataLocationEntity.findByCode(BUTARO),
			Value.VALUE_LIST([Value.VALUE_MAP(["key0":Value.VALUE_STRING("value"), "key1":Value.VALUE_NUMBER(10)])])
		)
		planningController = new EditPlanningController()
		
		when:
		planningController.params.targetURI = '/test'
		planningController.params.location = DataLocationEntity.findByCode(BUTARO).id
		planningController.params.planningType = planningType.id
		planningController.params.lineNumber = 0
		planningController.submit()
		
		then:
		planningController.response.redirectedUrl == '/test'
		RawDataElementValue.count() == 1
		RawDataElementValue.list()[0].value.listValue[0].getAttribute("budget_updated") == null
		RawDataElementValue.list()[0].value.listValue[0].getAttribute("submitted") == null
		FormEnteredValue.list()[0].value.listValue[0].getAttribute("budget_updated") == 'true'
		FormEnteredValue.list()[0].value.listValue[0].getAttribute("submitted") == 'true'
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
		def formElement = newFormElement(dataElement)
		def planningType = newPlanningType(formElement, "[_].key0", "[_].key1", planning)
		def elementValue = newFormEnteredValue(formElement, period, DataLocationEntity.findByCode(BUTARO),
			Value.VALUE_LIST([Value.VALUE_MAP(["key0":Value.VALUE_STRING("value"), "key1":Value.VALUE_NUMBER(10)])])
		)
		planningController = new EditPlanningController()
		
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
		RawDataElementValue.list()[0].value.listValue[0].getAttribute("budget_updated") == null
		RawDataElementValue.list()[0].value.listValue[0].getAttribute("submitted") == null
		FormEnteredValue.list()[0].value.listValue[0].getAttribute("budget_updated") == 'true'
		FormEnteredValue.list()[0].value.listValue[0].getAttribute("submitted") == 'false'
	}
	
}
