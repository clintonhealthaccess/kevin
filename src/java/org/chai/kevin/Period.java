package org.chai.kevin;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity(name="Period")
@Table(name="dhsst_period")
@Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
public class Period {

	private Long id;
	private Date startDate;
	private Date endDate;

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
	public Date getStartDate() {
		return startDate;
	}
	
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	
	@Column(nullable=false, columnDefinition="datetime")
	@Temporal(javax.persistence.TemporalType.TIMESTAMP)
	public Date getEndDate() {
		return endDate;
	}
	
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Period))
			return false;
		Period other = (Period) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Period [getId()=" + getId() + ", startDate=" + startDate + ", endDate=" + endDate + "]";
	}
	
}
