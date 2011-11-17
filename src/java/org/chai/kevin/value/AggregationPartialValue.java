package org.chai.kevin.value;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.chai.kevin.data.Aggregation;
import org.hibernate.annotations.NaturalId;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.period.Period;

@Entity(name="AggregationPartialValue")
@Table(name="dhsst_aggregation_value",
	uniqueConstraints = {
		@UniqueConstraint(columnNames={"aggregation", "organisationUnit", "period", "groupUuid", "expressionData"})
	}
)
public class AggregationPartialValue extends CalculationPartialValue {

	private String expressionData;
	private Aggregation data;
	
	public AggregationPartialValue() {
		super();
	}

	public AggregationPartialValue(Aggregation data, OrganisationUnit organisationUnit, Period period, String groupUuid, String expressionData, Value value) {
		super(organisationUnit, period, groupUuid, value);

		this.expressionData = expressionData;
		this.data = data;
	}

	public AggregationPartialValue(Aggregation data, OrganisationUnit organisationUnit, Period period, String groupUuid, String expressionData) {
		super(organisationUnit, period, groupUuid);
		
		this.expressionData = expressionData;
		this.data = data;
	}

	@Basic
	@Column(nullable=false)
	@NaturalId
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
