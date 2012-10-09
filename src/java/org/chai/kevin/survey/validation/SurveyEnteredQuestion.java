package org.chai.kevin.survey.validation;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import org.chai.kevin.form.EnteredEntity;
import org.chai.kevin.location.DataLocation;
import org.chai.kevin.survey.SurveyQuestion;
import org.chai.kevin.survey.SurveySkipRule;
import org.hibernate.annotations.NaturalId;

@Entity(name="SurveyEnteredQuestion")
@Table(name="dhsst_survey_entered_question", uniqueConstraints=@UniqueConstraint(
		columnNames={"question", "dataLocation"})
)
public class SurveyEnteredQuestion extends EnteredEntity {
	
	private Long id;
	private SurveyQuestion question;
	private DataLocation dataLocation;
	
	private Boolean complete;
	private Boolean invalid;
	private Set<SurveySkipRule> skipped = new HashSet<SurveySkipRule>();
	
	public SurveyEnteredQuestion() {}
	
	public SurveyEnteredQuestion(SurveyQuestion question, DataLocation dataLocation, Boolean invalid, Boolean complete) {
		super();
		this.question = question;
		this.dataLocation = dataLocation;
		this.complete = complete;
		this.invalid = invalid;
	}

	@Id
	@GeneratedValue
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	@NaturalId
	@ManyToOne(targetEntity=SurveyQuestion.class, fetch=FetchType.LAZY)
	public SurveyQuestion getQuestion() {
		return question;
	}
	
	public void setQuestion(SurveyQuestion question) {
		this.question = question;
	}
	
	@NaturalId
	@ManyToOne(targetEntity=DataLocation.class, fetch=FetchType.LAZY)
	public DataLocation getDataLocation() {
		return dataLocation;
	}
	
	public void setDataLocation(DataLocation dataLocation) {
		this.dataLocation = dataLocation;
	}
	
	@OneToMany(targetEntity=SurveySkipRule.class)
	@JoinTable(name="dhsst_survey_question_skipped")
	public Set<SurveySkipRule> getSkippedRules() {
		return skipped;
	}
	
	public void setSkippedRules(Set<SurveySkipRule> skipped) {
		this.skipped = skipped;
	}

	@Transient
	public boolean isSkipped() {
		return !skipped.isEmpty();
	}
	
	@Basic
	public Boolean isInvalid() {
		return invalid;
	}
	
	public void setInvalid(Boolean invalid) {
		this.invalid = invalid;
	}
	
	@Basic
	public Boolean isComplete() {
		return complete;
	}
	
	public void setComplete(Boolean complete) {
		this.complete = complete;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((dataLocation == null) ? 0 : dataLocation.hashCode());
		result = prime * result
				+ ((question == null) ? 0 : question.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof SurveyEnteredQuestion))
			return false;
		SurveyEnteredQuestion other = (SurveyEnteredQuestion) obj;
		if (dataLocation == null) {
			if (other.dataLocation != null)
				return false;
		} else if (!dataLocation.equals(other.dataLocation))
			return false;
		if (question == null) {
			if (other.question != null)
				return false;
		} else if (!question.equals(other.question))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "SurveyEnteredQuestion [complete=" + complete + ", invalid=" + invalid + ", skipped=" + skipped + "]";
	}
	
}
