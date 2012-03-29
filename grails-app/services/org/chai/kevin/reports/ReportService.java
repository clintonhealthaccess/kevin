
package org.chai.kevin.reports;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.chai.kevin.LanguageService;
import org.chai.kevin.LocationService;
import org.chai.kevin.LocationSorter;
import org.chai.kevin.dashboard.DashboardTarget;
import org.chai.kevin.data.DataService;
import org.chai.kevin.location.CalculationLocation;
import org.chai.kevin.location.Location;
import org.chai.kevin.location.LocationLevel;
import org.chai.kevin.value.ValueService;
import org.hibernate.Criteria;
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
	private SessionFactory sessionFactory;
	private Set<String> skipLevels;
	
	public <T extends CalculationLocation> Map<Location, List<T>> getParents(List<T> locations, LocationLevel level) {									
		
		Map<Location, List<T>> locationMap = new HashMap<Location, List<T>>();
		
		for (T location : locations){			
			Location parentLocation = locationService.getParentOfLevel(location, level);
			if(!locationMap.containsKey(parentLocation)) locationMap.put(parentLocation, new ArrayList<T>());
			locationMap.get(parentLocation).add(location);
		}
				
		//sort location map keys
		List<Location> sortedLocations = new ArrayList<Location>(locationMap.keySet());
		Collections.sort(sortedLocations, LocationSorter.BY_NAME(languageService.getCurrentLanguage()));
		
		//sort location map values
		Map<Location, List<T>> sortedLocationsMap = new LinkedHashMap<Location, List<T>>();		
		for (Location location : sortedLocations){
			List<T> sortedList = locationMap.get(location);
			Collections.sort(sortedList, LocationSorter.BY_NAME(languageService.getCurrentLanguage()));
			sortedLocationsMap.put(location, sortedList);
		}
		
		return sortedLocationsMap;
	}
	
	public ReportProgram getRootProgram() {
		ReportProgram program = (ReportProgram)sessionFactory.getCurrentSession().createCriteria(ReportProgram.class)
			.add(Restrictions.isNull("parent")).setCacheable(true).uniqueResult();
		return program;
	}

	public <T extends ReportTarget> List<ReportProgram> getProgramTree(Class<T> clazz){
		List<ReportProgram> programTree = new ArrayList<ReportProgram>();		
		List<T> targets = getReportTargets(clazz, null);		
		for(ReportTarget target : targets){
			programTree.add(target.getProgram());			
			ReportProgram parent = target.getProgram().getParent();
			while(parent != null){
				if(!programTree.contains(parent)) programTree.add(parent);
				parent = parent.getParent();
			}
		}
		return programTree;
	}
	
	// TODO check this
	public <T extends ReportTarget> List<T> getReportTargets(Class<T> clazz, ReportProgram program) {
		Criteria criteria = sessionFactory.getCurrentSession()
			.createCriteria(clazz)
			.setCacheable(true);
		if(program != null) criteria.add(Restrictions.eq("program", program));
		return (List<T>)criteria.list();			
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
	
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
	public void setSkipLevels(Set<String> skipLevels) {
		this.skipLevels = skipLevels;
	}
	
	public Set<LocationLevel> getSkipLocationLevels(Set<String> skipLevels) {
		Set<String> allSkipLevels = new HashSet<String>();
		if (skipLevels != null) allSkipLevels.addAll(skipLevels);
		allSkipLevels.addAll(this.skipLevels);
		
		Set<LocationLevel> levels = new HashSet<LocationLevel>();
		for (String skipLevel : allSkipLevels) {
			levels.add(locationService.findLocationLevelByCode(skipLevel));
		}
		return levels;
	}
}
