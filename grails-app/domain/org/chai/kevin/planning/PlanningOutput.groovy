package org.chai.kevin.planning;

import groovy.transform.EqualsAndHashCode;
import i18nfields.I18nFields

import org.chai.kevin.data.DataElement
import org.chai.kevin.data.Type.ValueType

@I18nFields
//@EqualsAndHashCode(includes='id')
class PlanningOutput {

	Integer order;
	DataElement dataElement;
	String fixedHeader;
	Boolean displayTotal;
	
	String names
	String captions
	String helps

	public Planning() {
		displayTotal = false
	}
	
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
	}
	
}
