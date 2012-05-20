package org.chai.kevin.planning

import org.apache.tomcat.util.bcel.classfile.ElementValue;
import org.chai.kevin.data.NormalizedDataElement;
import org.chai.kevin.data.Type;
import org.chai.kevin.form.FormEnteredValue;
import org.chai.kevin.location.DataLocationController;
import org.chai.kevin.location.DataLocation;
import org.chai.kevin.location.Location;
import org.chai.kevin.planning.PlanningCost.PlanningCostType;
import org.chai.kevin.value.NormalizedDataElementValue;
import org.chai.kevin.value.RawDataElementValue;
import org.chai.kevin.value.Status;
import org.chai.kevin.value.Value;

class PlanningServiceSpec extends PlanningIntegrationTests {

	def planningService
	
	def "get planning lines with non-existing enum"() {
		setup:
		setupLocationTree()
		setupSecurityManager(newSurveyUser('test', 'uuid', DataLocation.findByCode(BUTARO).id))
		def period = newPeriod()
		def dataElement = newRawDataElement(CODE(2), 
			Type.TYPE_LIST(Type.TYPE_MAP(["key0":Type.TYPE_ENUM(CODE(1)), "key1":Type.TYPE_NUMBER()])))
		def planning = newPlanning(period, [DISTRICT_HOSPITAL_GROUP, HEALTH_CENTER_GROUP])
		def formElement = newFormElement(dataElement)
		def planningType = newPlanningType(formElement, "[_].key0", "[_].key1", planning)
		
		when:
		def planningList = planningService.getPlanningList(planningType, DataLocation.findByCode(BUTARO))
		
		then:
		planningList.planningEntries.isEmpty()
	}
	
	def "get planning lines when empty"() {
		setup:
		setupLocationTree()
		setupSecurityManager(newSurveyUser('test', 'uuid', DataLocation.findByCode(BUTARO).id))
		def period = newPeriod()
		def enume = newEnume(CODE(1))
		newEnumOption(enume, "value")
		def dataElement = newRawDataElement(CODE(2), 
			Type.TYPE_LIST(Type.TYPE_MAP(["key0":Type.TYPE_ENUM(CODE(1)), "key1":Type.TYPE_NUMBER()])))
		def planning = newPlanning(period, [DISTRICT_HOSPITAL_GROUP, HEALTH_CENTER_GROUP])
		def formElement = newFormElement(dataElement)
		def planningType = newPlanningType(formElement, "[_].key0", "[_].key1", planning)
		def planningList = null
		
		when:
		planningList = planningService.getPlanningList(planningType, DataLocation.findByCode(BUTARO))
		
		then:
		planningList.planningEntries.isEmpty()
		planningList.planningEntryBudgetList.isEmpty()
	}
	
	def "get planning lines"() {
		setup:
		setupLocationTree()
		setupSecurityManager(newSurveyUser('test', 'uuid', DataLocation.findByCode(BUTARO).id))
		def period = newPeriod()
		def enume = newEnume(CODE(1))
		newEnumOption(enume, "value")
		def dataElement = newRawDataElement(CODE(2),
			Type.TYPE_LIST(Type.TYPE_MAP(["key0":Type.TYPE_ENUM(CODE(1)), "key1":Type.TYPE_NUMBER()])))
		def planning = newPlanning(period, [DISTRICT_HOSPITAL_GROUP, HEALTH_CENTER_GROUP])
		def formElement = newFormElement(dataElement)
		def planningType = newPlanningType(formElement, "[_].key0", "[_].key1", planning)
		def planningList = null
		
		when:
		newFormEnteredValue(formElement, period, DataLocation.findByCode(BUTARO), 
			new Value("{\"value\":[{\"value\":[{\"map_key\":\"key0\", \"map_value\":{\"value\":\"value\"}},{\"map_key\":\"key1\", \"map_value\":{\"value\":1}}]}]}"))
		planningList = planningService.getPlanningList(planningType, DataLocation.findByCode(BUTARO))
		
		then:
		planningList.planningEntries.size() == 1
		planningList.planningEntries[0].lineNumber == 0
		planningList.planningEntries[0].getValue("[0]").equals(new Value("{\"value\":[{\"map_key\":\"key0\", \"map_value\":{\"value\":\"value\"}},{\"map_key\":\"key1\", \"map_value\":{\"value\":1}}]}"))
	}
	
	def "get budget lines"() {
		setup:
		setupLocationTree()
		setupSecurityManager(newSurveyUser('test', 'uuid', DataLocation.findByCode(BUTARO).id))
		def period = newPeriod()
		def enume = newEnume(CODE(1))
		newEnumOption(enume, "value")
		def dataElement = newRawDataElement(CODE(2),
			Type.TYPE_LIST(Type.TYPE_MAP(["key0":Type.TYPE_ENUM(CODE(1)), "key1":Type.TYPE_NUMBER()])))
		def planning = newPlanning(period, [DISTRICT_HOSPITAL_GROUP, HEALTH_CENTER_GROUP])
		def formElement = newFormElement(dataElement)
		def planningType = newPlanningType(formElement, "[_].key0", "[_].key1", planning)
		def planningTypeBudget = null
		
		when:
		def value = new Value("{\"value\":[{\"value\":[{\"map_key\":\"key0\", \"map_value\":{\"value\":\"value\"}},{\"map_key\":\"key1\", \"map_value\":{\"value\":1}}],\"uuid\":\"uuid\"}]}")
		def formValue = newFormEnteredValue(formElement, period, DataLocation.findByCode(BUTARO), value)
		def elementValue = newRawDataElementValue(dataElement, period, DataLocation.findByCode(BUTARO), value)
		planningTypeBudget = planningService.getPlanningList(planningType, DataLocation.findByCode(BUTARO))
		
		then:
		planningTypeBudget.planningEntryBudgetList.size() == 1
		
		when:
		def element = newNormalizedDataElement(CODE(3), Type.TYPE_LIST(Type.TYPE_NUMBER()), 
			e([(period.id+''):[(DISTRICT_HOSPITAL_GROUP): '($'+dataElement.id+' -> transform each x (if (x.key0 == "value") x.key1 * 2 else 0))']]))
		def planningCost = newPlanningCost(PlanningCostType.OUTGOING, element, "value", planningType)
		refreshNormalizedDataElement()
		planningTypeBudget = planningService.getPlanningList(planningType, DataLocation.findByCode(BUTARO))
		
		then:
		planningTypeBudget.planningEntryBudgetList.size() == 1
		planningTypeBudget.planningEntryBudgetList[0].budgetCosts.size() == 1
		planningTypeBudget.planningEntryBudgetList[0].getBudgetCost(planningCost).value == 2.0d
	}
	
	def "get budget lines when data element has null value"() {
		setup:
		setupLocationTree()
		setupSecurityManager(newSurveyUser('test', 'uuid', DataLocation.findByCode(BUTARO).id))
		def period = newPeriod()
		def enume = newEnume(CODE(1))
		newEnumOption(enume, "value")
		def dataElement = newRawDataElement(CODE(2),
			Type.TYPE_LIST(Type.TYPE_MAP(["key0":Type.TYPE_ENUM(CODE(1)), "key1":Type.TYPE_NUMBER()])))
		def planning = newPlanning(period, [DISTRICT_HOSPITAL_GROUP, HEALTH_CENTER_GROUP])
		def formElement = newFormElement(dataElement)
		def planningType = newPlanningType(formElement, "[_].key0", "[_].key1", planning)
		def element = newNormalizedDataElement(CODE(3), Type.TYPE_LIST(Type.TYPE_NUMBER()),
			e([(period.id+''):[(HEALTH_CENTER_GROUP): '($'+dataElement.id+' -> transform each x (if (x.key0 == "value") x.key1 * 2 else 0))']]))
		def planningCost = newPlanningCost(PlanningCostType.OUTGOING, element, "value", planningType)
		def planningTypeBudget = null
		
		when:
		def value = new Value("{\"value\":[{\"value\":[{\"map_key\":\"key0\", \"map_value\":{\"value\":\"value\"}},{\"map_key\":\"key1\", \"map_value\":{\"value\":null}}],\"uuid\":\"uuid\"}]}")
		value.listValue[0].setAttribute("submitted", "true")
		newFormEnteredValue(formElement, period, DataLocation.findByCode(BUTARO), value)
		newRawDataElementValue(dataElement, period, DataLocation.findByCode(BUTARO), value)
		
		refreshNormalizedDataElement()
		planningTypeBudget = planningService.getPlanningList(planningType, DataLocation.findByCode(BUTARO))
		
		then:
		planningTypeBudget.planningEntryBudgetList.size() == 1
		planningTypeBudget.planningEntryBudgetList[0].budgetCosts.size() == 0
	}
	
	def "get budget lines when normalized data element does not apply"() {
		setup:
		setupLocationTree()
		setupSecurityManager(newSurveyUser('test', 'uuid', DataLocation.findByCode(BUTARO).id))
		def period = newPeriod()
		def enume = newEnume(CODE(1))
		newEnumOption(enume, "value")
		def dataElement = newRawDataElement(CODE(2),
			Type.TYPE_LIST(Type.TYPE_MAP(["key0":Type.TYPE_ENUM(CODE(1)), "key1":Type.TYPE_NUMBER()])))
		def planning = newPlanning(period, [DISTRICT_HOSPITAL_GROUP, HEALTH_CENTER_GROUP])
		def formElement = newFormElement(dataElement)
		def planningType = newPlanningType(formElement, "[_].key0", "[_].key1", planning)
		def planningTypeBudget = null
		
		when:
		def value = new Value("{\"value\":[{\"value\":[{\"map_key\":\"key0\", \"map_value\":{\"value\":\"value\"}},{\"map_key\":\"key1\", \"map_value\":{\"value\":1}}],\"uuid\":\"uuid\"}]}")
		def formValue = newFormEnteredValue(formElement, period, DataLocation.findByCode(BUTARO), value)
		def elementValue = newRawDataElementValue(dataElement, period, DataLocation.findByCode(BUTARO), value)
		def element = newNormalizedDataElement(CODE(3), Type.TYPE_LIST(Type.TYPE_NUMBER()),
			e([(period.id+''):[(HEALTH_CENTER_GROUP): '($'+dataElement.id+' -> transform each x (if (x.key0 == "value") x.key1 * 2 else 0))']]))
		def planningCost = newPlanningCost(PlanningCostType.OUTGOING, element, "value", planningType)
		refreshNormalizedDataElement()
		planningTypeBudget = planningService.getPlanningList(planningType, DataLocation.findByCode(BUTARO))
		
		then:
		planningTypeBudget.planningEntryBudgetList.size() == 1
		planningTypeBudget.planningEntryBudgetList[0].budgetCosts.size() == 0
	}
	
	def "get budget lines when values are missing for calculation"() {
		setup:
		setupLocationTree()
		setupSecurityManager(newSurveyUser('test', 'uuid', DataLocation.findByCode(BUTARO).id))
		def period = newPeriod()
		def enume = newEnume(CODE(1))
		newEnumOption(enume, "value")
		def dataElement = newRawDataElement(CODE(2),
			Type.TYPE_LIST(Type.TYPE_MAP(["key0":Type.TYPE_ENUM(CODE(1)), "key1":Type.TYPE_NUMBER()])))
		def planning = newPlanning(period, [DISTRICT_HOSPITAL_GROUP, HEALTH_CENTER_GROUP])
		def formElement = newFormElement(dataElement)
		def planningType = newPlanningType(formElement, "[_].key0", "[_].key1", planning)
		def planningTypeBudget = null
		
		when:
		def value = new Value("{\"value\":[{\"value\":[{\"map_key\":\"key0\", \"map_value\":{\"value\":\"value\"}},{\"map_key\":\"key1\", \"map_value\":{\"value\":null}}],\"uuid\":\"uuid1\"},"+
			"{\"value\":[{\"map_key\":\"key0\", \"map_value\":{\"value\":\"value\"}},{\"map_key\":\"key1\", \"map_value\":{\"value\":1}}],\"uuid\":\"uuid2\"}]}")
		def elementValue = newFormEnteredValue(formElement, period, DataLocation.findByCode(BUTARO), value)
		def dataElementValue = newRawDataElementValue(dataElement, period, DataLocation.findByCode(BUTARO), value);
		value.listValue[0].setAttribute("submitted", "true")
		value.listValue[1].setAttribute("submitted", "true")
		def element = newNormalizedDataElement(CODE(3), Type.TYPE_LIST(Type.TYPE_NUMBER()),
			e([(period.id+''):[(DISTRICT_HOSPITAL_GROUP): '$'+dataElement.id+' -> transform each x (if (x.key0 == "value") x.key1 * 2 else 0)']]))
		def planningCost = newPlanningCost(PlanningCostType.OUTGOING, element, "value", planningType)
		refreshNormalizedDataElement()
		planningTypeBudget = planningService.getPlanningList(planningType, DataLocation.findByCode(BUTARO))
		
		then:
		planningTypeBudget.planningEntryBudgetList.size() == 2
		planningTypeBudget.planningEntryBudgetList[0].budgetCosts.size() == 0
		planningTypeBudget.planningEntryBudgetList[1].budgetCosts.size() == 1
		planningTypeBudget.planningEntryBudgetList[1].getBudgetCost(planningCost).value == 2d
	}
	
	def "add planning entry"() {
		setup:
		setupLocationTree()
		setupSecurityManager(newSurveyUser('test', 'uuid', DataLocation.findByCode(BUTARO).id))
		def period = newPeriod()
		def enume = newEnume(CODE(1))
		newEnumOption(enume, "value")
		def dataElement = newRawDataElement(CODE(2),
			Type.TYPE_LIST(Type.TYPE_MAP(["key0":Type.TYPE_ENUM(CODE(1)), "key1":Type.TYPE_NUMBER()])))
		def planning = newPlanning(period, [DISTRICT_HOSPITAL_GROUP, HEALTH_CENTER_GROUP])
		def formElement = newFormElement(dataElement)
		def planningType = newPlanningType(formElement, "[_].key0", "[_].key1", planning)
		def dataElementValue = null
		def formValue = null
		
		when:
		planningService.modify(planningType, DataLocation.findByCode(BUTARO), 0, [("elements["+formElement.id+"].value"):['[0]'], ("elements["+formElement.id+"].value[0].key0"):"value", ("elements["+formElement.id+"].value[0].key1"):'123'])
		formValue = FormEnteredValue.list()[0]
		
		then:
		formValue.value.listValue.size() == 1
		formValue.value.listValue[0].mapValue['key0'].stringValue == 'value'
		formValue.value.listValue[0].mapValue['key1'].numberValue == 123d
		formValue.value.getAttribute('submitted') == "false"
		
		when: 'submitted set to false when saving existing value'
		formValue.value.setAttribute('submitted', 'true')
		formValue.save()
		planningService.modify(planningType, DataLocation.findByCode(BUTARO), 0, ["elements":['[0]'], "elements[0].key0":"value", "elements[0].key1":'123'])
		formValue = FormEnteredValue.list()[0]
		
		then:
		formValue.list()[0].value.listValue.size() == 1
		formValue.list()[0].value.getAttribute('submitted') == "false"
		
	}
	
	def "validation works"() {
		setup:
		setupLocationTree()
		setupSecurityManager(newSurveyUser('test', 'uuid', DataLocation.findByCode(BUTARO).id))
		def period = newPeriod()
		def enume = newEnume(CODE(1))
		newEnumOption(enume, "value")
		def dataElement = newRawDataElement(CODE(2),
			Type.TYPE_LIST(Type.TYPE_MAP(["key0":Type.TYPE_ENUM(CODE(1)), "key1":Type.TYPE_NUMBER()])))
		def planning = newPlanning(period, [DISTRICT_HOSPITAL_GROUP, HEALTH_CENTER_GROUP])
		def formElement = newFormElement(dataElement)
		def planningType = newPlanningType(formElement, "[_].key0", "[_].key1", planning)
		def validationRule = newFormValidationRule(formElement, "[_].key1", [DISTRICT_HOSPITAL_GROUP, HEALTH_CENTER_GROUP], "\$"+formElement.id+"[_].key1 < 100", [])
		def formValue = null
		
		when:
		planningService.modify(planningType, DataLocation.findByCode(BUTARO), 0, [("elements["+formElement.id+"].value"):['[0]'], ("elements["+formElement.id+"].value[0].key0"):"value", ("elements["+formElement.id+"].value[0].key1"):'123'])
		formValue = FormEnteredValue.list()[0]
		
		then:
		formValue.value.getAttribute("submitted") == "false"
		formValue.value.listValue.size() == 1
		formValue.getValidatable().getInvalidPrefixes().equals(s(['[0].key1']))
	}
	
	def "modify sets submitted to false"() {
		setup:
		setupLocationTree()
		setupSecurityManager(newSurveyUser('test', 'uuid', DataLocation.findByCode(BUTARO).id))
		def period = newPeriod()
		def enume = newEnume(CODE(1))
		newEnumOption(enume, "value")
		def dataElement = newRawDataElement(CODE(2),
			Type.TYPE_LIST(Type.TYPE_MAP(["key0":Type.TYPE_ENUM(CODE(1)), "key1":Type.TYPE_NUMBER()])))
		def planning = newPlanning(period, [DISTRICT_HOSPITAL_GROUP, HEALTH_CENTER_GROUP])
		def formElement = newFormElement(dataElement)
		def planningType = newPlanningType(formElement, "[_].key0", "[_].key1", planning)
		def formValue = null
		
		when:
		planningService.modify(planningType, DataLocation.findByCode(BUTARO), 0, [("elements["+formElement.id+"].value"):['[0]'], ("elements["+formElement.id+"].value[0].key0"):"value", ("elements["+formElement.id+"].value[0].key1"):'123'])
		formValue = FormEnteredValue.list()[0]
		
		then:
		formValue.value.getAttribute("submitted") == "false"
		formValue.value.listValue.size() == 1
	}
	
	def "refresh budget when no value"() {
		setup:
		setupLocationTree()
		setupSecurityManager(newSurveyUser('test', 'uuid', DataLocation.findByCode(BUTARO).id))
		def period = newPeriod()
		def enume = newEnume(CODE(1))
		newEnumOption(enume, "value")
		def dataElement = newRawDataElement(CODE(2),
			Type.TYPE_LIST(Type.TYPE_MAP(["key0":Type.TYPE_ENUM(CODE(1)), "key1":Type.TYPE_NUMBER()])))
		def planning = newPlanning(period, [DISTRICT_HOSPITAL_GROUP, HEALTH_CENTER_GROUP])
		def formElement = newFormElement(dataElement)
		def planningType = newPlanningType(formElement, "[_].key0", "[_].key1", planning)
		
		when:
		planningService.refreshBudgetIfNeeded(planning, DataLocation.findByCode(BUTARO))
		
		then:
		RawDataElementValue.count() == 0
		FormEnteredValue.count() == 0
	}
	
	def "refresh budget creates normalized data value"() {
		setup:
		setupLocationTree()
		setupSecurityManager(newSurveyUser('test', 'uuid', DataLocation.findByCode(BUTARO).id))
		def period = newPeriod()
		def enume = newEnume(CODE(1))
		newEnumOption(enume, "value")
		def dataElement = newRawDataElement(CODE(2),
			Type.TYPE_LIST(Type.TYPE_MAP(["key0":Type.TYPE_ENUM(CODE(1)), "key1":Type.TYPE_NUMBER()])))
		def planning = newPlanning(period, [DISTRICT_HOSPITAL_GROUP, HEALTH_CENTER_GROUP])
		def formElement = newFormElement(dataElement)
		def planningType = newPlanningType(formElement, "[_].key0", "[_].key1", planning)
		def element = newNormalizedDataElement(CODE(3), Type.TYPE_LIST(Type.TYPE_NUMBER()),
			e([(period.id+''):[(DISTRICT_HOSPITAL_GROUP): '($'+dataElement.id+' -> transform each x (if (x.key0 == "value") x.key1 * 2 else 0))']]))
		def planningCost = newPlanningCost(PlanningCostType.OUTGOING, element, "value", planningType)
		
		def value = new Value("{\"value\":[{\"value\":[{\"map_key\":\"key0\", \"map_value\":{\"value\":\"value\"}},{\"map_key\":\"key1\", \"map_value\":{\"value\":1}}],\"uuid\":\"uuid\"}]}")
		def elementValue = newRawDataElementValue(dataElement, period, DataLocation.findByCode(BUTARO), value)
		
		when:
		planningService.refreshBudgetIfNeeded(planning, DataLocation.findByCode(BUTARO))
		
		then:
		RawDataElementValue.count() == 1
		RawDataElementValue.list()[0].value.listValue[0].mapValue['key1'].numberValue == 1d
		NormalizedDataElementValue.count() == 1
		NormalizedDataElementValue.list()[0].value.listValue[0].numberValue == 2d
	}
	
	def "refresh budget with missing value for calculation"() {
		setup:
		setupLocationTree()
		setupSecurityManager(newSurveyUser('test', 'uuid', DataLocation.findByCode(BUTARO).id))
		def period = newPeriod()
		def enume = newEnume(CODE(1))
		newEnumOption(enume, "value")
		def dataElement = newRawDataElement(CODE(2),
			Type.TYPE_LIST(Type.TYPE_MAP(["key0":Type.TYPE_ENUM(CODE(1)), "key1":Type.TYPE_NUMBER()])))
		def planning = newPlanning(period, [DISTRICT_HOSPITAL_GROUP, HEALTH_CENTER_GROUP])
		def formElement = newFormElement(dataElement)
		def planningType = newPlanningType(formElement, "[_].key0", "[_].key1", planning)
		def element = newNormalizedDataElement(CODE(3), Type.TYPE_LIST(Type.TYPE_NUMBER()),
			e([(period.id+''):[(DISTRICT_HOSPITAL_GROUP): '($'+dataElement.id+' -> transform each x (if (x.key0 == "value") x.key1 * 2 else 0))']]))
		def planningCost = newPlanningCost(PlanningCostType.OUTGOING, element, "value", planningType)
		
		when:
		def value = new Value("{\"value\":[{\"value\":[{\"map_key\":\"key0\", \"map_value\":{\"value\":\"value\"}},{\"map_key\":\"key1\", \"map_value\":{\"value\":null}}],\"uuid\":\"uuid\"}]}")
		newFormEnteredValue(formElement, period, DataLocation.findByCode(BUTARO), value)
		newRawDataElementValue(dataElement, period, DataLocation.findByCode(BUTARO), value)
		planningService.refreshBudgetIfNeeded(planning, DataLocation.findByCode(BUTARO))
		
		then:
		NormalizedDataElementValue.count() == 1
		NormalizedDataElementValue.list()[0].value.listValue[0].isNull()	
	}
	
	def "needs update returns false when normalized data is up-to-date and raw data element does not exist"() {
		setup:
		setupLocationTree()
		setupSecurityManager(newSurveyUser('test', 'uuid', DataLocation.findByCode(BUTARO).id))
		def period = newPeriod()
		def enume = newEnume(CODE(1))
		newEnumOption(enume, "value")
		def planning = newPlanning(period, [DISTRICT_HOSPITAL_GROUP, HEALTH_CENTER_GROUP])
		def dataElement = newRawDataElement(CODE(2),
			Type.TYPE_LIST(Type.TYPE_MAP(["key0":Type.TYPE_ENUM(CODE(1)), "key1":Type.TYPE_NUMBER()])))
		def formElement = newFormElement(dataElement)
		def planningType = newPlanningType(formElement, "[_].key0", "[_].key1", planning)
		def element = newNormalizedDataElement(CODE(3), Type.TYPE_LIST(Type.TYPE_NUMBER()),
			e([(period.id+''):[(DISTRICT_HOSPITAL_GROUP): '($'+dataElement.id+' -> transform each x (if (x.key0 == "value") x.key1 * 2 else 0))']]))
		def planningCost = newPlanningCost(PlanningCostType.OUTGOING, element, "value", planningType)
		
		def elementValue = newNormalizedDataElementValue(element, DataLocation.findByCode(BUTARO), period, Status.VALID, Value.VALUE_LIST([Value.VALUE_NUMBER(2d)]))
		def date = elementValue.timestamp
		
		when:
		planningService.refreshBudgetIfNeeded(planning, DataLocation.findByCode(BUTARO))
		
		then:
		NormalizedDataElementValue.list()[0].timestamp.equals(date)
	}
	
	def "needs update returns false when normalized data element is up-to-date"() {
		setup:
		setupLocationTree()
		setupSecurityManager(newSurveyUser('test', 'uuid', DataLocation.findByCode(BUTARO).id))
		def period = newPeriod()
		def enume = newEnume(CODE(1))
		newEnumOption(enume, "value")
		def planning = newPlanning(period, [DISTRICT_HOSPITAL_GROUP, HEALTH_CENTER_GROUP])
		def dataElement = newRawDataElement(CODE(2),
			Type.TYPE_LIST(Type.TYPE_MAP(["key0":Type.TYPE_ENUM(CODE(1)), "key1":Type.TYPE_NUMBER()])))
		def formElement = newFormElement(dataElement)
		def planningType = newPlanningType(formElement, "[_].key0", "[_].key1", planning)
		def element = newNormalizedDataElement(CODE(3), Type.TYPE_LIST(Type.TYPE_NUMBER()),
			e([(period.id+''):[(DISTRICT_HOSPITAL_GROUP): '2.0']]))
		def planningCost = newPlanningCost(PlanningCostType.OUTGOING, element, "value", planningType)
		
		def elementValue = newNormalizedDataElementValue(element, DataLocation.findByCode(BUTARO), period, Status.VALID, Value.VALUE_LIST([Value.VALUE_NUMBER(2d)]))
		def date = elementValue.timestamp
		
		when:
		planningService.refreshBudgetIfNeeded(planning, DataLocation.findByCode(BUTARO))
		
		then:
		NormalizedDataElementValue.list()[0].timestamp.equals(date)
	}
	
	def "needs update returns true when normalized data element is not up-to-date"() {
		setup:
		setupLocationTree()
		setupSecurityManager(newSurveyUser('test', 'uuid', DataLocation.findByCode(BUTARO).id))
		def period = newPeriod()
		def enume = newEnume(CODE(1))
		newEnumOption(enume, "value")
		def planning = newPlanning(period, [DISTRICT_HOSPITAL_GROUP, HEALTH_CENTER_GROUP])
		def dataElement = newRawDataElement(CODE(2),
			Type.TYPE_LIST(Type.TYPE_MAP(["key0":Type.TYPE_ENUM(CODE(1)), "key1":Type.TYPE_NUMBER()])))
		def formElement = newFormElement(dataElement)
		def planningType = newPlanningType(formElement, "[_].key0", "[_].key1", planning)
		def element = newNormalizedDataElement(CODE(3), Type.TYPE_LIST(Type.TYPE_NUMBER()),
			e([(period.id+''):[(DISTRICT_HOSPITAL_GROUP): '2.0']]))
		def planningCost = newPlanningCost(PlanningCostType.OUTGOING, element, "value", planningType)
		def elementValue = newNormalizedDataElementValue(element, DataLocation.findByCode(BUTARO), period, Status.VALID, Value.VALUE_LIST([Value.VALUE_NUMBER(2d)]))
		def date = new Date()
		element.setTimestamp(date)
		element.save(failOnError: true)
		
		when:
		planningService.refreshBudgetIfNeeded(planning, DataLocation.findByCode(BUTARO))
		
		then:
		!NormalizedDataElementValue.list()[0].timestamp.equals(date)
	}
	
	def "submit if needed does not submit when not needed"() {
		expect:
		setup:
		setupLocationTree()
		setupSecurityManager(newSurveyUser('test', 'uuid', DataLocation.findByCode(BUTARO).id))
		def period = newPeriod()
		def dataElement = newRawDataElement(CODE(2),
			Type.TYPE_LIST(Type.TYPE_MAP(["key0":Type.TYPE_ENUM(CODE(1)), "key1":Type.TYPE_NUMBER()])))
		def planning = newPlanning(period, [DISTRICT_HOSPITAL_GROUP, HEALTH_CENTER_GROUP])
		def formElement = newFormElement(dataElement)
		def planningType = newPlanningType(formElement, "[_].key0", "[_].key1", planning)
		def value = new Value("{\"value\":[{\"value\":[{\"map_key\":\"key0\", \"map_value\":{\"value\":\"value\"}},{\"map_key\":\"key1\", \"map_value\":{\"value\":1}}],\"uuid\":\"uuid\"}]}")
		value.setAttribute("submitted", "true")
		def elementValue = newFormEnteredValue(formElement, period, DataLocation.findByCode(BUTARO), value)
		
		when:
		planningService.submitIfNeeded(planning, DataLocation.findByCode(BUTARO))
		
		then:
		RawDataElementValue.count() == 0
	}
	
	def "submit does not crash when submitted attribute not present"() {
		setup:
		setupLocationTree()
		setupSecurityManager(newSurveyUser('test', 'uuid', DataLocation.findByCode(BUTARO).id))
		def period = newPeriod()
		def dataElement = newRawDataElement(CODE(2),
			Type.TYPE_LIST(Type.TYPE_MAP(["key0":Type.TYPE_ENUM(CODE(1)), "key1":Type.TYPE_NUMBER()])))
		def planning = newPlanning(period, [DISTRICT_HOSPITAL_GROUP, HEALTH_CENTER_GROUP])
		def formElement = newFormElement(dataElement)
		def planningType = newPlanningType(formElement, "[_].key0", "[_].key1", planning)
		def elementValue = newFormEnteredValue(formElement, period, DataLocation.findByCode(BUTARO),
			new Value("{\"value\":[{\"value\":[{\"map_key\":\"key0\", \"map_value\":{\"value\":\"value\"}},{\"map_key\":\"key1\", \"map_value\":{\"value\":1}}],\"uuid\":\"uuid\"}]}"))
		
		when:
		planningService.submitIfNeeded(planning, DataLocation.findByCode(BUTARO))
		
		then:
		RawDataElementValue.count() == 1
		RawDataElementValue.list()[0].value.listValue[0].mapValue['key1'].numberValue == 1d
	}
	
	def "submit submits when submitted is false"() {
		setup:
		setupLocationTree()
		setupSecurityManager(newSurveyUser('test', 'uuid', DataLocation.findByCode(BUTARO).id))
		def period = newPeriod()
		def dataElement = newRawDataElement(CODE(2),
			Type.TYPE_LIST(Type.TYPE_MAP(["key0":Type.TYPE_ENUM(CODE(1)), "key1":Type.TYPE_NUMBER()])))
		def planning = newPlanning(period, [DISTRICT_HOSPITAL_GROUP, HEALTH_CENTER_GROUP])
		def formElement = newFormElement(dataElement)
		def planningType = newPlanningType(formElement, "[_].key0", "[_].key1", planning)
		def elementValue = newFormEnteredValue(formElement, period, DataLocation.findByCode(BUTARO),
			new Value("{\"value\":[{\"value\":[{\"map_key\":\"key0\", \"map_value\":{\"value\":\"value\"}},{\"map_key\":\"key1\", \"map_value\":{\"value\":1}}],\"uuid\":\"uuid\"}]}"))
		elementValue.value.setAttribute("submitted", "false")
		
		when:
		planningService.submitIfNeeded(planning, DataLocation.findByCode(BUTARO))
		
		then:
		RawDataElementValue.count() == 1
		RawDataElementValue.list()[0].value.listValue[0].mapValue['key1'].numberValue == 1d
	}
	
	def "submit does not submit when submitted is true"() {
		setup:
		setupLocationTree()
		setupSecurityManager(newSurveyUser('test', 'uuid', DataLocation.findByCode(BUTARO).id))
		def period = newPeriod()
		def dataElement = newRawDataElement(CODE(2),
			Type.TYPE_LIST(Type.TYPE_MAP(["key0":Type.TYPE_ENUM(CODE(1)), "key1":Type.TYPE_NUMBER()])))
		def planning = newPlanning(period, [DISTRICT_HOSPITAL_GROUP, HEALTH_CENTER_GROUP])
		def formElement = newFormElement(dataElement)
		def planningType = newPlanningType(formElement, "[_].key0", "[_].key1", planning)
		def elementValue = newFormEnteredValue(formElement, period, DataLocation.findByCode(BUTARO),
			new Value("{\"value\":[{\"value\":[{\"map_key\":\"key0\", \"map_value\":{\"value\":\"value\"}},{\"map_key\":\"key1\", \"map_value\":{\"value\":1}}],\"uuid\":\"uuid\"}]}"))
		elementValue.value.setAttribute("submitted", "true")
		
		when:
		planningService.submitIfNeeded(planning, DataLocation.findByCode(BUTARO))
		
		then:
		RawDataElementValue.count() == 0
	}
	
	def "get planning summary"() {
		setup:
		setupLocationTree()
		setupSecurityManager(newSurveyUser('test', 'uuid', DataLocation.findByCode(BUTARO).id))
		def period = newPeriod()
		def dataElement = newRawDataElement(CODE(2),
			Type.TYPE_LIST(Type.TYPE_MAP(["key0":Type.TYPE_ENUM(CODE(1)), "key1":Type.TYPE_NUMBER()])))
		def planning = newPlanning(period, [DISTRICT_HOSPITAL_GROUP, HEALTH_CENTER_GROUP])
		def formElement = newFormElement(dataElement)
		def planningType = newPlanningType(formElement, "[_].key0", "[_].key1", planning)
		def planningSummary = null
		
		when:
		planningSummary = planningService.getSummaryPage(planning, Location.findByCode(RWANDA))
		
		then:
		planningSummary.dataLocations.equals([DataLocation.findByCode(BUTARO), DataLocation.findByCode(KIVUYE)])
		planningSummary.getNumberOfEntries(DataLocation.findByCode(BUTARO), planningType) == 0
		
		when:
		def elementValue = newFormEnteredValue(formElement, period, DataLocation.findByCode(BUTARO),
			new Value("{\"value\":[{\"value\":[{\"map_key\":\"key0\", \"map_value\":{\"value\":\"value\"}},{\"map_key\":\"key1\", \"map_value\":{\"value\":1}}],\"uuid\":\"uuid\"}]}"))
		planningSummary = planningService.getSummaryPage(planning, Location.findByCode(RWANDA))
		
		then:
		planningSummary.dataLocations.equals([DataLocation.findByCode(BUTARO), DataLocation.findByCode(KIVUYE)])
		planningSummary.getNumberOfEntries(DataLocation.findByCode(BUTARO), planningType) == 1
		
	}
	
//	TODO think about how to make that test pass
//	def "submit creates raw data element value - does not transfer non-submitted values"() {
//		setup:
//		setupLocationTree()
//		setupSecurityManager(newSurveyUser('test', 'uuid', DataLocation.findByCode(BUTARO).id))
//		def period = newPeriod()
//		def dataElement = newRawDataElement(CODE(2),
//			Type.TYPE_LIST(Type.TYPE_MAP(["key0":Type.TYPE_ENUM(CODE(1)), "key1":Type.TYPE_NUMBER()])))
//		def planning = newPlanning(period)
//		def formElement = newFormElement(dataElement)
//		def planningType = newPlanningType(formElement, "[_].key0", "[_].key1", planning)
//		def elementValue = newFormEnteredValue(formElement, period, DataLocation.findByCode(BUTARO),
//			new Value("{\"value\":[{\"value\":[{\"map_key\":\"key0\", \"map_value\":{\"value\":\"value\"}},{\"map_key\":\"key1\", \"map_value\":{\"value\":1}}],\"uuid\":\"uuid\"}]}"))
//		
//		when:
//		planningService.submit(planningType, DataLocation.findByCode(BUTARO), 0)
//		
//		then:
//		RawDataElementValue.count() == 1
//		RawDataElementValue.list()[0].value.listValue.size == 0
//	}

}
