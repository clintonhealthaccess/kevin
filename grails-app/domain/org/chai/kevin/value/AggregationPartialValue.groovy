package org.chai.kevin.value;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.chai.kevin.Period;
import org.chai.kevin.data.Aggregation;
import org.chai.kevin.data.Data;
import org.chai.location.CalculationLocation;
import org.chai.location.DataLocationType;
import org.hibernate.annotations.NaturalId;

class AggregationPartialValue extends CalculationPartialValue {

	// TODO this should be of type Aggregation
	Data data;
	String expressionData;
	
	static constraints = {
		// TODO this create an UNIQUE index in the database that should not be theres
//		data (nullable: false, unique: ['data', 'period', 'location', 'expressionData', 'type'])
		data (nullable: false)
		expressionData (nullable: false)
	}
	
	public AggregationPartialValue(Aggregation data, CalculationLocation location, Period period, DataLocationType type, String expressionData, Value value) {
		super(location, period, type, value);

		this.expressionData = expressionData;
		this.data = data;
	}

	public AggregationPartialValue(Aggregation data, CalculationLocation location, Period period, DataLocationType type, String expressionData) {
		super(location, period, type);
		
		this.expressionData = expressionData;
		this.data = data;
	}

}
