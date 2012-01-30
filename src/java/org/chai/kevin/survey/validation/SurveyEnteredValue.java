package org.chai.kevin.survey.validation;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import org.chai.kevin.data.Type;
import org.chai.kevin.location.DataLocationEntity;
import org.chai.kevin.survey.Survey;
import org.chai.kevin.survey.SurveyElement;
import org.chai.kevin.value.ValidatableValue;
import org.chai.kevin.value.Value;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.NaturalId;

@Entity(name="SurveyEnteredValue")
@Table(name="dhsst_survey_entered_value", 
		uniqueConstraints=@UniqueConstraint(columnNames={"surveyElement", "entity"}
))
@Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
public class SurveyEnteredValue extends SurveyEnteredEntity {

	private Long id;
	private SurveyElement surveyElement;
	private Value value;
	private Value lastValue; //last year's value
	private DataLocationEntity entity;
	private ValidatableValue validatable;
	
	public SurveyEnteredValue() {}
	
	public SurveyEnteredValue(SurveyElement surveyElement, DataLocationEntity entity, Value value, Value lastValue) {
		this.surveyElement = surveyElement;
		this.entity = entity;
		this.value = value;
		this.lastValue = lastValue;
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
	@OneToOne(targetEntity=SurveyElement.class, fetch=FetchType.LAZY)
	public SurveyElement getSurveyElement() {
		return surveyElement;
	}
	
	public void setSurveyElement(SurveyElement surveyElement) {
		this.surveyElement = surveyElement;
	}
	
	@Embedded
	@AttributeOverrides({
        @AttributeOverride(name="jsonValue", column=@Column(name="value", nullable=false))
	})
	public Value getValue() {
		return value;
	}
	
	public void setValue(Value value) {
		this.value = value;
	}
	
	@Embedded
	@AttributeOverrides({
        @AttributeOverride(name="jsonValue", column=@Column(name="last_value", nullable=true))
	})
	public Value getLastValue() {
		return lastValue;
	}
	
	public void setLastValue(Value lastValue) {
		this.lastValue = lastValue;
	}
	
	@NaturalId
	@ManyToOne(targetEntity=DataLocationEntity.class, fetch=FetchType.LAZY)
	public DataLocationEntity getEntity() {
		return entity;
	}
	
	public void setEntity(DataLocationEntity entity) {
		this.entity = entity;
	}
	
	@Transient
	public ValidatableValue getValidatable() {
		if (validatable == null) validatable = new ValidatableValue(value, getSurveyElement().getDataElement().getType());
		return validatable;
	}
	
	@Transient
	public Survey getSurvey() {
		return surveyElement.getSurvey();
	}

	@Transient
	public Type getType() {
		return surveyElement.getDataElement().getType();
	}

	@Override
	public String toString() {
		return "SurveyEnteredValue [value=" + value + ", lastValue="
				+ lastValue + "]";
	}

}
