package org.chai.kevin.data;

import java.util.Date;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
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
import org.chai.kevin.Translation;
import org.chai.kevin.value.Value;
import org.chai.kevin.value.ValueCalculator;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.period.Period;

@Entity(name="Data")
@Table(name="dhsst_data", uniqueConstraints={@UniqueConstraint(columnNames="code")})
@Inheritance(strategy=InheritanceType.JOINED)
abstract public class Data<T extends Value> implements Timestamped {
	
	private static final long serialVersionUID = 7470871788061305391L;

	private Long id;
	private ValueType type;
	private Enum enume;
	private Date timestamp = new Date();
	
	private String code;
	private Translation names = new Translation();
	private Translation descriptions = new Translation();
	
	@Id
	@GeneratedValue
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
	

	@Embedded
	@AttributeOverrides({
		@AttributeOverride(name="jsonText", column=@Column(name="jsonNames", nullable=false))
	})
	public Translation getNames() {
		return names;
	}
	
	@Embedded
	@AttributeOverrides({
        @AttributeOverride(name="jsonText", column=@Column(name="jsonDescriptions", nullable=false))
	})
	public Translation getDescriptions() {
		return descriptions;
	}

	@Basic(fetch=FetchType.EAGER)
	public String getCode() {
		return code;
	}
	

	public void setCode(String code) {
		this.code = code;
	}

	public void setNames(Translation names) {
		this.names = names;
	}
	
	public void setDescriptions(Translation descriptions) {
		this.descriptions = descriptions;
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
		result = prime * result + ((getCode() == null) ? 0 : getCode().hashCode());
		return result;
	}

	@Override
	@SuppressWarnings("unchecked")
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Data))
			return false;
		Data<T> other = (Data<T>) obj;
		if (getCode() == null) {
			if (other.getCode() != null)
				return false;
		} else if (!getCode().equals(other.getCode()))
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		return "Data [type=" + type + ", enume=" + enume + ", code=" + code + "]";
	}

	@Transient
	public abstract T getValue(ValueCalculator calculator, OrganisationUnit organisationUnit, Period period);
	
}
