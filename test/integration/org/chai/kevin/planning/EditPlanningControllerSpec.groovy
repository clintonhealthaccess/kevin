package org.chai.kevin.planning

import org.chai.kevin.data.Type;
import org.chai.kevin.form.FormElement;
import org.chai.kevin.form.FormEnteredValue;
import org.chai.kevin.location.DataLocation;
import org.chai.kevin.location.Location;
import org.chai.kevin.planning.PlanningCost.PlanningCostType;
import org.chai.kevin.util.JSONUtils;
import org.chai.kevin.value.RawDataElementValue;
import org.chai.kevin.value.Value;


class EditPlanningControllerSpec extends PlanningIntegrationTests {
	
	def planningController
	
	def "save value works"() {
		setup:
		setupLocationTree()
		setupSecurityManager(newSurveyUser('test', 'uuid', DataLocation.findByCode(BUTARO).id))
		def period = newPeriod()
		def dataElement = newRawDataElement(CODE(2), Type.TYPE_LIST(Type.TYPE_MAP(["key0":Type.TYPE_ENUM(CODE(1)), "key1":Type.TYPE_NUMBER()])))
		def planning = newPlanning(period, [DISTRICT_HOSPITAL_GROUP, HEALTH_CENTER_GROUP])
		// we create 2 form elements so they don't have the same ID as the type
		def formElement1 = newFormElement(dataElement)
		def formElement2 = newFormElement(dataElement)
		def planningType = newPlanningType(formElement2, "[_].key1", planning)
		planningController = new EditPlanningController()
		
		when:
		planningController.params.location = DataLocation.findByCode(BUTARO).id
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
		FormEnteredValue.list()[0].value.getAttribute('submitted') == "false"
		jsonResult.id == planningType.id
		jsonResult.lineNumber == 0
		jsonResult.complete == false
		jsonResult.valid == true
	}
	
	def "validation works when false"() {
		setup:
		setupLocationTree()
		setupSecurityManager(newSurveyUser('test', 'uuid', DataLocation.findByCode(BUTARO).id))
		def period = newPeriod()
		def dataElement = newRawDataElement(CODE(2), Type.TYPE_LIST(Type.TYPE_MAP(["key0":Type.TYPE_ENUM(CODE(1)), "key1":Type.TYPE_NUMBER()])))
		def planning = newPlanning(period, [DISTRICT_HOSPITAL_GROUP, HEALTH_CENTER_GROUP])
		def formElement = newFormElement(dataElement)
		def formValidationRule = newFormValidationRule(CODE(1), formElement, "[_].key1", [DISTRICT_HOSPITAL_GROUP, HEALTH_CENTER_GROUP], "\$"+formElement.id+"[_].key1 < 100", [])
		def planningType = newPlanningType(formElement, "[_].key1", planning)
		planningController = new EditPlanningController()
		def jsonResult
		
		when:
		planningController.params.location = DataLocation.findByCode(BUTARO).id
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
		setupSecurityManager(newSurveyUser('test', 'uuid', DataLocation.findByCode(BUTARO).id))
		def period = newPeriod()
		def dataElement = newRawDataElement(CODE(2), Type.TYPE_LIST(Type.TYPE_MAP(["key0":Type.TYPE_ENUM(CODE(1)), "key1":Type.TYPE_NUMBER()])))
		def planning = newPlanning(period, [DISTRICT_HOSPITAL_GROUP, HEALTH_CENTER_GROUP])
		def formElement = newFormElement(dataElement)
		def formValidationRule = newFormValidationRule(CODE(1), formElement, "[_].key1", [DISTRICT_HOSPITAL_GROUP, HEALTH_CENTER_GROUP], "\$"+formElement.id+"[_].key1 < 100", [])
		def planningType = newPlanningType(formElement, "[_].key1", planning)
		planningController = new EditPlanningController()
		def jsonResult
		
		when:
		planningController.params.location = DataLocation.findByCode(BUTARO).id
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
	
	def "validation works with outliers"() {
		setup:
		setupLocationTree()
		setupSecurityManager(newSurveyUser('test', 'uuid', DataLocation.findByCode(BUTARO).id))
		def period = newPeriod()
		def dataElement = newRawDataElement(CODE(2), Type.TYPE_LIST(Type.TYPE_MAP(["key0":Type.TYPE_ENUM(CODE(1)), "key1":Type.TYPE_NUMBER()])))
		def planning = newPlanning(period, [DISTRICT_HOSPITAL_GROUP, HEALTH_CENTER_GROUP])
		def formElement = newFormElement(dataElement)
		def formValidationRule = newFormValidationRule(CODE(1), formElement, "[_].key1", [DISTRICT_HOSPITAL_GROUP, HEALTH_CENTER_GROUP], "\$"+formElement.id+"[_].key1 < 100", true, [])
		def planningType = newPlanningType(formElement, "[_].key1", planning)
		planningController = new EditPlanningController()
		def jsonResult
		
		when:
		planningController.params.location = DataLocation.findByCode(BUTARO).id
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
		
	
	def "skip works"() {
		setup:
		setupLocationTree()
		setupSecurityManager(newSurveyUser('test', 'uuid', DataLocation.findByCode(BUTARO).id))
		def period = newPeriod()
		def dataElement = newRawDataElement(CODE(2), Type.TYPE_LIST(Type.TYPE_MAP(["key0":Type.TYPE_ENUM(CODE(1)), "key1":Type.TYPE_NUMBER()])))
		def planning = newPlanning(period, [DISTRICT_HOSPITAL_GROUP, HEALTH_CENTER_GROUP])
		def formElement = newFormElement(dataElement)
		def skipRule = newFormSkipRule(CODE(1), "\$"+formElement.id+"[_].key1 == 1", [(formElement):"[_].key1"])
		def planningType = newPlanningType(formElement, "[_].key1", planning)
		planningController = new EditPlanningController()
		def jsonResult
		
		when:
		planningController.params.location = DataLocation.findByCode(BUTARO).id
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
	
	def "edit planning entry creates line"() {
		setup:
		setupLocationTree()
		setupSecurityManager(newSurveyUser('test', 'uuid', DataLocation.findByCode(BUTARO).id))
		def period = newPeriod()
		def dataElement = newRawDataElement(CODE(2), Type.TYPE_LIST(Type.TYPE_MAP(["key0":Type.TYPE_ENUM(CODE(1)), "key1":Type.TYPE_NUMBER()])))
		def planning = newPlanning(period, [DISTRICT_HOSPITAL_GROUP, HEALTH_CENTER_GROUP])
		// we create 2 form elements so they don't have the same ID as the type
		def formElement1 = newFormElement(dataElement)
		def formElement2 = newFormElement(dataElement)
		def planningType = newPlanningType(formElement2, "[_].key1", planning)
		planningController = new EditPlanningController()
		
		when:
		planningController.params.location = DataLocation.findByCode(BUTARO).id
		planningController.params.planningType = planningType.id
		planningController.params.lineNumber = 0
		planningController.editPlanningEntry()
		
		then:
		FormEnteredValue.count() == 1
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
		setupSecurityManager(newSurveyUser('test', 'uuid', DataLocation.findByCode(BUTARO).id))
		def period = newPeriod()
		def planning = newPlanning(period, [DISTRICT_HOSPITAL_GROUP, HEALTH_CENTER_GROUP], true)
		planningController = new EditPlanningController()
		
		when:
		planningController.view()
		
		then:
		planningController.response.redirectedUrl == '/editPlanning/overview/'+DataLocation.findByCode(BUTARO).id+'?planning='+planning.id
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
		def planning = newPlanning(period, [DISTRICT_HOSPITAL_GROUP, HEALTH_CENTER_GROUP])
		def formElement = newFormElement(dataElement)
		def planningType = newPlanningType(formElement, "[_].key1", planning)
		planningController = new EditPlanningController()
		
		when:
		planningController.params.location = Location.findByCode(RWANDA).id
		planningController.params.planning = planning.id
		planningController.summaryPage() 
		
		then:
		planningController.modelAndView.model.currentLocation.equals(Location.findByCode(RWANDA))
		planningController.modelAndView.model.currentPlanning.equals(planning)
		planningController.modelAndView.model.summaryPage != null
		
	}
	
	def "budget table with null values"() {
		setup:
		setupLocationTree()
		def period = newPeriod()
		def dataElement = newRawDataElement(CODE(2),
			Type.TYPE_LIST(Type.TYPE_MAP(["key0":Type.TYPE_ENUM(CODE(1)), "key1":Type.TYPE_NUMBER()])))
		def planning = newPlanning(period, [DISTRICT_HOSPITAL_GROUP, HEALTH_CENTER_GROUP])
		def formElement = newFormElement(dataElement)
		def planningType = newPlanningType(j(["en": "Planning Type"]), formElement, "[_].key0", planning, null)
		def normalizedDataElement = newNormalizedDataElement(CODE(3), Type.TYPE_NUMBER(), e([:]))
		def planningCost = newPlanningCost(j(["en": "Planning Cost"]), PlanningCostType.INCOMING, normalizedDataElement, planningType)
		def elementValue = newFormEnteredValue(formElement, period, DataLocation.findByCode(BUTARO),
			Value.VALUE_LIST([Value.VALUE_MAP(["key0":Value.VALUE_STRING("value"), "key1":Value.VALUE_NUMBER(10)])])
		)
		planningController = new EditPlanningController()
		
		when:
		planningController.params.location = DataLocation.findByCode(BUTARO).id
		planningController.params.planning = planning.id
		planningController.budget()
		def budgetTable = planningController.modelAndView.model.budgetTable
		
		then:
		budgetTable != null
		budgetTable.tableLine.lines.size() == 1
		budgetTable.tableLine.lines[0].displayName == "Planning Type"
		budgetTable.tableLine.lines[0].lines[0].displayName == "value"
		budgetTable.tableLine.lines[0].lines[0].lines[0].displayName == "Planning Cost"
		
	}
	
	def "budget table with no fixed header"() {
		setup:
		setupLocationTree()
		def period = newPeriod()
		def dataElement = newRawDataElement(CODE(2),
			Type.TYPE_LIST(Type.TYPE_MAP(["key0":Type.TYPE_ENUM(CODE(1)), "key1":Type.TYPE_NUMBER()])))
		def planning = newPlanning(period, [DISTRICT_HOSPITAL_GROUP, HEALTH_CENTER_GROUP])
		def formElement = newFormElement(dataElement)
		def planningType = newPlanningType(j(["en": "Planning Type"]), formElement, null, planning, null)
		def normalizedDataElement = newNormalizedDataElement(CODE(3), Type.TYPE_NUMBER(), e([:]))
		def planningCost = newPlanningCost(j(["en": "Planning Cost"]), PlanningCostType.INCOMING, normalizedDataElement, planningType)
		def elementValue = newFormEnteredValue(formElement, period, DataLocation.findByCode(BUTARO),
			Value.VALUE_LIST([Value.VALUE_MAP(["key0":Value.VALUE_STRING("value"), "key1":Value.VALUE_NUMBER(10)])])
		)
		planningController = new EditPlanningController()
		
		when:
		planningController.params.location = DataLocation.findByCode(BUTARO).id
		planningController.params.planning = planning.id
		planningController.budget()
		def budgetTable = planningController.modelAndView.model.budgetTable
		
		then:
		budgetTable != null
		budgetTable.tableLine.lines.size() == 1
		budgetTable.tableLine.lines[0].displayName == "Planning Type"
		budgetTable.tableLine.lines[0].lines[0].displayName == "Planning Type 1"
		budgetTable.tableLine.lines[0].lines[0].lines[0].displayName == "Planning Cost"
	}
	
	
	def "budget table with max number 1"() {
		setup:
		setupLocationTree()
		def period = newPeriod()
		def dataElement = newRawDataElement(CODE(2),
			Type.TYPE_LIST(Type.TYPE_MAP(["key0":Type.TYPE_ENUM(CODE(1)), "key1":Type.TYPE_NUMBER()])))
		def planning = newPlanning(period, [DISTRICT_HOSPITAL_GROUP, HEALTH_CENTER_GROUP])
		def formElement = newFormElement(dataElement)
		def planningType = newPlanningType(j(["en": "Planning Type"]), formElement, "[_].key0", planning, 1)
		def normalizedDataElement = newNormalizedDataElement(CODE(3), Type.TYPE_NUMBER(), e([:]))
		def planningCost = newPlanningCost(j(["en": "Planning Cost"]), PlanningCostType.INCOMING, normalizedDataElement, planningType)
		def elementValue = newFormEnteredValue(formElement, period, DataLocation.findByCode(BUTARO),
			Value.VALUE_LIST([Value.VALUE_MAP(["key0":Value.VALUE_STRING("value"), "key1":Value.VALUE_NUMBER(10)])])
		)
		planningController = new EditPlanningController()
		
		when:
		planningController.params.location = DataLocation.findByCode(BUTARO).id
		planningController.params.planning = planning.id
		planningController.budget()
		def budgetTable = planningController.modelAndView.model.budgetTable
		
		then:
		budgetTable != null
		budgetTable.tableLine.lines.size() == 1
		budgetTable.tableLine.lines[0].displayName == "Planning Type"
		budgetTable.tableLine.lines[0].lines[0].displayName == "Planning Cost"
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
		def planning = newPlanning(period, [DISTRICT_HOSPITAL_GROUP, HEALTH_CENTER_GROUP])
		def formElement = newFormElement(dataElement)
		def planningType = newPlanningType(formElement, "[_].key1", planning)
		def elementValue = newRawDataElementValue(dataElement, period, DataLocation.findByCode(BUTARO),
			Value.VALUE_LIST([Value.VALUE_MAP(["key0":Value.VALUE_STRING("value"), "key1":Value.VALUE_NUMBER(10)])])
		)
		planningController = new EditPlanningController()
		
		when:
		planningController.params.location = DataLocation.findByCode(BUTARO).id
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
		def planning = newPlanning(period, [DISTRICT_HOSPITAL_GROUP, HEALTH_CENTER_GROUP])
		def formElement = newFormElement(dataElement)
		def planningType = newPlanningType(formElement, "[_].key1", planning)
		def value = Value.VALUE_LIST([Value.VALUE_MAP(["key0":Value.VALUE_STRING("value"), "key1":Value.VALUE_NUMBER(10)])])
		value.listValue[0].setAttribute('budget_updated', 'true')
		def elementValue = newRawDataElementValue(dataElement, period, DataLocation.findByCode(BUTARO), value)
		planningController = new EditPlanningController()
		
		when:
		planningController.params.location = DataLocation.findByCode(BUTARO).id
		planningController.params.planning = planning.id
		planningController.budget()
		
		then:
		planningController.response.redirectedUrl == null
		
	}
	
	def "access budget page when no data displays budget without redirect"() {
		setup:
		setupLocationTree()
		def period = newPeriod()
		def enume = newEnume(CODE(1))
		newEnumOption(enume, "value")
		def dataElement = newRawDataElement(CODE(2),
			Type.TYPE_LIST(Type.TYPE_MAP(["key0":Type.TYPE_ENUM(CODE(1)), "key1":Type.TYPE_NUMBER()])))
		def planning = newPlanning(period, [DISTRICT_HOSPITAL_GROUP, HEALTH_CENTER_GROUP])
		def formElement = newFormElement(dataElement)
		def planningType = newPlanningType(formElement, "[_].key1", planning)
		planningController = new EditPlanningController()
		
		when:
		planningController.params.location = DataLocation.findByCode(BUTARO).id
		planningController.params.planning = planning.id
		planningController.budget()
		
		then:
		planningController.response.redirectedUrl == null
		planningController.modelAndView.model.planning.equals (planning)
		planningController.modelAndView.model.location.equals (DataLocation.findByCode(BUTARO))
		
	}
	
	def "overview with active planning"() {
		setup:
		setupLocationTree()
		def period = newPeriod()
		def enume = newEnume(CODE(1))
		newEnumOption(enume, "value")
		def dataElement = newRawDataElement(CODE(2),
			Type.TYPE_LIST(Type.TYPE_MAP(["key0":Type.TYPE_ENUM(CODE(1)), "key1":Type.TYPE_NUMBER()])))
		def planning = newPlanning(period, [DISTRICT_HOSPITAL_GROUP])
		planningController = new EditPlanningController()
		
		when:
		planningController.params.location = DataLocation.findByCode(BUTARO).id
		planningController.params.planning = planning.id
		planningController.overview()
		
		then:
		planningController.modelAndView.model.planning.equals(planning)
		planningController.modelAndView.model.location.equals(DataLocation.findByCode(BUTARO))
	}
	
	def "overview with non active planning for type returns 404"() {
		setup:
		setupLocationTree()
		def period = newPeriod()
		def enume = newEnume(CODE(1))
		newEnumOption(enume, "value")
		def dataElement = newRawDataElement(CODE(2),
			Type.TYPE_LIST(Type.TYPE_MAP(["key0":Type.TYPE_ENUM(CODE(1)), "key1":Type.TYPE_NUMBER()])))
		def planning = newPlanning(period, [DISTRICT_HOSPITAL_GROUP])
		planningController = new EditPlanningController()
		
		when:
		planningController.params.location = DataLocation.findByCode(KIVUYE).id
		planningController.params.planning = planning.id
		planningController.overview()
		
		then:
		planningController.modelAndView == null
	}
	
	def "planning list with non active planning for type returns 404"() {
		setup:
		setupLocationTree()
		def period = newPeriod()
		def enume = newEnume(CODE(1))
		newEnumOption(enume, "value")
		def dataElement = newRawDataElement(CODE(2),
			Type.TYPE_LIST(Type.TYPE_MAP(["key0":Type.TYPE_ENUM(CODE(1)), "key1":Type.TYPE_NUMBER()])))
		def planning = newPlanning(period, [DISTRICT_HOSPITAL_GROUP])
		def formElement = newFormElement(dataElement)
		def planningType = newPlanningType(formElement, "[_].key1", planning)
		planningController = new EditPlanningController()
		
		when:
		planningController.params.location = DataLocation.findByCode(KIVUYE).id
		planningController.params.planningType = planningType.id
		planningController.planningList()
		
		then:
		planningController.modelAndView == null
	}
	
	def "budget with non active planning for type returns 404"() {
		setup:
		setupLocationTree()
		def period = newPeriod()
		def enume = newEnume(CODE(1))
		newEnumOption(enume, "value")
		def dataElement = newRawDataElement(CODE(2),
			Type.TYPE_LIST(Type.TYPE_MAP(["key0":Type.TYPE_ENUM(CODE(1)), "key1":Type.TYPE_NUMBER()])))
		def planning = newPlanning(period, [DISTRICT_HOSPITAL_GROUP])
		planningController = new EditPlanningController()
		
		when:
		planningController.params.location = DataLocation.findByCode(KIVUYE).id
		planningController.params.planning = planning.id
		planningController.budget()
		
		then:
		planningController.modelAndView == null
	}
	
	def "summary page only shows active location types"() {
		setup:
		setupLocationTree()
		def period = newPeriod()
		def enume = newEnume(CODE(1))
		newEnumOption(enume, "value")
		def dataElement = newRawDataElement(CODE(2),
			Type.TYPE_LIST(Type.TYPE_MAP(["key0":Type.TYPE_ENUM(CODE(1)), "key1":Type.TYPE_NUMBER()])))
		def planning = newPlanning(period, [DISTRICT_HOSPITAL_GROUP])
		planningController = new EditPlanningController()
		
		when:
		planningController.params.location = Location.findByCode(RWANDA).id
		planningController.params.planning = planning.id
		planningController.summaryPage()
		
		then:
		planningController.modelAndView.model.summaryPage.dataLocations.equals([DataLocation.findByCode(BUTARO)])
	}
	
	def "save when survey invalid stays on same page and does not submit"() {
		setup:
		setupLocationTree()
		setupSecurityManager(newSurveyUser('test', 'uuid', DataLocation.findByCode(BUTARO).id))
		def period = newPeriod()
		def dataElement = newRawDataElement(CODE(2), Type.TYPE_LIST(Type.TYPE_MAP(["key0":Type.TYPE_ENUM(CODE(1)), "key1":Type.TYPE_NUMBER()])))
		def planning = newPlanning(period, [DISTRICT_HOSPITAL_GROUP, HEALTH_CENTER_GROUP])
		def formElement = newFormElement(dataElement)
		def formValidationRule = newFormValidationRule(CODE(1), formElement, "[_].key1", [DISTRICT_HOSPITAL_GROUP, HEALTH_CENTER_GROUP], "\$"+formElement.id+"[_].key1 < 100", [])
		def planningType = newPlanningType(formElement, "[_].key1", planning)
		planningController = new EditPlanningController()
		
		when:
		planningController.params.location = DataLocation.findByCode(BUTARO).id
		planningController.params.planningType = planningType.id
		planningController.params.element = formElement.id
		planningController.params.lineNumber = 0
		planningController.params.suffix = '[0].key1'
		planningController.params['elements['+formElement.id+'].value[0].key1'] = '123'
		planningController.save()
		
		then:
		planningController.modelAndView.model.planningType.equals(planningType)
		FormEnteredValue.count() == 1
		FormEnteredValue.list()[0].value.getAttribute('submitted') == "false"
		
	}
	
	def "save when valid submits and goes to target"() {
		setup:
		setupLocationTree()
		setupSecurityManager(newSurveyUser('test', 'uuid', DataLocation.findByCode(BUTARO).id))
		def period = newPeriod()
		def dataElement = newRawDataElement(CODE(2), Type.TYPE_LIST(Type.TYPE_MAP(["key0":Type.TYPE_ENUM(CODE(1)), "key1":Type.TYPE_NUMBER()])))
		def planning = newPlanning(period, [DISTRICT_HOSPITAL_GROUP, HEALTH_CENTER_GROUP])
		def formElement = newFormElement(dataElement)
		def planningType = newPlanningType(formElement, "[_].key1", planning)
		planningController = new EditPlanningController()
		
		when:
		planningController.params.targetURI = '/editPlanning/overview'
		planningController.params.location = DataLocation.findByCode(BUTARO).id
		planningController.params.planningType = planningType.id
		planningController.params.element = formElement.id
		planningController.params.lineNumber = 0
		planningController.params.suffix = '[0].key1'
		planningController.params['elements['+formElement.id+'].value[0].key1'] = '123'
		planningController.save()
		
		then:
		planningController.modelAndView == null
		planningController.response.redirectedUrl == '/editPlanning/overview'
		FormEnteredValue.count() == 1
		FormEnteredValue.list()[0].value.getAttribute('submitted') == "true"
	}
	
}
