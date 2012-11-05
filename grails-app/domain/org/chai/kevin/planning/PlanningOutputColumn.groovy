package org.chai.kevin.planning;

import groovy.transform.EqualsAndHashCode
import i18nfields.I18nFields

@I18nFields
//@EqualsAndHashCode(includes='id')
class PlanningOutputColumn {

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
}
