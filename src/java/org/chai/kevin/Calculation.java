package org.chai.kevin;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.MapKey;
import javax.persistence.MapKeyColumn;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Cascade;

@Entity(name="Calculation")
@Table(name="dhsst_calculation")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Calculation implements Timestamped {

	private Long id;
	private Map<String, Expression> expressions = new HashMap<String, Expression>();
	private Date timestamp = new Date();
	
	@Id
	@GeneratedValue
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	@Column(nullable=false, columnDefinition="datetime")
	@Temporal(javax.persistence.TemporalType.TIMESTAMP)
	public Date getTimestamp() {
		return timestamp;
	}
	
	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	
	@ManyToMany(targetEntity=Expression.class)
	@JoinTable(name="dhsst_calculation_expression", inverseJoinColumns=@JoinColumn(name="expression", nullable=true))
	@MapKeyColumn(name="groupUuid")
	public Map<String, Expression> getExpressions() {
		return expressions;
	}
	public void setExpressions(Map<String, Expression> expressions) {
		this.expressions = expressions;
	}
	
	@Transient
	public ValueType getType() throws IllegalStateException {
		ValueType result = null;
		for (Expression expression : expressions.values()) {
			if (result == null) result = expression.getType();
			else {
				if (result != expression.getType()) throw new IllegalStateException("calculation contains expressions of different formats");
			}
		}
		return result;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((expressions == null) ? 0 : expressions.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Calculation other = (Calculation) obj;
		if (expressions == null) {
			if (other.expressions != null)
				return false;
		} else if (!expressions.equals(other.expressions))
			return false;
		return true;
	}
	
	
}
