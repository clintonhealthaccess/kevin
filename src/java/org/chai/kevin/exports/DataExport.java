/**
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
package org.chai.kevin.exports;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import org.chai.kevin.Period;
import org.chai.kevin.Translation;
import org.chai.kevin.location.CalculationLocation;
import org.chai.kevin.location.DataLocationType;
import org.chai.kevin.util.Utils;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * @author Jean Kahigiso M.
 *
 */
@Entity(name="Exporter")
@Table(name="dhsst_export")
@Inheritance(strategy=InheritanceType.JOINED)
@Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
public abstract class DataExport {
	
	private Long id;
	private Translation descriptions = new Translation();
	private Date date;
	private String typeCodeString;
	private Set<CalculationLocation> locations = new HashSet<CalculationLocation>();
	private Set<Period> periods = new HashSet<Period>();
	
	@Id
	@GeneratedValue
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
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
	
	@Basic(optional=false)
    @Temporal(TemporalType.TIMESTAMP)
	public Date getDate() {
		return date;
	}
	
	public void setDate(Date date) {
		this.date = date;
	}
	
	@Lob
	public String getTypeCodeString() {
		return typeCodeString;
	}
	
	public void setTypeCodeString(String typeCodeString) {
		this.typeCodeString = typeCodeString;
	}
	
	@ManyToMany(targetEntity=CalculationLocation.class, fetch=FetchType.LAZY)
	@JoinTable(name="dhsst_export_locations",
		joinColumns=@JoinColumn(name="exporter"),
		uniqueConstraints=@UniqueConstraint(columnNames={"exporter","locations"})
	)
	public Set<CalculationLocation> getLocations() {
		return locations;
	}
	
	public void setLocations(Set<CalculationLocation> locations) {
		this.locations = locations;
	}
	
	@Transient
	public Set<String> getTypeCodes() {
		return Utils.split(typeCodeString, DataLocationType.DEFAULT_CODE_DELIMITER);
	}
	
	public void setTypeCodes(Set<String> typeCodes) {
		this.typeCodeString = Utils.unsplit(typeCodes, DataLocationType.DEFAULT_CODE_DELIMITER);
	}
	
	@ManyToMany(targetEntity=Period.class, fetch=FetchType.LAZY)
	@JoinTable(name="dhsst_export_periods",
		joinColumns=@JoinColumn(name="exporter"),
		uniqueConstraints=@UniqueConstraint(columnNames={"exporter","periods"})
	)
	public Set<Period> getPeriods() {
		return periods;
	}
	
	public void setPeriods(Set<Period> periods) {
		this.periods = periods;
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
		if (getClass() != obj.getClass())
			return false;
		DataExport other = (DataExport) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	
	public abstract String toString();
}
