package org.chai.kevin.planning

import org.chai.kevin.data.Type;
import org.chai.kevin.location.DataLocationEntity;
import org.chai.kevin.planning.PlanningCost.PlanningCostType;
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
		def planningType = newPlanningType(dataElement, ".key0", [""])
		def planningLines = null
		
		when:
		planningLines = planningService.getPlanningLines(planningType, DataLocationEntity.findByCode(BUTARO), period)
		
		then:
		planningLines.isEmpty()
		
		when:
		newRawDataElementValue(dataElement, period, DataLocationEntity.findByCode(BUTARO), 
			new Value("{\"value\":[{\"value\":[{\"map_key\":\"key0\", \"map_value\":{\"value\":\"value\"}},{\"map_key\":\"key1\", \"map_value\":{\"value\":1}}]}]}"))
		planningLines = planningService.getPlanningLines(planningType, DataLocationEntity.findByCode(BUTARO), period)
		
		then:
		planningLines.size() == 1
		planningLines[0].lineNumber == 0
		planningLines[0].getValue("").equals(new Value("{\"value\":[{\"map_key\":\"key0\", \"map_value\":{\"value\":\"value\"}},{\"map_key\":\"key1\", \"map_value\":{\"value\":1}}]}"))
		
		
	}
	
	def "get budget lines"() {
		setup:
		setupLocationTree()
		def period = newPeriod()
		def enume = newEnume(CODE(1))
		newEnumOption(enume, "value")
		def dataElement = newRawDataElement(CODE(2),
			Type.TYPE_LIST(Type.TYPE_MAP(["key0":Type.TYPE_ENUM(CODE(1)), "key1":Type.TYPE_NUMBER()])))
		def planningType = newPlanningType(dataElement, ".key0", [""])
		def planningTypeBudget = null
		
		when:
		planningTypeBudget = planningService.getPlanningTypeBudget(planningType, DataLocationEntity.findByCode(BUTARO), period)
		
		then:
		planningTypeBudget.budgetPlanningLines.size() == 0
		
		when:
		newRawDataElementValue(dataElement, period, DataLocationEntity.findByCode(BUTARO),
			new Value("{\"value\":[{\"value\":[{\"map_key\":\"key0\", \"map_value\":{\"value\":\"value\"}},{\"map_key\":\"key1\", \"map_value\":{\"value\":1}}]}]}"))
		planningTypeBudget = planningService.getPlanningTypeBudget(planningType, DataLocationEntity.findByCode(BUTARO), period)
		
		then:
		planningTypeBudget.budgetPlanningLines.size() == 1
		
		when:
		def sum = newSum('($'+dataElement.id+' -> filter $.key0 == "value")[0].key1 * 2', CODE(3))
		def planningCost = newPlanningCost(PlanningCostType.OUTGOING, sum, "", "value", planningType)
		planningTypeBudget = planningService.getPlanningTypeBudget(planningType, DataLocationEntity.findByCode(BUTARO), period)
		
		then:
		planningTypeBudget.budgetPlanningLines.size() == 1
		planningTypeBudget.budgetPlanningLines[0].budgetCosts.size() == 1
		planningTypeBudget.budgetPlanningLines[0].budgetCosts[0].value == 2.0d
		
	}
	
}
