package org.chai.kevin.reports;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.chai.kevin.LanguageService;
import org.chai.kevin.LocationService;
import org.chai.kevin.LocationSorter;
import org.chai.kevin.data.DataService;
import org.chai.kevin.location.CalculationEntity;
import org.chai.kevin.location.LocationEntity;
import org.chai.kevin.location.LocationLevel;
import org.chai.kevin.value.ValueService;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.transaction.annotation.Transactional;

@SuppressWarnings("unused")
@Transactional(readOnly=true)
public class ReportService {
	
//	private static final Log log = LogFactory.getLog(ReportService.class);
	
	private LocationService locationService;
	private ValueService valueService;
	private DataService dataService;
	private LanguageService languageService;
	private String groupLevel;
	private SessionFactory sessionFactory;
	
	
	public <T extends CalculationEntity> Map<LocationEntity, List<T>> getParents(List<T> entities, LocationLevel level) {									
		
		Map<LocationEntity, List<T>> locationMap = new HashMap<LocationEntity, List<T>>();
		
		for (T entity : entities){			
			LocationEntity parentLocation = locationService.getParentOfLevel(entity, level);
			if(!locationMap.containsKey(parentLocation)) locationMap.put(parentLocation, new ArrayList<T>());
			locationMap.get(parentLocation).add(entity);
		}
				
		//sort location map keys
		List<LocationEntity> sortedEntities = new ArrayList<LocationEntity>(locationMap.keySet());
		Collections.sort(sortedEntities, LocationSorter.BY_NAME(languageService.getCurrentLanguage()));
		
		//sort location map values
		Map<LocationEntity, List<T>> sortedEntitiesMap = new LinkedHashMap<LocationEntity, List<T>>();		
		for (LocationEntity entity : sortedEntities){
			List<T> sortedList = locationMap.get(entity);
			Collections.sort(sortedList, LocationSorter.BY_NAME(languageService.getCurrentLanguage()));
			sortedEntitiesMap.put(entity, sortedList);
		}
		
		return sortedEntitiesMap;
	}
	
	public ReportObjective getRootObjective() {
		return (ReportObjective)sessionFactory.getCurrentSession().createCriteria(ReportObjective.class)
			.add(Restrictions.isNull("parent")).uniqueResult();
	}

	public <T> List<T> getReportTargets(Class<T> clazz, ReportObjective objective) {
		return (List<T>)sessionFactory.getCurrentSession()
			.createCriteria(clazz)
			.add(Restrictions.eq("objective", objective))
			.list();
	}
	
	public void setLocationService(LocationService locationService) {
		this.locationService = locationService;
	}
	
	public void setValueService(ValueService valueService) {
		this.valueService = valueService;
	}
	
	public void setDataService(DataService dataService) {
		this.dataService = dataService;
	}
	
	public void setLanguageService(LanguageService languageService) {
		this.languageService = languageService;
	}
	
	public void setGroupLevel(String groupLevel) {
		this.groupLevel = groupLevel;
	}
	
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
}
