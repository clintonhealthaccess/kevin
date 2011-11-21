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
import org.chai.kevin.data.Average;
import org.hibernate.annotations.NaturalId;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.period.Period;

@Entity(name="AveragePartialValue")
@Table(name="dhsst_average_value",
	uniqueConstraints = {
		@UniqueConstraint(columnNames={"data", "organisationUnit", "period", "groupUuid"})
	}
)
public class AveragePartialValue extends CalculationPartialValue {

	private Integer numberOfFacilities;
	private Average data;
	
	public AveragePartialValue() {
		super();
	}

	public AveragePartialValue(Average data, OrganisationUnit organisationUnit, Period period, String groupUuid, Integer numberOfFacilities, Value value) {
		super(organisationUnit, period, groupUuid, value);
		
		this.data = data;
		this.numberOfFacilities = numberOfFacilities;
	}

	public AveragePartialValue(Average data, OrganisationUnit organisationUnit, Period period, String groupUuid, Integer numberOfFacilities) {
		super(organisationUnit, period, groupUuid);
		
		this.data = data;
		this.numberOfFacilities = numberOfFacilities;
	}

	@Override
	@NaturalId
	@ManyToOne(targetEntity=Aggregation.class, fetch=FetchType.LAZY)
	@JoinColumn(nullable=false)
	public Average getData() {
		return data;
	}
	
	public void setData(Average data) {
		this.data = data;
	}
	
	@Basic
	@Column(nullable=false)
	public Integer getNumberOfFacilities() {
		return numberOfFacilities;
	}
	
	public void setNumberOfFacilities(Integer numberOfFacilities) {
		this.numberOfFacilities = numberOfFacilities;
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
		if (!(obj instanceof AveragePartialValue))
			return false;
		AveragePartialValue other = (AveragePartialValue) obj;
		if (data == null) {
			if (other.data != null)
				return false;
		} else if (!data.equals(other.data))
			return false;
		return true;
	}

}
