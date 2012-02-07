package org.chai.kevin.planning;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.chai.kevin.data.DataService;
import org.chai.kevin.location.DataEntityType;
import org.chai.kevin.location.DataLocationEntity;
import org.chai.kevin.value.ExpressionService;
import org.chai.kevin.value.RawDataElementValue;
import org.chai.kevin.value.RefreshValueService;
import org.chai.kevin.value.SumValue;
import org.chai.kevin.value.Value;
import org.chai.kevin.value.ValueService;
import org.hisp.dhis.period.Period;

public class PlanningService {

//	private ExpressionService expressionService;
	private ValueService valueService;
	private DataService dataService;
	private RefreshValueService refreshValueService;
	
	public List<PlanningLine> getPlanningLines(PlanningType type, DataLocationEntity location, Period period) {
		RawDataElementValue dataElementValue = valueService.getDataElementValue(type.getDataElement(), location, period);
		List<PlanningLine> result = new ArrayList<PlanningLine>();
		if (dataElementValue != null && !dataElementValue.getValue().isNull()) {
			for (int i = 0; i < dataElementValue.getValue().getListValue().size(); i++) {
				result.add(new PlanningLine(type, dataElementValue, i));
			}
		}
		return result;
	}
	
	public PlanningLine getPlanningLine(PlanningType type, DataLocationEntity location, Period period, Integer lineNumber) {
		RawDataElementValue dataElementValue = getDataElementValue(type, location, period);
		
		return new PlanningLine(type, dataElementValue, lineNumber);
	}
	
	private RawDataElementValue getDataElementValue(PlanningType type, DataLocationEntity location, Period period) {
		RawDataElementValue dataElementValue = valueService.getDataElementValue(type.getDataElement(), location, period);
		if (dataElementValue == null) {
			dataElementValue = new RawDataElementValue(type.getDataElement(), location, period, Value.NULL_INSTANCE());
			valueService.save(dataElementValue);
		}
		return dataElementValue;
	}
	
	public void modify(DataLocationEntity location, Period period, PlanningType type, Integer lineNumber, Map<String, Object> params) {
		PlanningLine planningLine = getPlanningLine(type, location, period, lineNumber);
		
		// first we merge the values to create a new value
		planningLine.mergeValues(params);
		
		// second we run the validation rules
		// TODO
		
		// last we set and save the value
		planningLine.save(valueService);
	}
	
	public void refreshBudget(PlanningType type, DataLocationEntity location, Period period, Integer lineNumber) {
		PlanningLine planningLine = getPlanningLine(type, location, period, lineNumber);
		
		for (PlanningCost planningCost : planningLine.getPlanningCosts()) {
			refreshValueService.refreshCalculation(planningCost.getSum(), location, period);
		}
	}
	
	public BudgetPlanningType getPlanningTypeBudget(PlanningType type, DataLocationEntity location, Period period) {
		List<PlanningLine> planningLines = getPlanningLines(type, location, period);
		
		Set<DataEntityType> types = new HashSet<DataEntityType>();
		types.add(location.getType());
		
		List<BudgetPlanningLine> budgetPlanningLines = new ArrayList<BudgetPlanningLine>();
		for (PlanningLine planningLine : planningLines) {
			List<BudgetCost> budgetCosts = new ArrayList<BudgetCost>();
			for (PlanningCost planningCost : planningLine.getPlanningCosts()) {
				budgetCosts.add(new BudgetCost(planningCost, (SumValue)valueService.getCalculationValue(planningCost.getSum(), location, period, types)));
			}
			budgetPlanningLines.add(new BudgetPlanningLine(planningLine, budgetCosts, planningLine.getNames(dataService)));
		}
		
		return new BudgetPlanningType(type, budgetPlanningLines);
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
