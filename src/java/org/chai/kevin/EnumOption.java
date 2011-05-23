package org.chai.kevin;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity(name="EnumOption")
@Table(name="enumoption")
@Cache(usage=CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class EnumOption extends Translatable {

	private Long id;
	private String code;
	private String value;
	
	private Enum enume;

	@Id
	@GeneratedValue
	@Column(name="enumoptionid")
	public Long getId() {
		return id;
	}
	
	@Basic
	public String getCode() {
		return code;
	}
	
	@Basic(optional=false)
	@Column(nullable=false)
	public String getValue() {
		return value;
	}
	
	@ManyToOne(targetEntity=Enum.class, optional=false)
	@JoinColumn(name="enumid", nullable=false)
	public Enum getEnume() {
		return enume;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	public void setCode(String code) {
		this.code = code;
	}
	
	public void setValue(String value) {
		this.value = value;
	}
	
	public void setEnume(Enum enume) {
		this.enume = enume;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((code == null) ? 0 : code.hashCode());
		result = prime * result + ((enume == null) ? 0 : enume.hashCode());
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
		EnumOption other = (EnumOption) obj;
		if (code == null) {
			if (other.code != null)
				return false;
		} else if (!code.equals(other.code))
			return false;
		if (enume == null) {
			if (other.enume != null)
				return false;
		} else if (!enume.equals(other.enume))
			return false;
		return true;
	}
	
}
