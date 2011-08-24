package org.chai.kevin.survey;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;


@Entity(name="SurveySkipRule")
@Table(name="dhsst_survey_skip_rule")
public class SurveySkipRule {

	private Long id;
	private Survey survey;
	private String expression;
	
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
	
	@ManyToMany(targetEntity=SurveyElement.class)
	@JoinTable(name="dhsst_survey_skipped_survey_elements")
	public Set<SurveyElement> getSkippedSurveyElements() {
		return skippedSurveyElements;
	}
	
	public void setSkippedSurveyElements(Set<SurveyElement> skippedSurveyElements) {
		this.skippedSurveyElements = skippedSurveyElements;
	}
	
	@ManyToMany(targetEntity=SurveyQuestion.class)
	@JoinTable(name="dhsst_survey_skipped_survey_questions")
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

	protected void deepCopy(SurveySkipRule copy, SurveyCloner surveyCloner) {
		copy.setExpression(surveyCloner.getExpression(getExpression(), copy));
		copy.setSurvey(surveyCloner.getSurvey(getSurvey()));
		for (SurveyQuestion question : getSkippedSurveyQuestions()) {
			copy.getSkippedSurveyQuestions().add(surveyCloner.getQuestion(question));
		}
		for (SurveyElement element : getSkippedSurveyElements()) {
			copy.getSkippedSurveyElements().add(surveyCloner.getElement(element));
		}
	}
	
}
