package org.chai.kevin.value;

import java.util.Date;
import java.util.Map;
import java.util.Set;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.chai.kevin.Period;
import org.chai.kevin.data.Data
import org.chai.kevin.data.Mode;
import org.chai.kevin.util.JSONUtils;
import org.chai.location.CalculationLocation;
import org.chai.location.DataLocationType;
import org.hibernate.annotations.NaturalId;

public class ModePartialValue extends CalculationPartialValue {

	Data data;
	
	static constraints = {
		// TODO this create an UNIQUE index in the database that should not be there
//		data (nullable: false, unique: ['data', 'period', 'location', 'type'])
		data (nullable: false)
	}
	
	public ModePartialValue() {
		super();
	}

	public ModePartialValue(Mode data, CalculationLocation location, Period period, DataLocationType type, Value value) {
		super(location, period, type, value);
		this.data = data;
	}

	public ModePartialValue(Mode data, CalculationLocation location, Period period, DataLocationType type) {
		super(location, period, type);		
		this.data = data;
	}
	
}