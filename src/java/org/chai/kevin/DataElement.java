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

@Entity(name="DataElement")
@Table(name="dataelement")
@Cache(usage=CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class DataElement extends Translatable {

	public enum DataElementType {
		BOOL, ENUM, INT, DATE, STRING
	};
	
	private Long id;
	private String code;
	
	private DataElementType type;
	private Enum enume;

	@Id
	@GeneratedValue
	@Column(name="dataelementid")
	public Long getId() {
		return id;
	}
	
	@Basic
	public String getCode() {
		return code;
	}
	
	@Enumerated(EnumType.STRING)
	@Column(nullable=false, name="valuetype")
	public DataElementType getType() {
		return type;
	}
	
	@ManyToOne(targetEntity=Enum.class)
	@JoinColumn(name="enumid")
	public Enum getEnume() {
		return enume;
	}
	
	@Transient
	public boolean isAggregatable() {
		return getType() == DataElementType.INT;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	public void setCode(String code) {
		this.code = code;
	}
	
	public void setEnume(Enum enume) {
		this.enume = enume;
	}
	
	public void setType(DataElementType type) {
		this.type = type;
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
		DataElement other = (DataElement) obj;
		if (code == null) {
			if (other.code != null)
				return false;
		} else if (!code.equals(other.code))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "DataElement [name=" + getName() + ", code=" + code + ", type="
				+ type + ", enume=" + enume + "]";
	}
	
	
	
}
