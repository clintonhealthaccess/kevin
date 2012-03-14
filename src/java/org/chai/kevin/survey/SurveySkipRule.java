package org.chai.kevin.survey;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.chai.kevin.form.FormSkipRule;


@Entity(name="SurveySkipRule")
@Table(name="dhsst_survey_skip_rule")
public class SurveySkipRule extends FormSkipRule {

	private Survey survey;
	private Set<SurveyQuestion> skippedSurveyQuestions = new HashSet<SurveyQuestion>();
	
	@ManyToOne(targetEntity=Survey.class, fetch=FetchType.LAZY)
	@JoinColumn(nullable=false)
	public Survey getSurvey() {
		return survey;
	}
	
	public void setSurvey(Survey survey) {
		this.survey = survey;
	}
	
	@ManyToMany(targetEntity=SurveyQuestion.class)
	@JoinTable(name="dhsst_survey_skipped_survey_questions")
	public Set<SurveyQuestion> getSkippedSurveyQuestions() {
		return skippedSurveyQuestions;
	}
	
	public void setSkippedSurveyQuestions(Set<SurveyQuestion> skippedSurveyQuestions) {
		this.skippedSurveyQuestions = skippedSurveyQuestions;
	}

	protected void deepCopy(SurveySkipRule copy, SurveyCloner surveyCloner) {
		super.deepCopy(copy, surveyCloner);
		copy.setSurvey(surveyCloner.getSurvey(getSurvey()));
		for (SurveyQuestion question : getSkippedSurveyQuestions()) {
			copy.getSkippedSurveyQuestions().add(surveyCloner.getQuestion(question));
		}
	}

}
