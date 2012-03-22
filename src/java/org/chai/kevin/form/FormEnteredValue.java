package org.chai.kevin.form;

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
import org.chai.kevin.value.ValidatableValue;
import org.chai.kevin.value.Value;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.NaturalId;

@Entity(name="FormEnteredValue")
@Table(name="dhsst_form_entered_value", 
		uniqueConstraints=@UniqueConstraint(columnNames={"formElement", "entity"}
))
@Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
public class FormEnteredValue extends EnteredEntity {

	private Long id;
	private FormElement formElement;
	private Value value;
	private Value lastValue; //last year's value
	private DataLocationEntity entity;
	private ValidatableValue validatable;
	
	public FormEnteredValue() {}
	
	public FormEnteredValue(FormElement formElement, DataLocationEntity entity, Value value, Value lastValue) {
		this.formElement = formElement;
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
	@OneToOne(targetEntity=FormElement.class, fetch=FetchType.LAZY)
	public FormElement getFormElement() {
		return formElement;
	}
	
	public void setFormElement(FormElement formElement) {
		this.formElement = formElement;
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
		if (validatable == null) validatable = new ValidatableValue(value, formElement.getDataElement().getType());
		return validatable;
	}
	
	@Transient
	public Type getType() {
		return formElement.getDataElement().getType();
	}

	@Override
	public String toString() {
		return "FormEnteredValue [value=" + value + ", lastValue="
				+ lastValue + "]";
	}

}
