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
		def planningType = newPlanningType(dataElement, "[_].key0", [""], planning)
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
		def planningType = newPlanningType(dataElement, "[_].key0", [""], planning)
		def planningTypeBudget = null
		
		when:
		planningTypeBudget = planningService.getPlanningTypeBudget(planningType, DataLocationEntity.findByCode(BUTARO))
		
		then:
		planningTypeBudget.budgetPlanningLines.size() == 0
		
		when:
		newRawDataElementValue(dataElement, period, DataLocationEntity.findByCode(BUTARO),
			new Value("{\"value\":[{\"value\":[{\"map_key\":\"key0\", \"map_value\":{\"value\":\"value\"}},{\"map_key\":\"key1\", \"map_value\":{\"value\":1}}]}]}"))
		planningTypeBudget = planningService.getPlanningTypeBudget(planningType, DataLocationEntity.findByCode(BUTARO))
		
		then:
		planningTypeBudget.budgetPlanningLines.size() == 1
		
		when:
		def sum = newSum('($'+dataElement.id+' -> filter $.key0 == "value")[0].key1 * 2', CODE(3))
		def planningCost = newPlanningCost(PlanningCostType.OUTGOING, sum, "", "value", planningType)
		refreshCalculation()
		planningTypeBudget = planningService.getPlanningTypeBudget(planningType, DataLocationEntity.findByCode(BUTARO))
		
		then:
		planningTypeBudget.budgetPlanningLines.size() == 1
		planningTypeBudget.budgetPlanningLines[0].budgetCosts.size() == 1
		planningTypeBudget.budgetPlanningLines[0].budgetCosts[0].value == 2.0d
		
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
		def planningType = newPlanningType(dataElement, "[_].key0", [""], planning)
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
		elementValue.value.listValue.size() == 1
		elementValue.value.listValue[0].getAttribute('budget_updated') == "false"
		
	}
	
	def "refresh budget"() {
		
	}
	
}
