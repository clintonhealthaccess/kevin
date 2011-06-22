package org.chai.kevin;

import java.io.Serializable;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.FetchType;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class Translatable implements Serializable {

	private static final long serialVersionUID = 5282731214725130450L;
	
	protected Translation names = new Translation();
	protected Translation descriptions = new Translation();

	protected String code;
	
	@Embedded
	@AttributeOverrides({
		@AttributeOverride(name="jsonText", column=@Column(name="jsonNames", nullable=false))
	})
	public Translation getNames() {
		return names;
	}
	
	@Embedded
	@AttributeOverrides({
        @AttributeOverride(name="jsonText", column=@Column(name="jsonDescriptions", nullable=false))
	})
	public Translation getDescriptions() {
		return descriptions;
	}

	@Basic(fetch=FetchType.EAGER)
	public String getCode() {
		return code;
	}
	

	public void setCode(String code) {
		this.code = code;
	}

	public void setNames(Translation names) {
		this.names = names;
	}
	
	public void setDescriptions(Translation descriptions) {
		this.descriptions = descriptions;
	}

}
