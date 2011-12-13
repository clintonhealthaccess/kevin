package org.chai.kevin.value;

/* 
 * Copyright (c) 2011, Clinton Health Access Initiative.
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the <organization> nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import java.util.Date;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.Transient;

import org.chai.kevin.data.Data;
import org.chai.kevin.location.CalculationEntity;
import org.hibernate.annotations.NaturalId;
import org.hisp.dhis.period.Period;

@MappedSuperclass
public abstract class StoredValue implements DataValue {

	protected CalculationEntity entity;
	protected Period period;
	protected Value value;

	private Date timestamp = new Date();
	
	public StoredValue(){
		// default constructor for hibernate
	}
	
	public StoredValue(CalculationEntity entity, Period period, Value value) {
		this.entity = entity;
		this.period = period;
		this.value = value;
	}

	/* (non-Javadoc)
	 * @see org.chai.kevin.value.DataValue#getOrganisationUnit()
	 */
	@Override
	@NaturalId
	@ManyToOne(targetEntity=CalculationEntity.class, fetch=FetchType.LAZY)
	@JoinColumn(nullable=false)
	public CalculationEntity getEntity() {
		return entity;
	}
	
	public void setEntity(CalculationEntity entity) {
		this.entity = entity;
	}
	
	/* (non-Javadoc)
	 * @see org.chai.kevin.value.DataValue#getPeriod()
	 */
	@Override
	@NaturalId
	@ManyToOne(targetEntity=Period.class, fetch=FetchType.LAZY)
	@JoinColumn(nullable=false)
	public Period getPeriod() {
		return period;
	}
	
	public void setPeriod(Period period) {
		this.period = period;
	}

	/* (non-Javadoc)
	 * @see org.chai.kevin.value.DataValue#getTimestamp()
	 */
	@Column(nullable=false, columnDefinition="datetime")
	@Temporal(javax.persistence.TemporalType.TIMESTAMP)
	public Date getTimestamp() {
		return timestamp;
	}
	
	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}
	
	/* (non-Javadoc)
	 * @see org.chai.kevin.value.DataValue#getValue()
	 */
	@Override
	@Embedded
	@AttributeOverrides({
        @AttributeOverride(name="jsonValue", column=@Column(name="value", nullable=false))
	})
	public Value getValue() {
		return value;
	}
	
	public void setValue(Value value) {
		this.value = value;
	}

	/* (non-Javadoc)
	 * @see org.chai.kevin.value.DataValue#getData()
	 */
	@Transient
	public abstract Data<?> getData();
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((getEntity() == null) ? 0 : getEntity().hashCode());
		result = prime * result + ((getPeriod() == null) ? 0 : getPeriod().getId());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof StoredValue))
			return false;
		DataValue other = (DataValue) obj;
		if (getEntity() == null) {
			if (other.getEntity() != null)
				return false;
		} else if (!getEntity().equals(other.getEntity()))
			return false;
		if (getPeriod() == null) {
			if (other.getPeriod() != null)
				return false;
		} else if (!getPeriod().equals(other.getPeriod()))
			return false;
		return true;
	}
	
}