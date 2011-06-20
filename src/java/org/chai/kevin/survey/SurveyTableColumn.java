package org.chai.kevin.survey;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;


@SuppressWarnings("serial")
@Entity(name = "SurveyTableColumn")
@Table(name = "dhsst_survey_table_column")
public class SurveyTableColumn extends SurveyTranslatable {

	private Integer id;
	private Integer order;
	private SurveyTableQuestion question;

	@Id
	@GeneratedValue
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@Basic
	@Column(name = "ordering")
	public Integer getOrder() {
		return order;
	}

	public void setOrder(Integer order) {
		this.order = order;
	}

	@ManyToOne(targetEntity = SurveyTableQuestion.class, optional = false)
	public SurveyTableQuestion getQuestion() {
		return question;
	}

	public void setQuestion(SurveyTableQuestion question) {
		this.question = question;
	}

}
