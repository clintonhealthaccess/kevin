
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
import org.chai.kevin.data.Data;
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
	
	private static final Log log = LogFactory.getLog(ReportService.class);
	
	private LocationService locationService;
	private ValueService valueService;
	private DataService dataService;
	private LanguageService languageService;
	private SessionFactory sessionFactory;
	private Set<String> skipLevels;
	
	public ReportProgram getRootProgram() {
		ReportProgram program = (ReportProgram)sessionFactory.getCurrentSession().createCriteria(ReportProgram.class)
			.add(Restrictions.isNull("parent")).setCacheable(true).uniqueResult();
		return program;
	}
	
	public <T extends ReportTarget> List<T> collectReportTargets(Class<T> clazz, ReportProgram program) {
		List<T> result = new ArrayList<T>();
		collectReportTree(clazz, program, null, result);
		return result;
	}
	
	public <T extends ReportTarget> List<ReportProgram> collectReportProgramTree(Class<T> clazz, ReportProgram program) {
		List<ReportProgram> result = new ArrayList<ReportProgram>();
		collectReportTree(clazz, program, result, null);
		return result;
	}
	
	public <T extends ReportTarget> boolean collectReportTree(Class<T> clazz, ReportProgram program, List<ReportProgram> collectedPrograms, List<T> collectedTargets) {
		boolean hasTargets = false;
		for (ReportProgram child : program.getChildren()) {
			hasTargets = hasTargets | collectReportTree(clazz, child, collectedPrograms, collectedTargets);
		}
		
		//report target tree list
		List<T> targets = getReportTargets(clazz, program);
		if (log.isDebugEnabled()) log.debug("collectReportTree(program="+program+",targets="+targets+")");
		if(!targets.isEmpty()){
			hasTargets = true;
			if (collectedTargets != null) collectedTargets.addAll(targets);	
		}
		
		//report program tree list
		if (hasTargets && collectedPrograms != null) collectedPrograms.add(program);
		
		return hasTargets;
	}
	
	public List<AbstractReportTarget> getReportTargets(Data<?> data) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(AbstractReportTarget.class);
		criteria.add(Restrictions.eq("data", data));
		return (List<AbstractReportTarget>)criteria.list();
	}
	
	// TODO check this
	public <T extends ReportTarget> List<T> getReportTargets(Class<T> clazz, ReportProgram program) {
		Criteria criteria = sessionFactory.getCurrentSession()
			.createCriteria(clazz)
			.setCacheable(true);
		if(program != null) criteria.add(Restrictions.eq("program", program));
		List<T> targets = (List<T>)criteria.list();
		if (log.isDebugEnabled()) log.debug("collectReportTree(program="+program+",targets="+targets+")");
		return targets;			
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
	
	public Set<LocationLevel> getSkipReportLevels(Set<String> skipLevels) {
		Set<String> allSkipLevels = new HashSet<String>();
		
		//add report-specific skip levels
		if (skipLevels != null) allSkipLevels.addAll(skipLevels);
		//add report-generic skip levels
		allSkipLevels.addAll(this.skipLevels);
		
		Set<LocationLevel> levels = new HashSet<LocationLevel>();
		for (String skipLevel : allSkipLevels) {
			levels.add(locationService.findLocationLevelByCode(skipLevel));
		}
		return levels;
	}
}
