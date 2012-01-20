package org.chai.kevin.survey.wizard;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.chai.kevin.Translation;

@Entity
@Table(name="dhsst_survey_workflow_step")
public class WizardStep {

	private Long id;
	private String prefix;
	private Wizard wizard;
	private Translation description = new Translation();
	
	@Id
	@GeneratedValue
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	@Basic
	public String getPrefix() {
		return prefix;
	}
	
	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}
	
	@ManyToOne(targetEntity=Wizard.class)
	public Wizard getWizard() {
		return wizard;
	}
	
	public void setWizard(Wizard wizard) {
		this.wizard = wizard;
	}
	
	@Embedded
	@AttributeOverrides({ @AttributeOverride(name = "jsonText", column = @Column(name = "jsonDescriptions", nullable = false)) })
	public Translation getDescription() {
		return description;
	}
	
	public void setDescription(Translation description) {
		this.description = description;
	}
	
}
