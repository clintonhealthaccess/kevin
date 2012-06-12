package org.chai.kevin.planning;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.chai.kevin.LanguageService;
import org.chai.kevin.Period;
import org.chai.kevin.Translation;
import org.chai.kevin.data.Type;
import org.chai.kevin.data.Type.TypeVisitor;
import org.chai.kevin.form.FormElement;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

@Entity(name="PlanningType")
@Table(name="dhsst_planning_type")
public class PlanningType {

	private Long id;
	
	private Planning planning;
	private Translation names = new Translation();
	private Translation namesPlural = new Translation();
	private Translation newHelps = new Translation();
	private Translation listHelps = new Translation();
	private String fixedHeader;

	private Integer maxNumber;
	private Map<String, Translation> sectionDescriptions = new HashMap<String, Translation>();
	
	// only accepts element of LIST<MAP> type
	private FormElement formElement;
	
	private List<PlanningCost> costs = new ArrayList<PlanningCost>();
	
	@Id
	@GeneratedValue
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	@Embedded
	@AttributeOverrides({ @AttributeOverride(name = "jsonText", column = @Column(name = "jsonNames", nullable = false)) })
	public Translation getNames() {
		return names;
	}
	
	public void setNames(Translation names) {
		this.names = names;
	}
	
	@Embedded
	@AttributeOverrides({ @AttributeOverride(name = "jsonText", column = @Column(name = "jsonNamesPlural", nullable = false)) })
	public Translation getNamesPlural() {
		return namesPlural;
	}
	
	public void setNamesPlural(Translation namesPlural) {
		this.namesPlural = namesPlural;
	}
	
	@Embedded
	@AttributeOverrides({
        @AttributeOverride(name="jsonText", column=@Column(name="jsonNewHelps", nullable=false))
	})
	public Translation getNewHelps() {
		return newHelps;
	}

	public void setNewHelps(Translation newHelps) {
		this.newHelps = newHelps;
	}
	
	@Embedded
	@AttributeOverrides({
        @AttributeOverride(name="jsonText", column=@Column(name="jsonListHelps", nullable=false))
	})
	public Translation getListHelps() {
		return listHelps;
	}

	public void setListHelps(Translation listHelps) {
		this.listHelps = listHelps;
	}

	@JoinColumn(nullable=false)
	@Cascade({ CascadeType.ALL, CascadeType.DELETE_ORPHAN })
	@ManyToOne(targetEntity=FormElement.class, optional=false)
	public FormElement getFormElement() {
		return formElement;
	}
	
	public void setFormElement(FormElement formElement) {
		this.formElement = formElement;
	}
	
	@ElementCollection(targetClass=Translation.class)
	@Cascade({ CascadeType.ALL, CascadeType.DELETE_ORPHAN })
	@JoinTable(name="dhsst_planning_type_descriptions")	
	public Map<String, Translation> getSectionDescriptions() {
		return sectionDescriptions;
	}
	
	public void setSectionDescriptions(Map<String, Translation> sectionDescriptions) {
		this.sectionDescriptions = sectionDescriptions;
	}
	
	@OneToMany(mappedBy="planningType", targetEntity=PlanningCost.class)
	@OrderBy("order")
	public List<PlanningCost> getCosts() {
		return costs;
	}
	
	public void setCosts(List<PlanningCost> costs) {
		this.costs = costs;
	}

	@ManyToOne(targetEntity=Planning.class)
	public Planning getPlanning() {
		return planning;
	}
	
	public void setPlanning(Planning planning) {
		this.planning = planning;
	}
	
	@Basic
	public Integer getMaxNumber() {
		return maxNumber;
	}
	
	public void setMaxNumber(Integer maxNumber) {
		this.maxNumber = maxNumber;
	}
	
	@Basic
	public String getFixedHeader() {
		return fixedHeader;
	}
	
	public void setFixedHeader(String fixedHeader) {
		this.fixedHeader = fixedHeader;
	}

	@Transient
	public Period getPeriod() {
		return planning.getPeriod();
	}
	
	@Transient
	public Type getFixedHeaderType() {
		return getType(getFixedHeader());
	}
	
	@Transient
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
	@Transient
	public List<String> getValuePrefixes(String section) {
		List<String> result = formElement.getDataElement().getValuePrefixes(section);
		// TODO how do we handle lists
		result.remove(getFixedHeader());
		return result;
	}
	
	@Transient
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
	
	public void buildGroupHierarchy(LanguageService languageService) {
		buildGroupHierarchy(languageService, new ArrayList<String>(), getCosts());
	}
	
	private void buildGroupHierarchy(LanguageService languageService, List<String> groups, List<PlanningCost> planningCosts) {
		for (PlanningCost planningCost : planningCosts) {
			List<String> groupsInName = planningCost.getGroups(languageService);
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
