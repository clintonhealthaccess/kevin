package org.chai.kevin.survey.validation;

import java.util.HashSet;
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
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.chai.kevin.Translation;
import org.chai.kevin.survey.Survey;
import org.chai.kevin.survey.SurveyElement;
import org.chai.kevin.survey.SurveyQuestion;

@Entity(name="SurveySkipRule")
@Table(name="dhsst_survey_skip_rule")
public class SurveySkipRule {

	private Long id;
	private Survey survey;
	private String expression;
	private Translation descriptions = new Translation();
	
	private Set<SurveyElement> skippedSurveyElements = new HashSet<SurveyElement>();
	private Set<SurveyQuestion> skippedSurveyQuestions = new HashSet<SurveyQuestion>();
	
	@Id
	@GeneratedValue
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	@ManyToOne(targetEntity=Survey.class, optional=false)
	@JoinColumn(nullable=false)
	public Survey getSurvey() {
		return survey;
	}
	
	public void setSurvey(Survey survey) {
		this.survey = survey;
	}
	
	@Basic(optional=false)
	@Column(nullable=false)
	public String getExpression() {
		return expression;
	}
	
	public void setExpression(String expression) {
		this.expression = expression;
	}
	
	public void setDescriptions(Translation descriptions) {
		this.descriptions = descriptions;
	}
	@Embedded
	@AttributeOverrides({ @AttributeOverride(name = "jsonText", column = @Column(name = "jsonDescriptions", nullable = false)) })
	public Translation getDescriptions() {
		return descriptions;
	}

	@ManyToMany(targetEntity=SurveyElement.class)
	public Set<SurveyElement> getSkippedSurveyElements() {
		return skippedSurveyElements;
	}
	
	public void setSkippedSurveyElements(Set<SurveyElement> skippedSurveyElements) {
		this.skippedSurveyElements = skippedSurveyElements;
	}
	
	@ManyToMany(targetEntity=SurveyQuestion.class)
	public Set<SurveyQuestion> getSkippedSurveyQuestions() {
		return skippedSurveyQuestions;
	}
	
	public void setSkippedSurveyQuestions(Set<SurveyQuestion> skippedSurveyQuestions) {
		this.skippedSurveyQuestions = skippedSurveyQuestions;
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
		SurveySkipRule other = (SurveySkipRule) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	
	
	
}
