package org.chai.kevin.fct;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.chai.kevin.Exportable;
import org.chai.kevin.data.Calculation;
import org.chai.kevin.data.Summ;
import org.chai.kevin.data.Type;
import org.chai.kevin.reports.AbstractReportTarget;
import org.chai.kevin.util.Utils;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.proxy.HibernateProxy;

class FctTargetOption extends AbstractReportTarget implements Exportable {

	String numberFormat = "#";
	String percentageFormat = "#%";
	
	static belongsTo = [target: FctTarget]
			
	static mapping = {
		table 'dhsst_fct_target_option'
		target column: 'target'
	}
	
	static constraints = {
		numberFormat (nullable: true)
		percentageFormat (nullable: true)
	}
	
	@Override
	public Type getType() {
		return getSum().getType();
	}

	public Summ getSum() {
		if (getData() instanceof HibernateProxy) {
			return Summ.class.cast(((HibernateProxy) getData()).getHibernateLazyInitializer().getImplementation());  
		}
		else {
			return Summ.class.cast(getData());
		}
	}
	
	public String getNumberFormat() {
		return numberFormat;
	}

	public String getPercentageFormat() {
		return percentageFormat;
	}
	
	@Override
	public String toString() {
		return "FctTargetOption[getCode()=" + getCode() + "]";
	}

	@Override
	public String toExportString() {
		return "[" + Utils.formatExportCode(getCode()) + "]";
	}

}
