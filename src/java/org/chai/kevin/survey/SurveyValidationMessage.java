package org.chai.kevin.survey;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.chai.kevin.Translation;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

@Entity(name="SurveyValidationMessage")
@Table(name="dhsst_survey_validation_message")
public class SurveyValidationMessage {

	private Long id;
	private Translation messages = new Translation();
	private List<SurveyValidationRule> validationRules = new ArrayList<SurveyValidationRule>();
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
    @AttributeOverride(name="jsonText", column=@Column(name="jsonMessages", nullable=false))
	})
	public Translation getMessages() {
		return messages;
	}
	
	public void setMessages(Translation messages) {
		this.messages = messages;
	}

	public void setValidationRules(List<SurveyValidationRule> validationRules) {
		this.validationRules = validationRules;
	}
    @OneToMany(targetEntity= SurveyValidationRule.class, mappedBy="validationMessage")
    @Cascade(CascadeType.ALL)
	public List<SurveyValidationRule> getValidationRules() {
		return validationRules;
	}
	
	public void addValidationRule(SurveyValidationRule validationRule){
		validationRules.add(validationRule);
	}
	
}
