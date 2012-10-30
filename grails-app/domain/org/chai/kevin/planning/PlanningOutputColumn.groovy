package org.chai.kevin.planning;

import i18nfields.I18nFields

import org.chai.kevin.IntegerOrderable

@I18nFields
class PlanningOutputColumn extends IntegerOrderable {

	String prefix;
	Integer order;
	
	String names
	
	// deprecated
	String jsonNames;
	
	static belongsTo = [planningOutput: PlanningOutput] 
	
	static i18nFields = ['names']
	
	static mapping = {
		table 'dhsst_planning_output_column'
		order column: 'ordering'
		planningOutput column: 'planningOutput'
	}
	
	static constraints = {
		prefix(nullable: false, blank: false)
		order (nullable: true)
		names (nullable: true)
		
		jsonNames (nullable: true)
	}
	
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	public boolean equals(Object obj) {
		if (this.(is(obj)))
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Planning))
			return false;
		Planning other = (Planning) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	
}
