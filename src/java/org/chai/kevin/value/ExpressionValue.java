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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import org.chai.kevin.data.Expression;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.NaturalId;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.period.Period;

@Entity(name="ExpressionValue")
@Table(name="dhsst_expression_value",
	uniqueConstraints = {
		@UniqueConstraint(columnNames={"organisationUnit", "expression", "period"})
	}
)
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class ExpressionValue extends AbstractValue {
	private Integer id;

	private Status status;
	
	private Expression expression;
	
	public enum Status {
		VALID,
		MISSING_VALUE,
		NOT_AGGREGATABLE
	}
	
	public ExpressionValue() {}
	
	public ExpressionValue(String value, Status status, OrganisationUnit organisationUnit, Expression expression, Period period) {
		this.value = value;
		this.status = status;

		this.organisationUnit = organisationUnit;
		this.expression = expression;
		this.period = period;
	}
	

	@Id
	@GeneratedValue
	public Integer getId() {
		return id;
	}

	@NaturalId
	@ManyToOne(targetEntity=Expression.class, fetch=FetchType.LAZY)
	public Expression getExpression() {
		return expression;
	}
	
	@Enumerated(EnumType.STRING)
	@Column(nullable=false)
	public Status getStatus() {
		return status;
	}
	
	public void setId(Integer id) {
		this.id = id;
	}
	
	public void setStatus(Status status) {
		this.status = status;
	}
	
	public void setExpression(Expression expression) {
		this.expression = expression;
	}
	
	
	@Override
	public String toString() {
		return "ExpressionValue [status=" + status + ", value=" + value
				+ ", organisationUnit=" + organisationUnit + ", expression="
				+ expression + ", period=" + period + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((getExpression() == null) ? 0 : getExpression().hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (!(obj instanceof ExpressionValue))
			return false;
		ExpressionValue other = (ExpressionValue) obj;
		if (getExpression() == null) {
			if (other.getExpression() != null)
				return false;
		} else if (!getExpression().equals(other.getExpression()))
			return false;
		return true;
	}

}