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
import org.chai.location.DataLocation;
import org.chai.kevin.value.ValidatableValue;
import org.chai.kevin.value.Value;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.NaturalId;

@Entity(name="FormEnteredValue")
@Table(name="dhsst_form_entered_value", 
		uniqueConstraints=@UniqueConstraint(columnNames={"formElement", "dataLocation"}
))
@Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
public class FormEnteredValue extends EnteredEntity {

	private Long id;
	private FormElement formElement;
	private Value value;
	private Value lastValue; //last year's value
	private DataLocation dataLocation;
	private ValidatableValue validatable;
	
	public FormEnteredValue() {}
	
	public FormEnteredValue(FormElement formElement, DataLocation dataLocation, Value value, Value lastValue) {
		this.formElement = formElement;
		this.dataLocation = dataLocation;
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
	@ManyToOne(targetEntity=DataLocation.class, fetch=FetchType.LAZY)
	public DataLocation getDataLocation() {
		return dataLocation;
	}
	
	public void setDataLocation(DataLocation dataLocation) {
		this.dataLocation = dataLocation;
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((dataLocation == null) ? 0 : dataLocation.hashCode());
		result = prime * result
				+ ((formElement == null) ? 0 : formElement.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof FormEnteredValue))
			return false;
		FormEnteredValue other = (FormEnteredValue) obj;
		if (dataLocation == null) {
			if (other.dataLocation != null)
				return false;
		} else if (!dataLocation.equals(other.dataLocation))
			return false;
		if (formElement == null) {
			if (other.formElement != null)
				return false;
		} else if (!formElement.equals(other.formElement))
			return false;
		return true;
	}

}
