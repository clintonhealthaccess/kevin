
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
import org.chai.location.LocationService;
import org.chai.kevin.dashboard.DashboardTarget;
import org.chai.kevin.data.Data;
import org.chai.kevin.data.DataService;
import org.chai.location.CalculationLocation;
import org.chai.location.Location;
import org.chai.location.LocationLevel;
import org.chai.kevin.value.ValueService;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.transaction.annotation.Transactional;

import grails.plugin.springcache.annotations.CacheFlush;

public class ReportService {
	
	static transactional = true
	
	public enum ReportType {MAP, TABLE};
	
	def grailsApplication
	
	LocationService locationService;
	ValueService valueService;
	DataService dataService;
	LanguageService languageService;
	SessionFactory sessionFactory;
	Set<String> skipLevels;
	
	/**
	 * Returns the root of the program tree. The root is the only program that has no parent.
	 * 
	 * @return the root of the program tree
	 */
	public ReportProgram getRootProgram() {
		ReportProgram program = (ReportProgram)sessionFactory.getCurrentSession().createCriteria(ReportProgram.class)
			.add(Restrictions.isNull("parent")).setCacheable(true).uniqueResult();
		return program;
	}
	
	/**
	 * Returns the list of all report targets of the given class that are attached to the 
	 * given program either directly, or attached to a descendant of the program.
	 * 
	 * @param clazz the class of target to return
	 * @param program the program from which to start collecting targets
	 * @return a list of all report targets under the given program
	 */
	public <T extends ReportTarget> List<T> collectReportTargets(Class<T> clazz, ReportProgram program) {
		List<T> result = new ArrayList<T>();
		collectReportTree(clazz, program, null, result);
		return result;
	}
	
	/**
	 * Returns the list of all report programs under the given program that has target of the given class.
	 * Programs who don't have targets or whose children don't have targets of the given class are not returned.
	 *
	 * @param clazz the class of target
	 * @param program the program from which to start collecting targets
	 * @return a list of all report programs that have targets of the specified class under the given program
	 */
	public <T extends ReportTarget> List<ReportProgram> collectReportProgramTree(Class<T> clazz, ReportProgram program) {
		List<ReportProgram> result = new ArrayList<ReportProgram>();
		collectReportTree(clazz, program, result, null);
		return result;
	}
	
	public <T extends ReportTarget> boolean collectReportTree(Class<T> clazz, ReportProgram program, List<ReportProgram> collectedPrograms, List<T> collectedTargets) {
		if (log.debugEnabled) log.debug("collectReportTree(clazz=${clazz}, program=${program}, collectedPrograms=${collectedPrograms}, collectedTargets=${collectedTargets})")
		
		boolean hasTargets = false;
		for (ReportProgram child : program.getAllChildren()) {
			hasTargets = hasTargets | collectReportTree(clazz, child, collectedPrograms, collectedTargets);
		}
		
		//report target tree list
		List<T> targets = program.getReportTargets(clazz);
		if (log.isDebugEnabled()) log.debug("found immediate targets for program ${program}: ${targets}");
		
		if(!targets.isEmpty()){
			hasTargets = true;
			if (collectedTargets != null) collectedTargets.addAll(targets);	
		}
		
		//report program tree list
		if (hasTargets && collectedPrograms != null) collectedPrograms.add(program);
		
		return hasTargets;
	}
	
	/**
	 * Returns all report targets that point to the given data.
	 * 
	 * @param data the data to look for attached targets
	 * @return the list of all targets attached to the given data
	 */
	public List<AbstractReportTarget> getReportTargets(Data<?> data) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(AbstractReportTarget.class);
		criteria.add(Restrictions.eq("data", data));
		List<AbstractReportTarget> indicators = (List<AbstractReportTarget>)criteria.list();
		if (log.isDebugEnabled()) log.debug("getReportTargets(data="+data+",indicators="+indicators+")");
		return indicators;
	}
	
	/**
	 * Flushes all the caches.
	 */
	@CacheFlush(["dsrCache", "dashboardCache", "fctCache"])
	public void flushCaches(){
		
	}
	
	public Set<LocationLevel> getSkipReportLevels(Set<String> skipLevels) {
		Set<String> allSkipLevels = new HashSet<String>();
		
		//add report-specific skip levels
		if (skipLevels != null) allSkipLevels.addAll(skipLevels);
		// add report-generic skip levels
		allSkipLevels.addAll(grailsApplication.config.report.skip.levels);
		
		Set<LocationLevel> levels = new HashSet<LocationLevel>();
		for (String skipLevel : allSkipLevels) {
			levels.add(locationService.findLocationLevelByCode(skipLevel));
		}
		return levels;
	}
}
