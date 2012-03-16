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
import org.chai.kevin.form.FormElement;
import org.chai.kevin.form.FormElement.ElementSubmitter;
import org.chai.kevin.form.FormElementService;
import org.chai.kevin.form.FormEnteredValue;
import org.chai.kevin.form.FormValidationService;
import org.chai.kevin.form.FormElement.ElementCalculator;
import org.chai.kevin.form.FormValidationService.ValidatableLocator;
import org.chai.kevin.location.DataEntityType;
import org.chai.kevin.location.DataLocationEntity;
import org.chai.kevin.location.LocationEntity;
import org.chai.kevin.planning.budget.BudgetCost;
import org.chai.kevin.planning.budget.PlanningEntryBudget;
import org.chai.kevin.planning.budget.PlanningTypeBudget;
import org.chai.kevin.survey.SurveyElement;
import org.chai.kevin.value.NormalizedDataElementValue;
import org.chai.kevin.value.RefreshValueService;
import org.chai.kevin.value.ValidatableValue;
import org.chai.kevin.value.ValueService;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.transaction.annotation.Transactional;

public class PlanningService {

	private FormValidationService formValidationService;
	private FormElementService formElementService;
	private ValueService valueService;
	private DataService dataService;
	private RefreshValueService refreshValueService;
	private SessionFactory sessionFactory;
	
	public Planning getDefaultPlanning() {
		return (Planning)sessionFactory.getCurrentSession()
				.createCriteria(Planning.class).add(Restrictions.eq("active", true)).uniqueResult();
	}
	
	@Transactional(readOnly=true)
	public PlanningSummaryPage getSummaryPage(Planning planning, LocationEntity location) {
		List<DataLocationEntity> dataEntities = location.collectDataLocationEntities(null, null);
		Map<PlanningType, PlanningTypeSummary> summaries = new HashMap<PlanningType, PlanningTypeSummary>();
		for (PlanningType planningType : planning.getPlanningTypes()) {
			summaries.put(planningType, getPlanningTypeSummary(planningType, dataEntities));
		}
		
		return new PlanningSummaryPage(planning.getPlanningTypes(), dataEntities, summaries);
	}
	
	// TODO move to planning type
	private PlanningTypeSummary getPlanningTypeSummary(PlanningType planningType, List<DataLocationEntity> dataEntities) {
		Map<DataLocationEntity, Integer> numberOfEntries = new HashMap<DataLocationEntity, Integer>();
		for (DataLocationEntity entity : dataEntities) {
			numberOfEntries.put(entity, getPlanningList(planningType, entity).getPlanningEntries().size());
		}
		return new PlanningTypeSummary(planningType, numberOfEntries);
	}
	
//	@Transactional(readOnly=true)
//	public PlanningList getPlanningList(PlanningType type, DataLocationEntity location) {
//		FormEnteredValue formEnteredValue = formElementService.getOrCreateFormEnteredValue(location, type.getFormElement());
//		
//		return new PlanningList(type, location, formEnteredValue.getValidatable(), getEnums(type));
//	}
	
	private Map<String, Enum> getEnums(PlanningType type) {
		Map<String, Enum> result = new HashMap<String, Enum>();
		for (Entry<String, Type> prefix : type.getFormElement().getDataElement().getEnumPrefixes().entrySet()) {
			result.put(prefix.getValue().getEnumCode(), dataService.findEnumByCode(prefix.getValue().getEnumCode()));
		}
		return result;
	}
	
	@Transactional(readOnly=false)
	public void deletePlanningEntry(PlanningType type, DataLocationEntity location, Integer lineNumber) {
		PlanningList planningList = getPlanningList(type, location);
		PlanningEntry planningEntry = planningList.getPlanningEntries().get(lineNumber);
		planningEntry.delete();
		planningList.save(formElementService);
	}
	
	private ValidatableLocator getLocator() {
		return new ValidatableLocator() {
			@Override
			public ValidatableValue getValidatable(Long id, DataLocationEntity location) {
				FormElement element = formElementService.getFormElement(id);
				FormEnteredValue enteredValue = formElementService.getOrCreateFormEnteredValue(location, element);
				if (enteredValue == null) return null;
				return enteredValue.getValidatable();
			}
		};
	}
	
	@Transactional(readOnly=false)
	public PlanningEntry modify(PlanningType type, DataLocationEntity location, Integer lineNumber, Map<String, Object> params) {
		PlanningList planningList = getPlanningList(type, location);
		PlanningEntry planningEntry = planningList.getOrCreatePlanningEntry(lineNumber);
		
		// first we merge the values to create a new value
		planningEntry.mergeValues(params);
		planningEntry.setBudgetUpdated(false);
		
		// second we run the validation/skip rules
		List<FormEnteredValue> affectedValues = new ArrayList<FormEnteredValue>();
		ElementCalculator elementCalculator = new ElementCalculator(affectedValues, formValidationService, formElementService, getLocator());
		planningEntry.evaluateRules(elementCalculator);
		
		// last we set and save the value
		for (FormEnteredValue formEnteredValue : affectedValues) {
			formElementService.save(formEnteredValue);
		}
		planningList.save(formElementService);
		return planningEntry;
	}
	
	@Transactional(readOnly=false)
	public void submit(PlanningType type, DataLocationEntity location, Integer lineNumber) {
		PlanningList planningList = getPlanningList(type, location);
		PlanningEntry planningEntry = planningList.getOrCreatePlanningEntry(lineNumber);
		
		// we submit the entry
		planningEntry.setSubmitted(true);

		// we refresh the corresponding raw data element
		ElementSubmitter submitter = new ElementSubmitter(formElementService, valueService);
		type.getFormElement().submit(location, type.getPeriod(), submitter);
		
		// then we recalculate the budget
		refreshBudget(planningEntry, location);
		
		// last we save the value
		planningList.save(formElementService);
	}
	
	@Transactional(readOnly=false)
	public void unsubmit(PlanningType type, DataLocationEntity location, Integer lineNumber) {
		PlanningList planningList = getPlanningList(type, location);
		PlanningEntry planningEntry = planningList.getOrCreatePlanningEntry(lineNumber);
		
		// we submit the entry
		planningEntry.setSubmitted(false);

		// we refresh the corresponding raw data element
		ElementSubmitter submitter = new ElementSubmitter(formElementService, valueService);
		type.getFormElement().submit(location, type.getPeriod(), submitter);
		
		// then we recalculate the budget
		refreshBudget(planningEntry, location);
				
		// last we save the value
		planningList.save(formElementService);
	}
		
	@Transactional(readOnly=false)
	public void refreshBudget(PlanningType type, DataLocationEntity location) {
		PlanningList planningList = getPlanningList(type, location);
		for (PlanningEntry planningEntry : planningList.getPlanningEntries()) {
			refreshBudget(planningEntry, location);
		}
		planningList.save(formElementService);
	}
	
	private void refreshBudget(PlanningEntry planningEntry, DataLocationEntity location) {
		if (planningEntry.isSubmitted() && !planningEntry.isBudgetUpdated()) {
			for (PlanningCost cost : planningEntry.getPlanningCosts()) {
				refreshValueService.refreshNormalizedDataElement(cost.getDataElement(), location, cost.getPlanningType().getPeriod());
			}
			planningEntry.setBudgetUpdated(true);
		}
	}
	
	@Transactional(readOnly=true)
	public PlanningTypeBudget getPlanningTypeBudget(PlanningType type, DataLocationEntity location) {
		PlanningList planningList = getPlanningList(type, location);
		
		Set<DataEntityType> types = new HashSet<DataEntityType>();
		types.add(location.getType());
		
		List<PlanningEntryBudget> planningEntryBudgets = new ArrayList<PlanningEntryBudget>();
		for (PlanningEntry planningEntry : planningList.getPlanningEntries()) {
			if (planningEntry.isSubmitted()) {
				Map<PlanningCost, BudgetCost> budgetCosts = new HashMap<PlanningCost, BudgetCost>();
				for (PlanningCost planningCost : planningEntry.getPlanningCosts()) {
					NormalizedDataElementValue value = valueService.getDataElementValue(planningCost.getDataElement(), location, type.getPeriod());
					if (!value.getValue().isNull()) {
						if (!value.getValue().getListValue().get(planningEntry.getLineNumber()).isNull())
							budgetCosts.put(planningCost, new BudgetCost(planningEntry, planningCost, value));
					}
				}
				planningEntryBudgets.add(new PlanningEntryBudget(planningEntry, budgetCosts));
			}
		}
		
		return new PlanningTypeBudget(type, planningList, planningEntryBudgets);
	}
	
	public void setFormValidationService(FormValidationService formValidationService) {
		this.formValidationService = formValidationService;
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

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
	public void setFormElementService(FormElementService formElementService) {
		this.formElementService = formElementService;
	}
	
}
