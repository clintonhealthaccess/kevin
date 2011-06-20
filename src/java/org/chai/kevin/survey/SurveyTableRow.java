package org.chai.kevin.survey;

import java.util.Map;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.chai.kevin.DataElement;

@SuppressWarnings("serial")
@Entity(name = "SurveyTableRow")
@Table(name = "dhsst_survey_table_row")
public class SurveyTableRow extends SurveyTranslatable {

	private Integer id;
	private Integer order;
	private SurveyTableQuestion question;
	private Map<SurveyTableColumn,DataElement> dataElements;


	public void setId(Integer id) {
		this.id = id;
	}

	@Id
	@GeneratedValue
	public Integer getId() {
		return id;
	}

	public void setOrder(Integer order) {
		this.order = order;
	}

	@Basic
	@Column(name = "ordering")
	public Integer getOrder() {
		return order;
	}

	public void setQuestion(SurveyTableQuestion question) {
		this.question = question;
	}

	@ManyToOne(targetEntity = SurveyTableQuestion.class, optional = false)
	public SurveyTableQuestion getQuestion() {
		return question;
	}

	public void setDataElements(Map<SurveyTableColumn,DataElement> dataElements) {
		this.dataElements = dataElements;
	}
    @OneToMany(targetEntity=DataElement.class)
	public Map<SurveyTableColumn,DataElement> getDataElements() {
		return dataElements;
	}


}
