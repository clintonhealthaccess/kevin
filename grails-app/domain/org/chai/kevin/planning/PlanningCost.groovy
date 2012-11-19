package org.chai.kevin.planning;

import groovy.transform.EqualsAndHashCode
import i18nfields.I18nFields

import org.chai.kevin.data.NormalizedDataElement

@I18nFields
//@EqualsAndHashCode(includes='id')
class PlanningCost {

	public enum PlanningCostType {OUTGOING("planning.planningcost.type.outgoing"), INCOMING("planning.planningcost.type.incoming");
		private String code;
	
		PlanningCostType(String code) {
			this.code = code;
		}
	
		public String getCode() {
			return code;
		}
		String getKey() { return name(); }
	};

	Long id

	Integer order;
	
	PlanningCostType type;
	NormalizedDataElement dataElement;
	Boolean hideIfZero
	String names
	
	static i18nFields = ['names']
	
	PlanningType planningType
	static belongsTo = [planningType: PlanningType]
	
	static mapping = {
		table 'dhsst_planning_cost'
		order column: 'ordering'
		dataElement column: 'dataElement'
		planningType column: 'planningType'
	}
	
	static constraints = {
		dataElement (nullable: false)
		type (nullable: false)
		order (nullable: true)
		names (nullable: true)
	}
	
	public String toString(){
		return "PlanningCost[getId()=" + getId() + ", getNames()=" + getNames() + ", getType()=" + getType() + "]";
	}

	private List<String> splitName() {
		String name = names?:''
		String[] groupsInNameArray = name.split("-");
		List<String> groupsInName = new ArrayList<String>();
		for (String group : groupsInNameArray) {
			groupsInName.add(group.trim());
		}
		return groupsInName;
	}

	public List<String> getGroups() {
		List<String> groupsInName = splitName();
		groupsInName.remove(groupsInName.size() - 1);
		return groupsInName;
	}
	
	public String getDisplayName() {
		List<String> groupsInName = splitName();
		return groupsInName.get(groupsInName.size() -1);
	}
	
}
