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

import java.util.Map;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.chai.kevin.Calculation;
import org.chai.kevin.Organisation;
import org.chai.kevin.ValueType;
import org.chai.kevin.value.ExpressionValue.Status;
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
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class CalculationValue extends Value {

	private static final Log log = LogFactory.getLog(CalculationValue.class);
	
	private Long id;
	
	private Calculation calculation;
	private Boolean hasMissingValues;
	private Boolean hasMissingExpression;
	
	private Double average;
	
	public CalculationValue() {}
	
	public CalculationValue(Calculation calculation, OrganisationUnit organisationUnit, Period period, Map<Organisation, ExpressionValue> values) {
		this.calculation = calculation;
		this.organisationUnit = organisationUnit;
		this.period = period;

		calculateAverage(values);
		calculateHasMissingExpression(values);
		calculateHasMissingValues(values);
	}
	
	@Id
	@GeneratedValue
	public Long getId() {
		return id;
	}
	
	@NaturalId
	@ManyToOne(targetEntity=Calculation.class, optional=false)
	public Calculation getCalculation() {
		return calculation;
	}
	
	@Basic
	@Column(nullable=true)
	public Double getAverage() {
		return average;
	}
	
	@Basic
	@Column(nullable=false)
	public Boolean getHasMissingExpression() {
		return hasMissingExpression;
	}
	
	@Basic
	@Column(nullable=false)
	public Boolean getHasMissingValues() {
		return hasMissingValues;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	public void setCalculation(Calculation calculation) {
		this.calculation = calculation;
	}
	
	public void setAverage(Double average) {
		this.average = average;
	}
	
	public void setHasMissingExpression(Boolean hasMissingExpression) {
		this.hasMissingExpression = hasMissingExpression;
	}
	
	public void setHasMissingValues(Boolean hasMissingValues) {
		this.hasMissingValues = hasMissingValues;
	}
	
	private void calculateHasMissingValues(Map<Organisation, ExpressionValue> values) {
		for (ExpressionValue expressionValue : values.values()) {
			if (expressionValue != null && expressionValue.getStatus() == Status.MISSING_VALUE) hasMissingValues = true;
		}
		hasMissingValues = false;
	}
	
	private void calculateHasMissingExpression(Map<Organisation, ExpressionValue> values) {
		for (ExpressionValue expressionValue : values.values()) {
			if (expressionValue == null) hasMissingExpression = true;
		}
		hasMissingExpression = false;
	}

	private void calculateAverage(Map<Organisation, ExpressionValue> values) {
		if (calculation.getType() != ValueType.VALUE) log.error("averaging value of non VALUE type calculation: "+calculation);
		// we do it anyway in case it's a user error

		try {
			Double sum = 0d;
			Integer num = 0;
			for (ExpressionValue expressionValue : values.values()) {
				if (expressionValue != null && expressionValue.getStatus() == Status.VALID) {
					sum += Double.parseDouble(expressionValue.getValue());
					num++;
				}
			}
			average = sum / num;
			if (average.isNaN()) average = null;
		} catch (NumberFormatException e) {
			log.error("average of non-number values: ", e);
			average = null;
		}
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


}
