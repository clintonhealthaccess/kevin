package org.chai.kevin.survey;

import java.io.Serializable;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.MappedSuperclass;

import org.chai.kevin.Translation;

@MappedSuperclass
public class SurveyTranslatable implements Serializable {

	private static final long serialVersionUID = -6838401416857236741L;

	protected Translation names = new Translation();
	protected Translation descriptions = new Translation();
	
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

	public void setNames(Translation names) {
		this.names = names;
	}
	
	public void setDescriptions(Translation descriptions) {
		this.descriptions = descriptions;
	}

}
