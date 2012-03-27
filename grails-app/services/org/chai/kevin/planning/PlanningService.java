package org.chai.kevin.planning;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.chai.kevin.data.DataService;
import org.chai.kevin.data.Enum;
import org.chai.kevin.data.Type;
import org.chai.kevin.data.Type.ValueType;
import org.chai.kevin.form.FormElement;
import org.chai.kevin.form.FormElement.ElementCalculator;
import org.chai.kevin.form.FormElement.ElementSubmitter;
import org.chai.kevin.form.FormElementService;
import org.chai.kevin.form.FormEnteredValue;
import org.chai.kevin.form.FormValidationService;
import org.chai.kevin.form.FormValidationService.ValidatableLocator;
import org.chai.kevin.location.DataLocation;
import org.chai.kevin.location.Location;
import org.chai.kevin.value.NormalizedDataElementValue;
import org.chai.kevin.value.RawDataElementValue;
import org.chai.kevin.value.RefreshValueService;
import org.chai.kevin.value.ValidatableValue;
import org.chai.kevin.value.Value;
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
	public PlanningSummaryPage getSummaryPage(Planning planning, Location location) {
		List<DataLocation> dataLocations = location.collectDataLocations(null, null);
		Map<PlanningType, PlanningTypeSummary> summaries = new HashMap<PlanningType, PlanningTypeSummary>();
		for (PlanningType planningType : planning.getPlanningTypes()) {
			summaries.put(planningType, getPlanningTypeSummary(planningType, dataLocations));
		}
		
		return new PlanningSummaryPage(planning.getPlanningTypes(), dataLocations, summaries);
	}
	
	// TODO move to planning type
	private PlanningTypeSummary getPlanningTypeSummary(PlanningType planningType, List<DataLocation> dataLocations) {
		Map<DataLocation, Integer> numberOfEntries = new HashMap<DataLocation, Integer>();
		for (DataLocation dataLocation : dataLocations) {
			numberOfEntries.put(dataLocation, getPlanningList(planningType, dataLocation).getPlanningEntries().size());
		}
		return new PlanningTypeSummary(planningType, numberOfEntries);
	}
	
	@Transactional(readOnly=false)
	public PlanningEntry getOrCreatePlanningEntry(PlanningType type, DataLocation location, Integer lineNumber) {
		PlanningList planningList = getPlanningList(type, location);
		PlanningEntry entry = planningList.getOrCreatePlanningEntry(lineNumber);
		formElementService.save(planningList.getFormEnteredValue());
		return entry;
	}
	
	@Transactional(readOnly=true)
	public PlanningList getPlanningList(PlanningType type, DataLocation location) {
		FormEnteredValue formEnteredValue = formElementService.getOrCreateFormEnteredValue(location, type.getFormElement());
		RawDataElementValue rawDataElementValue = valueService.getDataElementValue(type.getFormElement().getDataElement(), location, type.getPeriod());
		if (rawDataElementValue == null) {
			rawDataElementValue = new RawDataElementValue(type.getFormElement().getDataElement(), location, type.getPeriod(), Value.NULL_INSTANCE());
		}
		Map<PlanningCost, NormalizedDataElementValue> costValues = new HashMap<PlanningCost, NormalizedDataElementValue>();
		for (PlanningCost planningCost : type.getCosts()) {
			costValues.put(planningCost, valueService.getDataElementValue(planningCost.getDataElement(), location, type.getPeriod()));
		}
		
		return new PlanningList(type, location, formEnteredValue, rawDataElementValue, costValues, getEnums(type));
	}
	
	private Map<String, Enum> getEnums(PlanningType type) {
		Map<String, Enum> result = new HashMap<String, Enum>();
		for (Entry<String, Type> prefix : type.getFormElement().getDataElement().getEnumPrefixes().entrySet()) {
			Enum enume = dataService.findEnumByCode(prefix.getValue().getEnumCode());
			result.put(prefix.getValue().getEnumCode(), enume);
		}
		return result;
	}
	
	@Transactional(readOnly=false)
	public void deletePlanningEntry(PlanningType type, DataLocation location, Integer lineNumber) {
		PlanningList planningList = getPlanningList(type, location);
		PlanningEntry planningEntry = planningList.getPlanningEntries().get(lineNumber);
		planningEntry.delete();
		formElementService.save(planningList.getFormEnteredValue());
	}
	
	private ValidatableLocator getLocator() {
		return new ValidatableLocator() {
			@Override
			public ValidatableValue getValidatable(Long id, DataLocation location) {
				FormElement element = formElementService.getFormElement(id);
				FormEnteredValue enteredValue = formElementService.getOrCreateFormEnteredValue(location, element);
				if (enteredValue == null) return null;
				return enteredValue.getValidatable();
			}
		};
	}
	
	@Transactional(readOnly=false)
	public PlanningEntry modify(PlanningType type, DataLocation location, Integer lineNumber, Map<String, Object> params) {
		PlanningList planningList = getPlanningList(type, location);
		PlanningEntry planningEntry = planningList.getOrCreatePlanningEntry(lineNumber);
		
		// first we merge the values to create a new value
		planningEntry.mergeValues(params);
		planningEntry.setBudgetUpdated(false);
		formElementService.save(planningList.getFormEnteredValue());
		
		// second we run the validation/skip rules
		List<FormEnteredValue> affectedValues = new ArrayList<FormEnteredValue>();
		ElementCalculator elementCalculator = new ElementCalculator(affectedValues, formValidationService, formElementService, getLocator());
		planningEntry.evaluateRules(elementCalculator);
		
		// last we set and save the value
		for (FormEnteredValue formEnteredValue : affectedValues) {
			formElementService.save(formEnteredValue);
		}
		return planningEntry;
	}
	
	@Transactional(readOnly=false)
	public void submit(PlanningType type, DataLocation location, Integer lineNumber) {
		PlanningList planningList = getPlanningList(type, location);
		PlanningEntry planningEntry = planningList.getOrCreatePlanningEntry(lineNumber);
		
		// we submit the entry
		planningEntry.setSubmitted(true);

		// we refresh the corresponding raw data element
		ElementSubmitter submitter = new PlanningElementSubmitter(formElementService, valueService);
		type.getFormElement().submit(location, type.getPeriod(), submitter);
		
		// then we recalculate the budget
		refreshBudget(planningEntry, location);
		
		// last we save the value
		formElementService.save(planningList.getFormEnteredValue());
	}
	
	@Transactional(readOnly=false)
	public void unsubmit(PlanningType type, DataLocation location, Integer lineNumber) {
		PlanningList planningList = getPlanningList(type, location);
		PlanningEntry planningEntry = planningList.getOrCreatePlanningEntry(lineNumber);
		
		// we submit the entry
		planningEntry.setSubmitted(false);

		// we refresh the corresponding raw data element
		ElementSubmitter submitter = new PlanningElementSubmitter(formElementService, valueService);
		type.getFormElement().submit(location, type.getPeriod(), submitter);
		
		// then we recalculate the budget
		refreshBudget(planningEntry, location);
				
		// last we save the value
		formElementService.save(planningList.getFormEnteredValue());
	}
	
	public static class PlanningElementSubmitter extends ElementSubmitter {

		private PlanningType planningType;
		
		public PlanningElementSubmitter(FormElementService formElementService, ValueService valueService) {
			super(formElementService, valueService);
		}

		public boolean transformValue(Value currentValue, Type currentType, String currentPrefix) {
			boolean modified = false;
			
			currentValue.setAttribute(PlanningEntry.BUDGET_UPDATED, null);
			currentValue.setAttribute(PlanningEntry.SUBMITTED, null);

//			if (currentType.getType() == ValueType.LIST) {
//				for (Value value : currentValue.getListValue()) {
//					if (value.getAttribute(PlanningEntry.SUBMITTED) == "false") {
//
//					}
//				}
//				 currentValue.setJsonValue(jsonValue);
//			}
			
			return modified | super.transformValue(currentValue, currentType, currentPrefix);
		}
		
	}
	
	@Transactional(readOnly=false)
	public void refreshBudget(PlanningType type, DataLocation location) {
		ElementSubmitter submitter = new PlanningElementSubmitter(formElementService, valueService);
		type.getFormElement().submit(location, type.getPeriod(), submitter);
		
		PlanningList planningList = getPlanningList(type, location);
		for (PlanningEntry planningEntry : planningList.getPlanningEntries()) {
			refreshBudget(planningEntry, location);
		}
		formElementService.save(planningList.getFormEnteredValue());
	}
	
	private void refreshBudget(PlanningEntry planningEntry, DataLocation location) {
		if (planningEntry.isSubmitted() && !planningEntry.isBudgetUpdated()) {
			for (PlanningCost cost : planningEntry.getPlanningCosts()) {
				refreshValueService.refreshNormalizedDataElement(cost.getDataElement(), location, cost.getPlanningType().getPeriod());
			}
			planningEntry.setBudgetUpdated(true);
		}
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
