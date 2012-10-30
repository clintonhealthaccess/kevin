package org.chai.kevin.planning;

import i18nfields.I18nFields

import java.util.ArrayList
import java.util.List

import org.chai.kevin.IntegerOrderable
import org.chai.kevin.data.NormalizedDataElement

@I18nFields
class PlanningCost extends IntegerOrderable {

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

	Integer order;
	
	PlanningCostType type;
	NormalizedDataElement dataElement;
	Boolean hideIfZero = false
	String names
	
	// deprecated
	String jsonNames
	
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
		
		jsonNames (nullable: true)
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof PlanningCost))
			return false;
		PlanningCost other = (PlanningCost) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
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
