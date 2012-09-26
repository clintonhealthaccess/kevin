package org.chai.kevin.data;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.chai.kevin.Translation;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity(name="Source")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Table(name="dhsst_data_source")
public class Source {

	private Long id;
	private String code;
	private Translation names = new Translation();
	private Translation descriptions = new Translation();
	
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
	public String getCode() {
		return code;
	}
	
	public void setCode(String code) {
		this.code = code;
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
		if (!(obj instanceof Source))
			return false;
		Source other = (Source) obj;
		if (code == null) {
			if (other.code != null)
				return false;
		} else if (!code.equals(other.code))
			return false;
		return true;
	}
	
}
