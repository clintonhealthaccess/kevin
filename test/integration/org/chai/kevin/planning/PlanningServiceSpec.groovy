package org.chai.kevin.planning

import org.chai.kevin.data.Type;
import org.chai.kevin.location.DataLocationEntity;
import org.chai.kevin.planning.PlanningCost.PlanningCostType;
import org.chai.kevin.value.RawDataElementValue;
import org.chai.kevin.value.Value;

class PlanningServiceSpec extends PlanningIntegrationTests {

	def planningService
	
	def "get planning lines"() {
		setup:
		setupLocationTree()
		def period = newPeriod()
		def enume = newEnume(CODE(1))
		newEnumOption(enume, "value")
		def dataElement = newRawDataElement(CODE(2), 
			Type.TYPE_LIST(Type.TYPE_MAP(["key0":Type.TYPE_ENUM(CODE(1)), "key1":Type.TYPE_NUMBER()])))
		def planning = newPlanning(period)
		def planningType = newPlanningType(dataElement, "[_].key0", "[_].key1", planning)
		def planningList = null
		
		when:
		planningList = planningService.getPlanningList(planningType, DataLocationEntity.findByCode(BUTARO))
		
		then:
		planningList.planningEntries.isEmpty()
		
		when:
		newRawDataElementValue(dataElement, period, DataLocationEntity.findByCode(BUTARO), 
			new Value("{\"value\":[{\"value\":[{\"map_key\":\"key0\", \"map_value\":{\"value\":\"value\"}},{\"map_key\":\"key1\", \"map_value\":{\"value\":1}}]}]}"))
		planningList = planningService.getPlanningList(planningType, DataLocationEntity.findByCode(BUTARO))
		
		then:
		planningList.planningEntries.size() == 1
		planningList.planningEntries[0].lineNumber == 0
		planningList.planningEntries[0].getValue("[0]").equals(new Value("{\"value\":[{\"map_key\":\"key0\", \"map_value\":{\"value\":\"value\"}},{\"map_key\":\"key1\", \"map_value\":{\"value\":1}}]}"))
		
		
	}
	
	def "get budget lines"() {
		setup:
		setupLocationTree()
		def period = newPeriod()
		def enume = newEnume(CODE(1))
		newEnumOption(enume, "value")
		def dataElement = newRawDataElement(CODE(2),
			Type.TYPE_LIST(Type.TYPE_MAP(["key0":Type.TYPE_ENUM(CODE(1)), "key1":Type.TYPE_NUMBER()])))
		def planning = newPlanning(period)
		def planningType = newPlanningType(dataElement, "[_].key0", "[_].key1", planning)
		def planningTypeBudget = null
		
		when:
		planningTypeBudget = planningService.getPlanningTypeBudget(planningType, DataLocationEntity.findByCode(BUTARO))
		
		then:
		planningTypeBudget.budgetPlanningEntries.size() == 0
		when:
		def elementValue = newRawDataElementValue(dataElement, period, DataLocationEntity.findByCode(BUTARO),
			new Value("{\"value\":[{\"value\":[{\"map_key\":\"key0\", \"map_value\":{\"value\":\"value\"}},{\"map_key\":\"key1\", \"map_value\":{\"value\":1}}]}]}"))
		elementValue.value.listValue[0].setAttribute("submitted", "true")
		planningTypeBudget = planningService.getPlanningTypeBudget(planningType, DataLocationEntity.findByCode(BUTARO))
		
		then:
		planningTypeBudget.budgetPlanningEntries.size() == 1
		
		when:
		def element = newNormalizedDataElement(CODE(3), Type.TYPE_LIST(Type.TYPE_NUMBER()), 
			e([(period.id+''):[(DISTRICT_HOSPITAL_GROUP): '($'+dataElement.id+' -> transform each x (if (x.key0 == "value") x.key1 * 2 else 0))']]))
		def planningCost = newPlanningCost(PlanningCostType.OUTGOING, element, "[_].key1", "value", planningType)
		refreshNormalizedDataElement()
		planningTypeBudget = planningService.getPlanningTypeBudget(planningType, DataLocationEntity.findByCode(BUTARO))
		
		then:
		planningTypeBudget.budgetPlanningEntries.size() == 1
		planningTypeBudget.budgetPlanningEntries[0].budgetCosts.size() == 1
		planningTypeBudget.budgetPlanningEntries[0].getBudgetCost(planningCost).value == 2.0d
	}
	
	def "get budget lines when normalized data element does not apply"() {
		setup:
		setupLocationTree()
		def period = newPeriod()
		def enume = newEnume(CODE(1))
		newEnumOption(enume, "value")
		def dataElement = newRawDataElement(CODE(2),
			Type.TYPE_LIST(Type.TYPE_MAP(["key0":Type.TYPE_ENUM(CODE(1)), "key1":Type.TYPE_NUMBER()])))
		def planning = newPlanning(period)
		def planningType = newPlanningType(dataElement, "[_].key0", "[_].key1", planning)
		def planningTypeBudget = null
		
		when:
		def elementValue = newRawDataElementValue(dataElement, period, DataLocationEntity.findByCode(BUTARO),
			new Value("{\"value\":[{\"value\":[{\"map_key\":\"key0\", \"map_value\":{\"value\":\"value\"}},{\"map_key\":\"key1\", \"map_value\":{\"value\":1}}]}]}"))
		elementValue.value.listValue[0].setAttribute("submitted", "true")
		def element = newNormalizedDataElement(CODE(3), Type.TYPE_LIST(Type.TYPE_NUMBER()),
			e([(period.id+''):[(HEALTH_CENTER_GROUP): '($'+dataElement.id+' -> transform each x (if (x.key0 == "value") x.key1 * 2 else 0))']]))
		def planningCost = newPlanningCost(PlanningCostType.OUTGOING, element, "[_].key1", "value", planningType)
		refreshNormalizedDataElement()
		planningTypeBudget = planningService.getPlanningTypeBudget(planningType, DataLocationEntity.findByCode(BUTARO))
		
		then:
		planningTypeBudget.budgetPlanningEntries.size() == 1
		planningTypeBudget.budgetPlanningEntries[0].budgetCosts.size() == 0
	}
	
	def "get budget lines when values are missing for calculation"() {
		setup:
		setupLocationTree()
		def period = newPeriod()
		def enume = newEnume(CODE(1))
		newEnumOption(enume, "value")
		def dataElement = newRawDataElement(CODE(2),
			Type.TYPE_LIST(Type.TYPE_MAP(["key0":Type.TYPE_ENUM(CODE(1)), "key1":Type.TYPE_NUMBER()])))
		def planning = newPlanning(period)
		def planningType = newPlanningType(dataElement, "[_].key0", "[_].key1", planning)
		def planningTypeBudget = null
		
		when:
		def elementValue = newRawDataElementValue(dataElement, period, DataLocationEntity.findByCode(BUTARO),
			new Value("{\"value\":[{\"value\":[{\"map_key\":\"key0\", \"map_value\":{\"value\":\"value\"}},{\"map_key\":\"key1\", \"map_value\":{\"value\":null}}]},"+
				"{\"value\":[{\"map_key\":\"key0\", \"map_value\":{\"value\":\"value\"}},{\"map_key\":\"key1\", \"map_value\":{\"value\":1}}]}"
				+"]}"))
		elementValue.value.listValue[0].setAttribute("submitted", "true")
		elementValue.value.listValue[1].setAttribute("submitted", "true")
		def element = newNormalizedDataElement(CODE(3), Type.TYPE_LIST(Type.TYPE_NUMBER()),
			e([(period.id+''):[(DISTRICT_HOSPITAL_GROUP): '$'+dataElement.id+' -> transform each x (if (x.key0 == "value") x.key1 * 2 else 0)']]))
		def planningCost = newPlanningCost(PlanningCostType.OUTGOING, element, "[_].key1", "value", planningType)
		refreshNormalizedDataElement()
		planningTypeBudget = planningService.getPlanningTypeBudget(planningType, DataLocationEntity.findByCode(BUTARO))
		
		then:
		planningTypeBudget.budgetPlanningEntries.size() == 2
		planningTypeBudget.budgetPlanningEntries[0].budgetCosts.size() == 0
		planningTypeBudget.budgetPlanningEntries[1].budgetCosts.size() == 1
		planningTypeBudget.budgetPlanningEntries[1].getBudgetCost(planningCost).value == 2d
	}
	
	def "add planning entry"() {
		setup:
		setupLocationTree()
		def period = newPeriod()
		def enume = newEnume(CODE(1))
		newEnumOption(enume, "value")
		def dataElement = newRawDataElement(CODE(2),
			Type.TYPE_LIST(Type.TYPE_MAP(["key0":Type.TYPE_ENUM(CODE(1)), "key1":Type.TYPE_NUMBER()])))
		def planning = newPlanning(period)
		def planningType = newPlanningType(dataElement, "[_].key0", "[_].key1", planning)
		def elementValue = null
		
		when:
		planningService.modify(planningType, DataLocationEntity.findByCode(BUTARO), 0, ["elements":['[0]'], "elements[0].key0":"value", "elements[0].key1":'123'])
		elementValue = RawDataElementValue.list()[0]
		
		then:
		RawDataElementValue.count() == 1
		elementValue.value.listValue.size() == 1
		elementValue.value.listValue[0].getAttribute('budget_updated') == "false"
		
		when: 'budget updated set to false when saving existing value'
		elementValue.value.listValue[0].setAttribute('budget_updated', 'true')
		elementValue.save()
		planningService.modify(planningType, DataLocationEntity.findByCode(BUTARO), 0, ["elements":['[0]'], "elements[0].key0":"value", "elements[0].key1":'123'])
		elementValue = RawDataElementValue.list()[0]
		
		then:
		RawDataElementValue.count() == 1
		RawDataElementValue.list()[0].value.listValue.size() == 1
		RawDataElementValue.list()[0].value.listValue[0].getAttribute('budget_updated') == "false"
		
	}
	
	def "refresh budget when no value"() {
		setup:
		setupLocationTree()
		def period = newPeriod()
		def enume = newEnume(CODE(1))
		newEnumOption(enume, "value")
		def dataElement = newRawDataElement(CODE(2),
			Type.TYPE_LIST(Type.TYPE_MAP(["key0":Type.TYPE_ENUM(CODE(1)), "key1":Type.TYPE_NUMBER()])))
		def planning = newPlanning(period)
		def planningType = newPlanningType(dataElement, "[_].key0", "[_].key1", planning)
		
		when:
		planningService.refreshBudget(planningType, DataLocationEntity.findByCode(BUTARO))
		
		then:
		RawDataElementValue.count() == 1
	}
	
	def "refresh budget sets updated budget to true"() {
		setup:
		setupLocationTree()
		def period = newPeriod()
		def enume = newEnume(CODE(1))
		newEnumOption(enume, "value")
		def dataElement = newRawDataElement(CODE(2),
			Type.TYPE_LIST(Type.TYPE_MAP(["key0":Type.TYPE_ENUM(CODE(1)), "key1":Type.TYPE_NUMBER()])))
		def planning = newPlanning(period)
		def planningType = newPlanningType(dataElement, "[_].key0", "[_].key1", planning)
		def elementValue = newRawDataElementValue(dataElement, period, DataLocationEntity.findByCode(BUTARO), 
			Value.VALUE_LIST([Value.VALUE_MAP(["key0":Value.VALUE_STRING("value"), "key1":Value.VALUE_NUMBER(10)])])	
		)
		
		when:
		elementValue.value.listValue[0].setAttribute('budget_updated', 'true')
		planningService.refreshBudget(planningType, DataLocationEntity.findByCode(BUTARO))
		
		then:
		RawDataElementValue.count() == 1
		RawDataElementValue.list()[0].value.listValue[0].getAttribute('budget_updated') == "true"
	}
	
}
