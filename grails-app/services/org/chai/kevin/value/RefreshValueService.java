package org.chai.kevin.value;

import grails.plugin.springcache.annotations.CacheFlush;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.chai.kevin.Period;
import org.chai.kevin.PeriodService;
import org.chai.kevin.data.Calculation;
import org.chai.kevin.data.Data;
import org.chai.kevin.data.DataElement;
import org.chai.kevin.data.DataService;
import org.chai.kevin.data.NormalizedDataElement;
import org.chai.kevin.data.RawDataElement;
import org.chai.location.CalculationLocation;
import org.chai.location.DataLocation;
import org.chai.location.DataLocationType;
import org.chai.location.LocationService;
import org.chai.task.Progress;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

public class RefreshValueService {

	private final static Log log = LogFactory.getLog(RefreshValueService.class);
	
	private PeriodService periodService;
	private LocationService locationService;
	private DataService dataService;
	private SessionFactory sessionFactory;
	private PlatformTransactionManager transactionManager;
	private ExpressionService expressionService;
	private ValueService valueService;
	
	private TransactionTemplate transactionTemplate;
	
	private TransactionTemplate getTransactionTemplate() {
		if (transactionTemplate == null) {
			transactionTemplate = new TransactionTemplate(transactionManager);
			transactionTemplate.setPropagationBehavior(TransactionTemplate.PROPAGATION_REQUIRES_NEW);
		}
		return transactionTemplate;
	}
	
	@Transactional(readOnly = true)
	public List<NormalizedDataElement> refreshNormalizedDataElement(NormalizedDataElement normalizedDataElement, Progress progress) {
		// set progress maximum - we count the number of NormalizedDataElement dependencies
		Set<Data> dependencySet = new HashSet<Data>();
		collectOrderedDependencies(normalizedDataElement, dependencySet, NormalizedDataElement.class);
		removeNulls(dependencySet);
		progress.setMaximum(dependencySet.size() * periodService.listPeriods().size() * countLocations(DataLocation.class));
		
		List<NormalizedDataElement> uptodateElements = refreshNormalizedDataElementWithoutSettingProgress(normalizedDataElement, progress);
		return uptodateElements;
	}

	private List<NormalizedDataElement> refreshNormalizedDataElementWithoutSettingProgress(NormalizedDataElement normalizedDataElement, Progress progress) {
		// run the actual expression calculations
		List<NormalizedDataElement> uptodateElements = new ArrayList<NormalizedDataElement>();
		refreshDataElement(normalizedDataElement, uptodateElements, progress);
		return uptodateElements;
	}
	
	private static List<?> NULL_LIST = new ArrayList();
	
	static{
		NULL_LIST.add(null);
	}
	
	private void removeNulls(Collection<?> list) {
		list.removeAll(NULL_LIST);
	}
	
	private Date refreshDataElement(DataElement dataElement, List<NormalizedDataElement> uptodateElements, Progress progress) {
		if (log.isDebugEnabled()) log.debug("refreshDataElement(dataElement="+dataElement+", uptodateElements="+uptodateElements+", progress)");
		
		if (dataElement instanceof RawDataElement) return dataElement.getLastValueChanged();
		else {
			NormalizedDataElement normalizedDataElement = (NormalizedDataElement)dataElement;
			Date latestDependency = null;
			
			Set<DataElement> dependencies = new HashSet<DataElement>();
			for (String expression : normalizedDataElement.getExpressions()) {
				dependencies.addAll(expressionService.getDataInExpression(expression, DataElement.class).values());
			}
			removeNulls(dependencies);
			
			uptodateElements.add(normalizedDataElement);
			for (DataElement dependency : dependencies) {
				if (!uptodateElements.contains(dependency)) {
					Date dependencyDate = refreshDataElement(dependency, uptodateElements, progress);
					if (latestDependency == null || (dependencyDate != null && dependencyDate.after(latestDependency))) latestDependency = dependencyDate;
				}
			}
			
			// we refresh if the data element was changed after the last refresh
			if (normalizedDataElement.getRefreshed() == null || normalizedDataElement.getTimestamp().after(normalizedDataElement.getRefreshed())) {
				refreshNormalizedDataElementOnly(normalizedDataElement, progress);
			}
			
			// we refresh if a value was saved after the last refresh
			else if (normalizedDataElement.getRefreshed() == null || normalizedDataElement.getLastValueChanged().after(normalizedDataElement.getRefreshed())) {
				refreshNormalizedDataElementOnly(normalizedDataElement, progress);
			}
			
			// we refresh if the last value of the dependency is after the last refreshed date of this element
			// this means some values of the dependency were changed after us
			else if (latestDependency != null && latestDependency.after(normalizedDataElement.getRefreshed())) {
				refreshNormalizedDataElementOnly(normalizedDataElement, progress);
			}
			
			else {
				progress.incrementProgress(periodService.listPeriods().size() * countLocations(DataLocation.class));
			}
			
			return normalizedDataElement.getLastValueChanged();
		}
	}
	
	@Transactional(readOnly = false)
	public void refreshNormalizedDataElement(NormalizedDataElement dataElement, DataLocation dataLocation, Period period) {
		refreshDataElement(dataElement, dataLocation, period, new ArrayList<NormalizedDataElement>());
		dataService.save(dataElement);
	}
	
	private Date refreshDataElement(DataElement dataElement, DataLocation dataLocation, Period period, List<NormalizedDataElement> uptodateElements) {
		DataValue storedValue = valueService.getDataElementValue(dataElement, dataLocation, period);
		
		if (dataElement instanceof RawDataElement) {
			if (storedValue == null) return dataElement.getTimestamp();
			return storedValue.getTimestamp();
		}
		else {
			NormalizedDataElement normalizedDataElement = (NormalizedDataElement)dataElement;
			Date latestDependency = null;
			
			Set<DataElement> dependencies = new HashSet<DataElement>();
			for (String expression : normalizedDataElement.getExpressions()) {
				dependencies.addAll(expressionService.getDataInExpression(expression, DataElement.class).values());
			}
			removeNulls(dependencies);
			
			uptodateElements.add(normalizedDataElement);
			for (DataElement dependency : dependencies) {
				if (!uptodateElements.contains(dependency)) {
					Date dependencyDate = refreshDataElement(dependency, dataLocation, period, uptodateElements);
					if (latestDependency == null || (dependencyDate != null && dependencyDate.after(latestDependency))) latestDependency = dependencyDate;
				}
			}
			
			// we refresh if the value does not exist
			if (storedValue == null) {
				storedValue = updateNormalizedDataElementValue(normalizedDataElement, dataLocation, period);
			}
			
			// we refresh if the timestamp of the dataElement is bigger than that of the value
			else if (dataElement.getTimestamp().after(storedValue.getTimestamp())) {
				storedValue = updateNormalizedDataElementValue(normalizedDataElement, dataLocation, period);
			}
			
			// we refresh if the timestamp of the dependency is after the timestamp of this value
			else if (latestDependency != null && latestDependency.after(storedValue.getTimestamp())) {
				storedValue = updateNormalizedDataElementValue(normalizedDataElement, dataLocation, period);
			}
			
			return storedValue.getTimestamp();
		}
	}
	
	public NormalizedDataElementValue updateNormalizedDataElementValue(NormalizedDataElement normalizedDataElement, DataLocation dataLocation, Period period) {
		NormalizedDataElementValue newValue = expressionService.calculateValue(normalizedDataElement, dataLocation, period);
		NormalizedDataElementValue oldValue = valueService.getDataElementValue(normalizedDataElement, dataLocation, period);
		
		if (log.isDebugEnabled()) log.debug("updating NDE for: "+dataLocation+", new value: "+newValue);
		
		if (oldValue == null) oldValue = newValue;
		else {
			oldValue.setValue(newValue.getValue());
			oldValue.setStatus(newValue.getStatus());
		}
		
		valueService.save(oldValue);
		normalizedDataElement.setLastValueChanged(new Date());
		
		sessionFactory.getCurrentSession().evict(oldValue);
		return oldValue;
	}
	
	private void refreshNormalizedDataElementOnly(final NormalizedDataElement normalizedDataElement, final Progress progress) {
		if (log.isDebugEnabled()) log.debug("refreshNormalizedDataElement(normalizedDataElement="+normalizedDataElement+")");
		
		getTransactionTemplate().execute(new TransactionCallbackWithoutResult() {
			@Override
			protected void doInTransactionWithoutResult(TransactionStatus arg0) {
				NormalizedDataElement newNormalizedDataElement = dataService.getData(normalizedDataElement.getId(), NormalizedDataElement.class);
				
				for (Iterator<Object[]> iterator = getCombinations(DataLocation.class); iterator.hasNext();) {
					Object[] row = (Object[]) iterator.next();
					DataLocation dataLocation = (DataLocation)row[0];
					Period period = (Period)row[1];
					
					// TODO improve performance by getting all values at the same time
					updateNormalizedDataElementValue(newNormalizedDataElement, dataLocation, period);
					progress.incrementProgress();
				}
				
				updateSources(newNormalizedDataElement);
				newNormalizedDataElement.setRefreshed(new Date());
				dataService.save(newNormalizedDataElement);
			}
		});
		
		sessionFactory.getCurrentSession().clear();
	}
	
	private void updateSources(Data<?> data) {
		Map<String, Map<String, List<String>>> originalSourceMap = new HashMap<String, Map<String, List<String>>>();
		for (Period period : periodService.listPeriods()) {
			if (!originalSourceMap.containsKey(period.getId()+"")) {
				originalSourceMap.put(period.getId()+"", new HashMap<String, List<String>>());
			}
			
			Map<String, List<String>> jsonMap = new HashMap<String, List<String>>(originalSourceMap.get(period.getId()+""));
			for (DataLocationType type : locationService.listTypes()) {
				List<Data<?>> dependencies = new ArrayList<Data<?>>();
				if (data instanceof NormalizedDataElement) {
					NormalizedDataElement normalizedDataElement = (NormalizedDataElement)data;
					Map<String, ? extends Data> dependenciesMap = expressionService.getDataInExpression(normalizedDataElement.getExpression(period, type.getCode()), Data.class);
					dependencies.addAll((Collection<Data<?>>)dependenciesMap.values());
				}
				else if (data instanceof Calculation) {
					Calculation calculation = (Calculation)data;
					Map<String, ? extends Data> dependenciesMap = expressionService.getDataInExpression(calculation.getExpression(), Data.class);
					dependencies.addAll((Collection<Data<?>>)dependenciesMap.values());
				}
				
				for (Data<?> dependency : dependencies) {
					if (!jsonMap.containsKey(type.getCode())) {
						jsonMap.put(type.getCode(), new ArrayList<String>());
					}
					jsonMap.get(type.getCode()).addAll(dependency.getSources(period, type));
				}
			}
			
			originalSourceMap.put(period.getId()+"", jsonMap);
		}
		
		
		if (data instanceof NormalizedDataElement) {
			NormalizedDataElement normalizedDataElement = (NormalizedDataElement)data;
			normalizedDataElement.setSourceMap(originalSourceMap);
		}
		else if (data instanceof Calculation) {
			Calculation calculation = (Calculation)data;
			calculation.setSourceMap(originalSourceMap);
		}
	}
	
	private void collectOrderedDependencies(Data data, Collection<Data> dependencies, Class<? extends Data> clazz) {
		if (dependencies.contains(data)) return;
		
		dependencies.add(data);
		if (data instanceof NormalizedDataElement) {
			NormalizedDataElement normalizedDataElement = (NormalizedDataElement)data;
			for (String expression : normalizedDataElement.getExpressions()) {
				Map<String, ? extends Data> dependenciesMap = expressionService.getDataInExpression(expression, clazz);
				for (Data dependency : dependenciesMap.values()) {
					if (dependency != null) collectOrderedDependencies(dependency, dependencies, clazz);
				}
			}
		}
		if (data instanceof Calculation) {
			Calculation calculation = (Calculation)data;
			Map<String, ? extends Data> dependenciesMap = expressionService.getDataInExpression(calculation.getExpression(), clazz);
			for (Data dependency : dependenciesMap.values()) {
				if (dependency != null) collectOrderedDependencies(dependency, dependencies, clazz);
			}
		}
	}
	
	@Transactional(readOnly = true)
	public void refreshAll(final Progress progress) {
		if (log.isDebugEnabled()) log.debug("refreshAll(progress)");
		
		List<Calculation<?>> calculations = sessionFactory.getCurrentSession().createCriteria(Calculation.class).list();
		if (log.isDebugEnabled()) log.debug("calculation size: " + calculations.size());
		
		List<Data> calculationDependencySet = new ArrayList<Data>();
		for (Calculation<?> calculation : calculations) {
			Set<Data> calculationDependencies = new HashSet<Data>();
			collectOrderedDependencies(calculation, calculationDependencies, NormalizedDataElement.class);
			calculationDependencySet.addAll(calculationDependencies);
			calculationDependencySet.remove(calculation);
		}
		removeNulls(calculationDependencySet);
		if (log.isDebugEnabled()) log.debug("dependencies of calculation size: "+calculationDependencySet.size());
		
		List<NormalizedDataElement> normalizedDataElements = sessionFactory.getCurrentSession().createCriteria(NormalizedDataElement.class).list();
		if (log.isDebugEnabled()) log.debug("normalized data element size: " + normalizedDataElements.size());
		
		List<Data> dataElementDependencySet = new ArrayList<Data>();
		for (NormalizedDataElement dataElement : normalizedDataElements) {
			Set<Data> dataElementDependencies = new HashSet<Data>();
			collectOrderedDependencies(dataElement, dataElementDependencies, NormalizedDataElement.class);
			dataElementDependencySet.addAll(dataElementDependencies);
		}
		removeNulls(dataElementDependencySet);
		if (log.isDebugEnabled()) log.debug("dependencies of normalized data elements size: " + dataElementDependencySet.size());
	
		progress.setMaximum(
			// all dependencies of NDEs
			(dataElementDependencySet.size() * periodService.listPeriods().size() * countLocations(DataLocation.class)) +
			// all dependent NDEs of calculations
			(calculationDependencySet.size() * periodService.listPeriods().size() * countLocations(DataLocation.class)) +
			// all calculations
			(periodService.listPeriods().size() * countLocations(CalculationLocation.class)) * calculations.size()
		);
		
		// refresh normalized data elements
		while (!normalizedDataElements.isEmpty()) {
			final NormalizedDataElement normalizedDataElement = normalizedDataElements.get(0);
			
			List<NormalizedDataElement> uptodateElements = refreshNormalizedDataElementWithoutSettingProgress(normalizedDataElement, progress);
			normalizedDataElements.remove(normalizedDataElement);
		}
		
		// refresh all calculations
		for (Calculation<?> calculation : calculations) {
			refreshCalculationWithoutSettingProgress(calculation, progress);
		}
	}
	
	@Transactional(readOnly = true)
	public void refreshCalculation(Calculation<?> calculation, Progress progress) {
		// set progress maximum - we count the number of NormalizedDataElement dependencies
		Set<Data> dependencySet = new HashSet<Data>();
		collectOrderedDependencies(calculation, dependencySet, NormalizedDataElement.class);
		dependencySet.remove(calculation);
		removeNulls(dependencySet);
		progress.setMaximum(
			// all dependent NDEs
			(dependencySet.size() * periodService.listPeriods().size() * countLocations(DataLocation.class)) +
			// the calculation itself
			(periodService.listPeriods().size() * countLocations(CalculationLocation.class))
		);
		
		refreshCalculationWithoutSettingProgress(calculation, progress);
	}
	
	private void refreshCalculationWithoutSettingProgress(Calculation<?> calculation, Progress progress) {
		
		// refresh the calculation
		Map<String, DataElement> dependenciesMap = expressionService.getDataInExpression(calculation.getExpression(), DataElement.class);
		
		Date latestDependency = null;
		List<NormalizedDataElement> uptodateElements = new ArrayList<NormalizedDataElement>(); 
		for (DataElement<?> dependency : dependenciesMap.values()) {
			if (!uptodateElements.contains(dependency)) {
				Date dependencyDate = refreshDataElement(dependency, uptodateElements, progress);
				if (latestDependency == null || (dependencyDate != null && dependencyDate.after(latestDependency))) latestDependency = dependencyDate;
			}
		}
		
		// we refresh if the data element was changed after the last refresh
		if (calculation.getRefreshed() == null || calculation.getTimestamp().after(calculation.getRefreshed())) {
			refreshCalculationOnly(calculation, progress);
		}
		
		// we refresh if a value was saved after the last refresh
		else if (calculation.getRefreshed() == null || calculation.getLastValueChanged().after(calculation.getRefreshed())) {
			refreshCalculationOnly(calculation, progress);
		}
		
		// we refresh if the last value of the dependency is after the last refreshed date of this element
		// this means some values of the dependency were changed after us
		else if (latestDependency != null && latestDependency.after(calculation.getRefreshed())) {
			refreshCalculationOnly(calculation, progress);
		}
		
		else {
			progress.incrementProgress(periodService.listPeriods().size() * countLocations(CalculationLocation.class));
		}
	}
	
	public void updateCalculationPartialValues(Calculation<?> calculation, CalculationLocation location, Period period) {
		valueService.deleteValues(calculation, location, period);
		
		for (CalculationPartialValue newPartialValue : expressionService.calculatePartialValues(calculation, location, period)) {
			valueService.save(newPartialValue);
			sessionFactory.getCurrentSession().evict(newPartialValue);
		}
		calculation.setLastValueChanged(new Date());
	}
	
	private void refreshCalculationOnly(final Calculation<?> calculation, final Progress progress) {
		if (log.isDebugEnabled()) log.debug("refreshCalculation(calculation="+calculation+")");
		
		getTransactionTemplate().execute(new TransactionCallbackWithoutResult() {
			@Override
			protected void doInTransactionWithoutResult(TransactionStatus arg0) {
				Calculation<?> newCalculation = dataService.getData(calculation.getId(), Calculation.class);
				
				for (Iterator<Object[]> iterator = getCombinations(CalculationLocation.class); iterator.hasNext();) {
					Object[] row = (Object[]) iterator.next();
					CalculationLocation location = (CalculationLocation)row[0];
					Period period = (Period)row[1];
					
					// TODO improve performance by getting all values at the same time
					updateCalculationPartialValues(newCalculation, location, period);
					progress.incrementProgress();
				}
				
				updateSources(newCalculation);
				newCalculation.setRefreshed(new Date());
				dataService.save(newCalculation);
			}
		});
		
		sessionFactory.getCurrentSession().clear();
	}

	@CacheFlush(caches={"dsrCache", "dashboardCache", "fctCache"})
	public void flushCaches() { }
	
	// TODO move to location service ?
	private <T extends CalculationLocation> Iterator<Object[]> getCombinations(Class<T> clazz) {
		Query query = sessionFactory.getCurrentSession().createQuery(
			"select location, period from "+clazz.getSimpleName()+" location, Period period"
		).setCacheable(true).setReadOnly(true);
		return query.iterate();
	}
	
	// TODO move to location service ?
	private <T extends CalculationLocation> Long countLocations(Class<T> clazz) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(clazz);
		criteria.setCacheable(true).setReadOnly(true);
		return (Long)criteria.setProjection(Projections.rowCount()).uniqueResult();
	}
	
	public void setPeriodService(PeriodService periodService) {
		this.periodService = periodService;
	}
	
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
	public void setExpressionService(ExpressionService expressionService) {
		this.expressionService = expressionService;
	}
	
	public void setValueService(ValueService valueService) {
		this.valueService = valueService;
	}
	
	public void setDataService(DataService dataService) {
		this.dataService = dataService;
	}
	
	public void setLocationService(LocationService locationService) {
		this.locationService = locationService;
	}
	
	public void setTransactionManager(PlatformTransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}
	
}
