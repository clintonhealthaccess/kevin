package org.chai.kevin.planning;

import groovy.transform.EqualsAndHashCode;
import i18nfields.I18nFields

import java.util.ArrayList
import java.util.HashMap
import java.util.List
import java.util.Map

import org.chai.kevin.Period
import org.chai.kevin.data.Type
import org.chai.kevin.data.Type.TypeVisitor
import org.chai.kevin.form.FormElement

@I18nFields
//@EqualsAndHashCode(includes='id')
class PlanningType {

	Long id
	
	// only accepts element of LIST<MAP> type
	FormElement formElement;
	
	String fixedHeader;
	Integer maxNumber;

	String names
	String namesPlural
	String newHelps
	String listHelps
	
	static transients = ['sectionDescriptions']

	static i18nFields = ['names', 'namesPlural', 'newHelps', 'listHelps']
		
	Planning planning
	static belongsTo = [planning: Planning]
	
	static hasMany = [
		costs: PlanningCost,
		planningTypeSectionMaps: PlanningTypeSectionMap
	]
	
	static mapping = {
		table 'dhsst_planning_type'
		planning column: 'planning'
		formElement column: 'formElement', cascade: 'all'
	}
	
	static constraints = {
		formElement (nullable:false)
		fixedHeader (nullable: true, validator: {val, obj ->
			if (val == null || val.trim().empty) return true;
			if (obj.formElement?.dataElement == null) return false;
			if (!obj.formElement.dataElement.getValuePrefixes('').contains(val)) return false;
		})
		maxNumber (nullable: true)
		
		names (nullable: true)
		namesPlural (nullable: true)
		newHelps (nullable: true)
		listHelps (nullable: true)
	}
	
	List<PlanningCost> getAllCosts() {
		return new ArrayList(costs?:[])
	}
		
	public Map<String, String> getSectionDescriptions(String language) {
		Map result = [:]
		planningTypeSectionMaps?.each {
			result.put(it.section, it.getNames(new Locale(language)))
		}
		return result;
	}
	
	public Map<String, Map<String, String>> getSectionDescriptions() {
		def result = [:]
		planningTypeSectionMaps?.each {
			def map = [:]
			domainClass.grailsApplication.config.i18nFields.locales.each{ language ->
				map.put(language, it.getNames(new Locale(language)))
			}
			result.put(it.section, map)
		}
		return result
	}
	
	public void setSectionDescriptions(Map<String, Map<String, String>> sectionDescriptions) {
		if (log.debugEnabled) log.debug('old section map: '+planningTypeSectionMaps)
		
		def oldPlanningTypeSectionMap = new ArrayList(planningTypeSectionMaps?:[])

		def newSections = []
		sectionDescriptions.each {
			if (oldPlanningTypeSectionMap.find{old -> old.section == it.key}) {
				oldPlanningTypeSectionMap.find{old -> old.section == it.key}.setNamesMap(it.value)
			}
			else {
				def sectionMap = new PlanningTypeSectionMap(section: it.key)
				sectionMap.setNamesMap(it.value)
				
				if (log.debugEnabled) log.debug('adding section map: '+sectionMap)
				addToPlanningTypeSectionMaps(sectionMap)
			}
			newSections.add(it.key)
		}
		
		oldPlanningTypeSectionMap.each {
			if (!newSections.contains(it.section)) {
				def toRemove = planningTypeSectionMaps.find{current -> it.section == current.section}
				
				if (log.debugEnabled) log.debug('removing section map: '+toRemove)
				removeFromPlanningTypeSectionMaps(toRemove)
			}
		}
		
		if (log.debugEnabled) log.debug('new section map: '+planningTypeSectionMaps)
	}

	public Period getPeriod() {
		return planning.getPeriod();
	}
	
	public Type getFixedHeaderType() {
		return getType(getFixedHeader());
	}
	
	public Type getType(String section) {
		try {
			return formElement.getDataElement().getType().getType(section);
		} catch (IllegalArgumentException e) {
			return null;
		}
	}
	
	/**
	 * Value prefixes are all values having the following properties:
	 * 
	 *  - Non-complex values not in block MAP
	 *  - Block MAP values
	 *  - LIST values
	 * 
	 * @param section
	 * @return
	 */
	public List<String> getValuePrefixes(String section) {
		List<String> result = formElement.getDataElement().getValuePrefixes(section);
		// TODO how do we handle lists
		result.remove(getFixedHeader());
		return result;
	}
	
	public List<String> getSections() {
		final List<String> result = new ArrayList<String>();
		formElement.getDataElement().getType().visit(new TypeVisitor() {
			@Override
			public void handle(Type type, String prefix) {
				if (getParents().size() == 3) result.add(prefix);
			}
		});
		return result;
	}

	private Map<List<String>, List<PlanningCost>> planningCostsInGroup = new HashMap<List<String>, List<PlanningCost>>();
	private Map<List<String>, List<PlanningCost>> planningCosts = new HashMap<List<String>, List<PlanningCost>>();
	private Map<List<String>, List<String>> groupHierarchy = new HashMap<List<String>, List<String>>();
	
	public void buildGroupHierarchy() {
		buildGroupHierarchy(new ArrayList<String>());
	}
	
	private void buildGroupHierarchy(List<String> groups) {
		for (PlanningCost planningCost : costs) {
			List<String> groupsInName = planningCost.getGroups();
			addPlanningCostToGroup(groupsInName, planningCost);
		}
	}

	private void addPlanningCostToGroup(List<String> groups, PlanningCost planningCost) {
		addToMap(planningCostsInGroup, groups, planningCost);
		
		List<String> currentGroups = new ArrayList<String>();
		for (String group : groups) {
			addToMap(planningCosts, new ArrayList<String>(currentGroups), planningCost);
			addToMap(groupHierarchy, new ArrayList<String>(currentGroups), group);
			currentGroups.add(group);
		}
		addToMap(planningCosts, new ArrayList<String>(currentGroups), planningCost);
	}

	private <T> void addToMap(Map<List<String>, List<T>> map, List<String> groups, T element) {
		if (!map.containsKey(groups)) {
			map.put(groups, new ArrayList<T>());
		}
		if (!map.get(groups).contains(element)) map.get(groups).add(element);
	}
	
	public List<String> getGroups(List<String> groups) {
		return groupHierarchy.get(groups);
	}
	
	public List<PlanningCost> getPlanningCostsInGroup(List<String> groups) {
		return planningCostsInGroup.get(groups);
	}
	
	public List<PlanningCost> getPlanningCosts(List<String> groups) {
		return planningCosts.get(groups);
	}
	
	public String toString(){
		return "PlanningType[getId()=" + getId() + ", getNames()=" + getNames() + "]";
	}
	
}
