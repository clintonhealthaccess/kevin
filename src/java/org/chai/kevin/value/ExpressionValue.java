
package org.chai.kevin.value;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
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

import org.chai.kevin.Expression;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Cascade;
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
public class ExpressionValue extends Value {
	private Integer id;

	private Status status;
	private String value;
	
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
	
	@Basic(optional=true)
	@Column(nullable=true)
	public String getValue() {
		return value;
	}
	
	public void setId(Integer id) {
		this.id = id;
	}
	
	public void setValue(String value) {
		this.value = value;
	}
	
	public void setStatus(Status status) {
		this.status = status;
	}
	
	public void setExpression(Expression expression) {
		this.expression = expression;
	}
	
	@Transient
	public Double getNumberValue() {
		if (value == null) return null;
		try {
			return Double.parseDouble(value);
		} catch (NumberFormatException e) {
			return null;
		}
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