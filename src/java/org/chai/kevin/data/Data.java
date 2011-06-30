package org.chai.kevin.data;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import org.chai.kevin.Timestamped;
import org.chai.kevin.Translatable;
import org.chai.kevin.value.Value;
import org.chai.kevin.value.ValueCalculator;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.period.Period;

@Entity(name="Data")
@Table(name="dhsst_data", uniqueConstraints={@UniqueConstraint(columnNames="code")})
@Inheritance(strategy=InheritanceType.JOINED)
abstract public class Data<T extends Value> extends Translatable implements Timestamped {
	
	private static final long serialVersionUID = 7470871788061305391L;

	private Long id;
	private ValueType type;
	private Enum enume;
	private Date timestamp = new Date();
	
	@Id
	@GeneratedValue
	@Column
	public Long getId() {
		return id;
	}
	
	@Enumerated(EnumType.STRING)
	@Column(nullable=false)
	public ValueType getType() {
		return type;
	}
	
	@ManyToOne(targetEntity=Enum.class)
	@JoinColumn
	public Enum getEnume() {
		return enume;
	}
	
	@Column(nullable=false, columnDefinition="datetime")
	@Temporal(javax.persistence.TemporalType.TIMESTAMP)
	public Date getTimestamp() {
		return timestamp;
	}
	
	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}
	
	
	public void setId(Long id) {
		this.id = id;
	}
	
	public void setEnume(Enum enume) {
		this.enume = enume;
	}
	
	public void setType(ValueType type) {
		this.type = type;
	}
	
	@Transient
	public boolean isAggregatable() {
		return getType() == ValueType.VALUE;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((code == null) ? 0 : code.hashCode());
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
		Data other = (Data) obj;
		if (code == null) {
			if (other.code != null)
				return false;
		} else if (!code.equals(other.code))
			return false;
		return true;
	}
	
	@Transient
	public abstract T getValue(ValueCalculator calculator, OrganisationUnit organisationUnit, Period period);
	
}
