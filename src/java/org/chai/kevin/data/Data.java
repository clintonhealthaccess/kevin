package org.chai.kevin.data;

import java.util.Date;
import java.util.List;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import org.chai.kevin.Period;
import org.chai.kevin.Translation;
import org.chai.kevin.location.DataLocation;
import org.chai.kevin.value.DataValue;
import org.chai.kevin.value.ExpressionService;
import org.chai.kevin.value.ValueService;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity(name="Data")
@Table(name="dhsst_data", uniqueConstraints={@UniqueConstraint(columnNames="code")})
@Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
@Inheritance(strategy=InheritanceType.JOINED)
abstract public class Data<T extends DataValue> {
	
	private Long id;
	private Date timestamp = new Date();
	
	private String code;
	private Translation names = new Translation();
	private Translation descriptions = new Translation();
	
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

	@Embedded
	@AttributeOverrides({
		@AttributeOverride(name="jsonText", column=@Column(name="jsonNames", nullable=false))
	})
	public Translation getNames() {
		return names;
	}
	
	public void setNames(Translation names) {
		this.names = names;
	}

	@Embedded
	@AttributeOverrides({
        @AttributeOverride(name="jsonText", column=@Column(name="jsonDescriptions", nullable=false))
	})
	public Translation getDescriptions() {
		return descriptions;
	}

	public void setDescriptions(Translation descriptions) {
		this.descriptions = descriptions;
	}

	@Basic
	public String getCode() {
		return code;
	}
	
	public void setCode(String code) {
		this.code = code;
	}

	@Transient
	public abstract Type getType();
	
	@Transient
	public abstract Class<T> getValueClass();

	@Override
	public String toString() {
		return "Data [getId()=" + getId() + ", getCode()="
				+ getCode() + "]";
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
		if (!(obj instanceof Data))
			return false;
		Data<?> other = (Data<?>) obj;
		if (code == null) {
			if (other.code != null)
				return false;
		} else if (!code.equals(other.code))
			return false;
		return true;
	}

}
