package org.chai.kevin.value;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.chai.kevin.data.Sum;
import org.chai.kevin.location.CalculationEntity;
import org.chai.kevin.location.DataEntityType;
import org.hibernate.annotations.NaturalId;
import org.hisp.dhis.period.Period;

@Entity(name="SumValue")
@Table(name="dhsst_value_partial_sum",
	uniqueConstraints = {
		@UniqueConstraint(columnNames={"data", "entity", "period", "type"})
	}
)
public class SumPartialValue extends CalculationPartialValue {

	private Sum data;
	
	public SumPartialValue() {
		super();
	}

	public SumPartialValue(Sum data, CalculationEntity entity, Period period, DataEntityType type, Value value) {
		super(entity, period, type, value);
		
		this.data = data;
	}

	public SumPartialValue(Sum data, CalculationEntity entity, Period period, DataEntityType type) {
		super(entity, period, type);
		
		this.data = data;
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
