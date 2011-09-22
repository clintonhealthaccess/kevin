package org.chai.kevin.value;

/* 
 * Copyright (c) 2011, Clinton Health Access Initiative.
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the <organization> nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import org.chai.kevin.data.Calculation;
import org.chai.kevin.data.Data;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.NaturalId;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.period.Period;

@Entity(name="CalculationValue")
@Table(name="dhsst_calculation_value",
	uniqueConstraints = {
		@UniqueConstraint(columnNames={"calculation", "organisationUnit", "period"})
	}
)
//@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class CalculationValue extends StoredValue {

	private Long id;
	private Calculation calculation;
	
	private Boolean hasMissingValues;
	private Boolean hasMissingExpression;
	
	public CalculationValue() {}
	
	public CalculationValue(Calculation calculation, OrganisationUnit organisationUnit, Period period, Value value, boolean hasMissingValues, boolean hasMissingExpression) {
		super(organisationUnit, period, value);
		
		this.calculation = calculation;

		this.hasMissingExpression = hasMissingExpression;
		this.hasMissingValues = hasMissingValues;
	}
	
	public CalculationValue(Calculation calculation, OrganisationUnit organisationUnit, Period period) {
		super();
		
		this.calculation = calculation;
		this.organisationUnit = organisationUnit;
		this.period = period;
	}
	
	
	@Id
	@GeneratedValue
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@NaturalId
	@ManyToOne(targetEntity=Calculation.class, optional=false)
	public Calculation getCalculation() {
		return calculation;
	}
	
	public void setCalculation(Calculation calculation) {
		this.calculation = calculation;
	}

	@Basic
	@Column(nullable=false)
	public Boolean getHasMissingExpression() {
		return hasMissingExpression;
	}
	
	public void setHasMissingExpression(Boolean hasMissingExpression) {
		this.hasMissingExpression = hasMissingExpression;
	}
	
	@Basic
	@Column(nullable=false)
	public Boolean getHasMissingValues() {
		return hasMissingValues;
	}
	
	public void setHasMissingValues(Boolean hasMissingValues) {
		this.hasMissingValues = hasMissingValues;
	}
	
	@Override
	@Transient
	public Data<?> getData() {
		return calculation;
	}	
	
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((getCalculation() == null) ? 0 : getCalculation().hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (!(obj instanceof CalculationValue))
			return false;
		CalculationValue other = (CalculationValue) obj;
		if (getCalculation() == null) {
			if (other.getCalculation() != null)
				return false;
		} else if (!getCalculation().equals(other.getCalculation()))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "CalculationValue [calculation=" + calculation
				+ ", hasMissingValues=" + hasMissingValues
				+ ", hasMissingExpression=" + hasMissingExpression
				+ ", value=" + getValue() + "]";
	}


}
