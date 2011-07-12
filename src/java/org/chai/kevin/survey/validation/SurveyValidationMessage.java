package org.chai.kevin.survey.validation;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.chai.kevin.Translation;

@Entity(name="SurveyValidationMessage")
@Table(name="dhsst_survey_validation_message")
public class SurveyValidationMessage {

	private Long id;
	private Translation messages;
	
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
	
}
