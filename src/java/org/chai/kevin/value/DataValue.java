package org.chai.kevin.value;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.chai.kevin.DataElement;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.NaturalId;

@Entity(name="DataValue")
@Table(name="datavalue",
		uniqueConstraints=@UniqueConstraint(columnNames={"dataElement", "period", "organisationUnit"})
)
@Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
public class DataValue extends Value {
	
	private Long id;
	
	private String value;
	
	private DataElement dataElement;
	
	@Id
	@GeneratedValue
	public Long getId() {
		return id;
	}

	@NaturalId
	@ManyToOne(targetEntity=DataElement.class, optional=false)
	@JoinColumn(nullable=false)
	public DataElement getDataElement() {
		return dataElement;
	}
	
	@Basic(optional=false)
	@Column(nullable=false, length=511)
	public String getValue() {
		return value;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	public void setDataElement(DataElement dataElement) {
		this.dataElement = dataElement;
	}
	
	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((dataElement == null) ? 0 : dataElement.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		DataValue other = (DataValue) obj;
		if (dataElement == null) {
			if (other.dataElement != null)
				return false;
		} else if (!dataElement.equals(other.dataElement))
			return false;
		return true;
	}
	
	
}
