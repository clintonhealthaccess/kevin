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
import org.chai.kevin.data.Sum;
import org.chai.location.CalculationLocation;
import org.chai.location.DataLocationType;
import org.hibernate.annotations.NaturalId;

public class SumPartialValue extends CalculationPartialValue {

	Sum data;
	Integer numberOfDataLocations;
	
	static constraints = {
		// TODO this create an UNIQUE index in the database that should not be there
//		data (nullable: false, unique: ['data', 'period', 'location', 'type'])
		data (nullable: false)
		numberOfDataLocations (nullable: false)
	}
	
	public SumPartialValue(Sum data, CalculationLocation location, Period period, DataLocationType type, Integer numberOfDataLocations, Value value) {
		super(location, period, type, value);		
		this.data = data;
		this.numberOfDataLocations = numberOfDataLocations;
	}

	public SumPartialValue(Sum data, CalculationLocation location, Period period, DataLocationType type, Integer numberOfDataLocations) {
		super(location, period, type);		
		this.data = data;
		this.numberOfDataLocations = numberOfDataLocations;
	}

}
