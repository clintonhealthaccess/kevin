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
import org.chai.kevin.location.CalculationLocation;
import org.chai.kevin.location.DataLocationType;
import org.hibernate.annotations.NaturalId;

@Entity(name="SumValue")
@Table(name="dhsst_value_partial_sum",
	uniqueConstraints = {
		@UniqueConstraint(columnNames={"data", "location", "period", "type"})
	}
)
public class SumPartialValue extends CalculationPartialValue {

	private Sum data;
	private Integer numberOfDataLocations;
	
	public SumPartialValue() {
		super();
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

	@Basic
	@Column(nullable=false)
	public Integer getNumberOfDataLocations() {
		return numberOfDataLocations;
	}
	public void setNumberOfDataLocations(Integer numberOfDataLocations) {
		this.numberOfDataLocations = numberOfDataLocations;
	}
	
	@NaturalId
	@ManyToOne(targetEntity=Sum.class, fetch=FetchType.LAZY)
	@JoinColumn(nullable=false)
	public Sum getData() {
		return data;
	}
	
	public void setData(Sum data) {
		this.data = data;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((data == null) ? 0 : data.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (!(obj instanceof SumPartialValue))
			return false;
		SumPartialValue other = (SumPartialValue) obj;
		if (data == null) {
			if (other.data != null)
				return false;
		} else if (!data.equals(other.data))
			return false;
		return true;
	}
	
}
