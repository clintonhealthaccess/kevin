package org.chai.kevin;

import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.NaturalId;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.period.Period;

@Entity(name="DataValue")
@Table(name="datavalue",
		uniqueConstraints=@UniqueConstraint(columnNames={"dataElement", "period", "organisationUnit"})
)
@Cache(usage=CacheConcurrencyStrategy.READ_ONLY)
public class DataValue {
	
	private Long id;
	
	private String value;
	private Date timestamp;
	
	private DataElement dataElement;
	private Period period;
	private OrganisationUnit organisationUnit;
	
	@Id
	@GeneratedValue
	public Long getId() {
		return id;
	}

	@NaturalId
	@ManyToOne(targetEntity=OrganisationUnit.class, optional=false)
	@JoinColumn(nullable=false)
	public OrganisationUnit getOrganisationUnit() {
		return organisationUnit;
	}
	
	@NaturalId
	@ManyToOne(targetEntity=Period.class, optional=false)
	@JoinColumn(nullable=false)
	public Period getPeriod() {
		return period;
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
	
	@Basic
	@Column
	public Date getTimestamp() {
		return timestamp;
	}
	
	
	public void setId(Long id) {
		this.id = id;
	}
	
	public void setOrganisationUnit(OrganisationUnit organisationUnit) {
		this.organisationUnit = organisationUnit;
	}
	
	public void setPeriod(Period period) {
		this.period = period;
	}
	
	public void setDataElement(DataElement dataElement) {
		this.dataElement = dataElement;
	}
	
	public void setValue(String value) {
		this.value = value;
	}
	
	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((dataElement == null) ? 0 : dataElement.hashCode());
		result = prime
				* result
				+ ((organisationUnit == null) ? 0 : organisationUnit.hashCode());
		result = prime * result + ((period == null) ? 0 : period.hashCode());
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
		DataValue other = (DataValue) obj;
		if (dataElement == null) {
			if (other.dataElement != null)
				return false;
		} else if (!dataElement.equals(other.dataElement))
			return false;
		if (organisationUnit == null) {
			if (other.organisationUnit != null)
				return false;
		} else if (!organisationUnit.equals(other.organisationUnit))
			return false;
		if (period == null) {
			if (other.period != null)
				return false;
		} else if (!period.equals(other.period))
			return false;
		return true;
	}
	
	
}
