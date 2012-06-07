package org.chai.kevin.data;

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

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.chai.kevin.Exportable;
import org.chai.kevin.Importable;
import org.chai.kevin.Orderable;
import org.chai.kevin.Ordering;
import org.chai.kevin.Translation;
import org.chai.kevin.util.Utils;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity(name="EnumOption")
@Table(name="dhsst_enum_option", uniqueConstraints={@UniqueConstraint(columnNames={"code"})})
@Cache(usage=CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class EnumOption extends Orderable<Ordering> implements Exportable, Importable {

	private Long id;
	private String code;
	private String value;
	private Ordering order = new Ordering();
	private Translation names = new Translation();
	private Translation descriptions = new Translation();
	
	// TODO flag to deactivate option in survey, 
	// think about how to make that better
	// enum and enum options should not be directly linked to a survey
	private Boolean inactive = false;
	
	private Enum enume;

	@Id
	@GeneratedValue
	@Column
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	@Basic(fetch=FetchType.EAGER)
	public String getCode() {
		return code;
	}
	
	public void setCode(String code) {
		this.code = code;
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
	@Column(nullable=false)
	public String getValue() {
		return value;
	}
	
	public void setValue(String value) {
		this.value = value;
	}
	
	@ManyToOne(targetEntity=Enum.class)
	@JoinColumn(nullable=false)
	public Enum getEnume() {
		return enume;
	}
	
	public void setEnume(Enum enume) {
		this.enume = enume;
	}
	
	@Basic
	public Boolean getInactive() {
		return inactive;
	}
	
	public void setInactive(Boolean inactive) {
		this.inactive = inactive;
	}

	@Embedded
	@AttributeOverrides({ @AttributeOverride(name = "jsonText", column = @Column(name = "ordering", nullable = false)) })
	public Ordering getOrder() {
		return order;
	}
	
	public void setOrder(Ordering order) {
		this.order = order;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof EnumOption))
			return false;
		EnumOption other = (EnumOption) obj;
		if (getId() == null) {
			if (other.getId() != null)
				return false;
		} else if (!getId().equals(other.getId()))
			return false;
		return true;
	}	
	
	@Override
	public String toExportString() {
		return "[" + Utils.formatExportCode(getCode().toString()) + "]";
	}
	
	@Override
	public EnumOption fromExportString(Object value) {
		return (EnumOption) value;
	}
}
