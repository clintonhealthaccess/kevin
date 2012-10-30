package org.chai.kevin.planning;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.chai.location.LocationService;
import org.chai.kevin.data.DataService;
import org.chai.kevin.data.Enum;
import org.chai.kevin.data.NormalizedDataElement;
import org.chai.kevin.data.Type;
import org.chai.kevin.form.FormElement;
import org.chai.kevin.form.FormElement.ElementCalculator;
import org.chai.kevin.form.FormElement.ElementSubmitter;
import org.chai.kevin.form.FormElementService;
import org.chai.kevin.form.FormEnteredValue;
import org.chai.kevin.form.FormValidationService;
import org.chai.kevin.form.FormValidationService.ValidatableLocator;
import org.chai.location.DataLocation;
import org.chai.location.DataLocationType;
import org.chai.location.Location;
import org.chai.kevin.value.NormalizedDataElementValue;
import org.chai.kevin.value.RawDataElementValue;
import org.chai.kevin.value.RefreshValueService;
import org.chai.kevin.value.StoredValue;
import org.chai.kevin.value.ValidatableValue;
import org.chai.kevin.value.Value;
import org.chai.kevin.value.ValueService;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.transaction.annotation.Transactional;

public class PlanningService {

	static final String SUBMITTED = "submitted";

	private LocationService locationService;
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
	
	private Set<DataLocationType> getDataLocationTypes(Planning planning) {
		Set<DataLocationType> result = new HashSet<DataLocationType>();
		for (String code : planning.getTypeCodes()) {
			result.add(locationService.findDataLocationTypeByCode(code));
		}
		return result;
	}
	
	@Transactional(readOnly=true)
	public PlanningSummaryPage getSummaryPage(Planning planning, Location location) {
		List<DataLocation> dataLocations = location.collectDataLocations(null, getDataLocationTypes(planning));
		Map<PlanningType, PlanningTypeSummary> summaries = new HashMap<PlanningType, PlanningTypeSummary>();
		for (PlanningType planningType : planning.getAllPlanningTypes()) {
			summaries.put(planningType, getPlanningTypeSummary(planningType, dataLocations));
		}
		
		return new PlanningSummaryPage(planning.getAllPlanningTypes(), dataLocations, summaries);
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
		for (PlanningCost planningCost : type.getAllCosts()) {
			costValues.put(planningCost, valueService.getDataElementValue(planningCost.getDataElement(), location, type.getPeriod()));
		}
		
		return new PlanningList(type, location, formEnteredValue, rawDataElementValue, costValues, getEnums(type));
	}
	
	@Transactional(readOnly=true)
	public PlanningOutputTable getPlanningOutputTable(PlanningOutput output, DataLocation location) {
		StoredValue value = (StoredValue)valueService.getDataElementValue(output.getDataElement(), location, output.getPlanning().getPeriod());
		return new PlanningOutputTable(output, value);
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
		planningEntry.getValidatable().setAttribute("", SUBMITTED, "false");
		formElementService.save(planningList.getFormEnteredValue());
		
		// second we run the validation/skip rules
		List<FormEnteredValue> affectedValues = new ArrayList<FormEnteredValue>();
		ElementCalculator elementCalculator = new ElementCalculator(affectedValues, formValidationService, formElementService, getLocator());
		planningEntry.evaluateRules(elementCalculator);
		
		// last we set and save the value
		for (FormEnteredValue formEnteredValue : affectedValues) {
			formEnteredValue.getValidatable().setAttribute("", SUBMITTED, "false");
			formElementService.save(formEnteredValue);
		}
		return planningEntry;
	}
	
	@Transactional(readOnly=false)
	public void submitIfNeeded(Planning planning, DataLocation location) {
		for (PlanningType planningType : planning.getAllPlanningTypes()) {
			PlanningList planningList = getPlanningList(planningType, location);
			if (!"true".equals(planningList.getFormEnteredValue().getValidatable().getValue().getAttribute(SUBMITTED))) { 
				// we refresh the corresponding raw data element
				ElementSubmitter submitter = new PlanningElementSubmitter(formElementService, valueService);
				planningType.getFormElement().submit(location, planning.getPeriod(), submitter);
	
				// set submitted true
				planningList.getFormEnteredValue().getValidatable().setAttribute("", SUBMITTED, "true");
				
				// last we save the value
				formElementService.save(planningList.getFormEnteredValue());
			}
		}
	}
	
	public static class PlanningElementSubmitter extends ElementSubmitter {

		private PlanningType planningType;
		
		public PlanningElementSubmitter(FormElementService formElementService, ValueService valueService) {
			super(formElementService, valueService);
		}

		public boolean transformValue(Value currentValue, Type currentType, String currentPrefix) {
			boolean modified = false;
			
			currentValue.setAttribute(SUBMITTED, null);

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
	public void refreshBudgetIfNeeded(Planning planning, DataLocation location) {
		for (PlanningCost cost : planning.getPlanningCosts()) {
			refreshValueService.refreshNormalizedDataElement(cost.getDataElement(), location, cost.getPlanningType().getPeriod());
		}
	}

	@Transactional(readOnly=false)
	public void refreshOutputTableIfNeeded(PlanningOutput planningOutput, DataLocation location) {
		if (planningOutput.getDataElement() instanceof NormalizedDataElement) refreshValueService.refreshNormalizedDataElement((NormalizedDataElement)planningOutput.getDataElement(), location, planningOutput.getPlanning().getPeriod());
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
	
	public void setLocationService(LocationService locationService) {
		this.locationService = locationService;
	}
	
}
