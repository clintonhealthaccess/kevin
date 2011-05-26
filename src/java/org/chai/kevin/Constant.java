package org.chai.kevin;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity(name="Constant")
@Table(name="constant")
@Cache(usage=CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Constant extends Translatable {

	private static final long serialVersionUID = -5866136027581146157L;

	private Long id;
	
	private ValueType type;
	private Enum enume;
	private String value;
	
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
	
	@Basic
	public String getValue() {
		return value;
	}
	
	
//	@Transient
//	public boolean isAggregatable() {
//		return getType() == DataElementType.INT;
//	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	public void setEnume(Enum enume) {
		this.enume = enume;
	}
	
	public void setType(ValueType type) {
		this.type = type;
	}
	
	public void setValue(String value) {
		this.value = value;
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
		Constant other = (Constant) obj;
		if (code == null) {
			if (other.code != null)
				return false;
		} else if (!code.equals(other.code))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "DataElement [code=" + getCode() + ", type="
				+ type + ", enume=" + enume + "]";
	}
	
}
