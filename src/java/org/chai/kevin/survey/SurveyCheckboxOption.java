package org.chai.kevin.survey;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.chai.kevin.DataElement;

@SuppressWarnings("serial")
@Entity(name="SurveyCheckboxOption")
@Table(name="dhsst_survey_checkbox_element")
public class SurveyCheckboxOption extends SurveyTranslatable {
	
	private Integer id;
	private Integer order;
	private SurveyCheckboxQuestion question;
	private DataElement dataElement;
	
	@Id
	@GeneratedValue
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	@Basic
	@Column(name="ordering")
	public Integer getOrder() {
		return order;
	}
	public void setOrder(Integer order) {
		this.order = order;
	}
	//optional has to be set to true
	@ManyToOne(targetEntity=SurveyCheckboxQuestion.class, optional=true)
	public SurveyCheckboxQuestion getQuestion() {
		return question;
	}
	public void setQuestion(SurveyCheckboxQuestion question) {
		this.question = question;
	}
	public DataElement getDataElement() {
		return dataElement;
	}
	public void setDataElement(DataElement dataElement) {
		this.dataElement = dataElement;
	}
	
	
	

}
