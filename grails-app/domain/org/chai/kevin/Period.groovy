package org.chai.kevin;

import groovy.transform.EqualsAndHashCode

import org.chai.kevin.util.Utils

@EqualsAndHashCode(includes='code')
class Period implements Exportable {

	// TODO get rid of this
	String code;
	Date startDate;
	Date endDate;
	Boolean defaultSelected

	// deprecated
	Long id;
	
	static mapping = {
		table 'dhsst_period'
		cache true
//		startDate sqlType: "datetime"
//		endDate sqlType: "datetime"
	}
	
	static constraints =  {
		code (nullable: false, blank: false, unique: true)
		startDate(nullable: false, blank: false)
		endDate(nullable: false, blank: false, validator: { val, obj ->
			if (obj.startDate && val) return val?.after(obj.startDate)
		})
		defaultSelected (nullable: false)
	}

	
	@Override
	public String toString() {
		return "Period[getId()=" + getId() + ", getStartDate()=" + getStartDate() + ", getEndDate()=" + getEndDate() + "]";
	}

	@Override
	public String toExportString() {
		return "[" + Utils.formatExportCode(getCode()) + "]";
	}
	
}