package org.chai.kevin.planning;

import i18nfields.I18nFields

import org.chai.kevin.data.DataElement
import org.chai.kevin.data.Type.ValueType

@I18nFields
class PlanningOutput {

	Integer order;
	DataElement dataElement;
	String fixedHeader;
	Boolean displayTotal = false;
	
	String names
	String captions
	String helps

	// deprecated
	String jsonNames
	String jsonCaptions
	String jsonHelps

	static i18nFields = ['names', 'captions', 'helps']
	
	Planning planning
	static belongsTo = [planning: Planning]
	
	static hasMany = [columns: PlanningOutputColumn]
	
	static mapping = {
		table 'dhsst_planning_output'
		order column: 'ordering'
		dataElement column: 'dataElement'
		planning column: 'planning'
	}
	
	static constraints = {
		dataElement (nullable: false, validator: {val, obj ->
			return val.type.type == ValueType.LIST
		})
		fixedHeader (nullable: false, blank: false)
		order (nullable: true)
		
		names (nullable: true)
		captions (nullable: true)
		helps (nullable: true)
		
		jsonNames (nullable: true)
		jsonCaptions (nullable: true)
		jsonHelps (nullable: true)
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
