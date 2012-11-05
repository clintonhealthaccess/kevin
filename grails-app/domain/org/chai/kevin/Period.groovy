package org.chai.kevin;

import groovy.transform.EqualsAndHashCode;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;

import org.chai.kevin.util.Utils;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@EqualsAndHashCode(includes='code')
class Period implements Exportable {

	// TODO get rid of this
	String code;
	Date startDate;
	Date endDate;

	// deprecated
	Long id;
	
	static mapping = {
		table 'dhsst_period'
		cache true
		startDate sqlType: "datetime"
		endDate sqlType: "datetime"
	}
	
	static constraints =  {
		code (nullable: false, blank: false, unique: true)
		startDate(nullable: false, blank: false)
		endDate(nullable: false, blank: false, validator: { val, obj ->
			if (obj.startDate && val) return val?.after(obj.startDate)
		})
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