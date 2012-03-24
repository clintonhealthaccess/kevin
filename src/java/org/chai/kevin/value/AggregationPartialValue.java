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
import org.chai.kevin.location.CalculationLocation;
import org.chai.kevin.location.DataLocationType;
import org.hibernate.annotations.NaturalId;

@Entity(name="AggregationPartialValue")
@Table(name="dhsst_value_partial_aggregation",
	uniqueConstraints = {
		@UniqueConstraint(columnNames={"data", "location", "period", "type", "expressionData"})
	}
)
public class AggregationPartialValue extends CalculationPartialValue {

	private String expressionData;
	private Aggregation data;
	
	public AggregationPartialValue() {
		super();
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

	@Basic
	@NaturalId
	@Column(nullable=false)
	public String getExpressionData() {
		return expressionData;
	}
	
	public void setExpressionData(String expressionData) {
		this.expressionData = expressionData;
	}

	@Override
	@NaturalId
	@ManyToOne(targetEntity=Aggregation.class, fetch=FetchType.LAZY)
	@JoinColumn(nullable=false)
	public Aggregation getData() {
		return data;
	}
	
	public void setData(Aggregation data) {
		this.data = data;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((data == null) ? 0 : data.hashCode());
		result = prime * result
				+ ((expressionData == null) ? 0 : expressionData.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (!(obj instanceof AggregationPartialValue))
			return false;
		AggregationPartialValue other = (AggregationPartialValue) obj;
		if (data == null) {
			if (other.data != null)
				return false;
		} else if (!data.equals(other.data))
			return false;
		if (expressionData == null) {
			if (other.expressionData != null)
				return false;
		} else if (!expressionData.equals(other.expressionData))
			return false;
		return true;
	}
	
}
