package org.chai.kevin.planning;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.chai.kevin.data.DataService;
import org.chai.kevin.data.Enum;
import org.chai.kevin.data.Type;
import org.chai.kevin.location.DataEntityType;
import org.chai.kevin.location.DataLocationEntity;
import org.chai.kevin.planning.budget.BudgetCost;
import org.chai.kevin.planning.budget.PlanningEntryBudget;
import org.chai.kevin.planning.budget.PlanningTypeBudget;
import org.chai.kevin.value.RawDataElementValue;
import org.chai.kevin.value.RefreshValueService;
import org.chai.kevin.value.SumValue;
import org.chai.kevin.value.Value;
import org.chai.kevin.value.ValueService;

public class PlanningService {

	private ValueService valueService;
	private DataService dataService;
	private RefreshValueService refreshValueService;
	
	public PlanningList getPlanningList(PlanningType type, DataLocationEntity location) {
		RawDataElementValue dataElementValue = valueService.getDataElementValue(type.getDataElement(), location, type.getPeriod());
		
		return new PlanningList(type, dataElementValue, getEnums(type));
	}
	
	public PlanningEntry getPlanningEntry(PlanningType type, DataLocationEntity location, Integer lineNumber) {
		RawDataElementValue dataElementValue = getDataElementValue(type, location);
		
		return new PlanningEntry(type, dataElementValue, lineNumber, getEnums(type));
	}
	
	private Map<String, Enum> getEnums(PlanningType type) {
		Map<String, Enum> result = new HashMap<String, Enum>();
		for (Entry<String, Type> prefix : type.getDataElement().getEnumPrefixes().entrySet()) {
			result.put(prefix.getValue().getEnumCode(), dataService.findEnumByCode(prefix.getValue().getEnumCode()));
		}
		return result;
	}
	
	private RawDataElementValue getDataElementValue(PlanningType type, DataLocationEntity location) {
		RawDataElementValue dataElementValue = valueService.getDataElementValue(type.getDataElement(), location, type.getPeriod());
		if (dataElementValue == null) {
			dataElementValue = new RawDataElementValue(type.getDataElement(), location, type.getPeriod(), Value.NULL_INSTANCE());
			valueService.save(dataElementValue);
		}
		return dataElementValue;
	}
	
	public void deletePlanningEntry(PlanningType type, DataLocationEntity location, Integer lineNumber) {
		PlanningEntry planningEntry = getPlanningEntry(type, location, lineNumber);
		planningEntry.delete();
		planningEntry.save(valueService);
	}
	
	public PlanningEntry modify(PlanningType type, DataLocationEntity location, Integer lineNumber, Map<String, Object> params) {
		PlanningEntry planningEntry = getPlanningEntry(type, location, lineNumber);
		
		// first we merge the values to create a new value
		planningEntry.mergeValues(params);
		planningEntry.setBudgetUpdated(false);
		
		// second we run the validation rules
		// TODO
		
		// last we set and save the value
		planningEntry.save(valueService);
		return planningEntry;
	}
	
	public void refreshBudget(PlanningType type, DataLocationEntity location) {
		PlanningList planningList = getPlanningList(type, location);
		for (PlanningEntry planningEntry : planningList.getPlanningEntries()) {
			if (!planningEntry.isBudgetUpdated()) {
				for (PlanningCost cost : planningEntry.getPlanningCosts()) {
					refreshValueService.refreshCalculation(cost.getSum(), location, type.getPeriod());
				}
				planningEntry.setBudgetUpdated(true);
			}
		}
	}
	
	public PlanningTypeBudget getPlanningTypeBudget(PlanningType type, DataLocationEntity location) {
		PlanningList planningList = getPlanningList(type, location);
		
		Set<DataEntityType> types = new HashSet<DataEntityType>();
		types.add(location.getType());
		
		List<PlanningEntryBudget> planningEntryBudgets = new ArrayList<PlanningEntryBudget>();
		for (PlanningEntry planningEntry : planningList.getPlanningEntries()) {
			List<BudgetCost> budgetCosts = new ArrayList<BudgetCost>();
			for (PlanningCost planningCost : planningEntry.getPlanningCosts()) {
				budgetCosts.add(new BudgetCost(planningCost, (SumValue)valueService.getCalculationValue(planningCost.getSum(), location, type.getPeriod(), types)));
			}
			planningEntryBudgets.add(new PlanningEntryBudget(planningEntry, budgetCosts));
		}
		
		return new PlanningTypeBudget(type, planningEntryBudgets);
	}
	
	public void setDataService(DataService dataService) {
		this.dataService = dataService;
	}
	
	public void setValueService(ValueService valueService) {
		this.valueService = valueService;
	}

	public void setRefreshValueService(RefreshValueService refreshValueService) {
		this.refreshValueService = refreshValueService;
	}

}
