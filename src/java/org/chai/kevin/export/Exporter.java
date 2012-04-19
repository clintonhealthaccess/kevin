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
package org.chai.kevin.export;

import java.util.Date;
import java.util.Set;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import org.chai.kevin.Period;
import org.chai.kevin.Translation;
import org.chai.kevin.data.Data;
import org.chai.kevin.location.DataLocation;
import org.chai.kevin.util.Utils;
import org.chai.kevin.value.DataValue;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * @author Jean Kahigiso M.
 *
 */
@Entity(name="Exporter")
@Table(name="dhsst_export")
@Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
public class Exporter {
	private Long id;
	private Translation names = new Translation();
	private Date date;
	private String typeCodeString;
	private Set<DataLocation> dataLocations; 
	private Set<Period> periods;
	private Set<Data<DataValue>> data;
	
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
		@AttributeOverride(name="jsonText", column=@Column(name="jsonNames", nullable=false))
	})
	public Translation getNames() {
		return names;
	}
	public void setNames(Translation names) {
		this.names = names;
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

	@Transient
	public Set<String> getTypeCodes() {
		return Utils.split(typeCodeString);
	}
	public void setTypeCodes(Set<String> typeCodes) {
		this.typeCodeString = Utils.unsplit(typeCodes);
	}
	
	@ManyToMany(targetEntity=Period.class)
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
	
	@ManyToMany(targetEntity=Data.class)
	@JoinTable(name="dhsst_export_data",
		joinColumns=@JoinColumn(name="exporter"),
		uniqueConstraints=@UniqueConstraint(columnNames={"exporter","data"})
	)
	public Set<Data<DataValue>> getData() {
		return data;
	}
	public void setData(Set<Data<DataValue>> data) {
		this.data = data;
	}
	
	public void setDataLocations(Set<DataLocation> dataLocations) {
		this.dataLocations = dataLocations;
	}
	
	@ManyToMany(targetEntity=DataLocation.class)
	@JoinTable(name="dhsst_export_data_locations",
		joinColumns=@JoinColumn(name="exporter"),
		uniqueConstraints=@UniqueConstraint(columnNames={"exporter","dataLocations"})
	)
	public Set<DataLocation> getDataLocations() {
		return dataLocations;
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
		Exporter other = (Exporter) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		return "Exporter [id=" + id + ", names=" + names + ", date=" + date
				+ "]";
	}
	
}
