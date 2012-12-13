package org.chai.kevin.form;

import groovy.transform.EqualsAndHashCode;

import org.chai.kevin.data.Type

import org.chai.kevin.value.ValidatableValue
import org.chai.kevin.value.Value
import org.chai.location.DataLocation

//@EqualsAndHashCode(includes=['formElement', 'dataLocation'])
class FormEnteredValue {

	FormElement formElement;
	DataLocation dataLocation;
	String valueString
	String lastValueString
	
	String userUuid
	Date timestamp
		
	static mapping = {
		table 'dhsst_form_entered_value'
		cache true
		
		id natural:['dataLocation', 'formElement']
		formElement index: 'Form_Value_Index', column: 'formElement'
		dataLocation index: 'Form_Value_Index', column: 'dataLocation'
		valueString column: 'value', sqlType: 'text'
		lastValueString column: 'last_value', sqlType: 'text'
	}
	
	static constraints = {
		formElement (nullable: false, unique: ['formElement', 'dataLocation'])
		value (nullable: false)
		dataLocation (nullable: false)
		valueString (nullable: false)
		lastValueString (nullable: true)
		
		userUuid (nullable: true)
		timestamp (nullable: true)
	}
	
	Value cachedValue;
	Value cachedLastValue; //last year's value
	ValidatableValue validatable;
	
	static transients = ['validatable', 'cachedValue', 'value', 'cachedLastValue', 'lastValue']
	
	public FormEnteredValue() {}
	
	public FormEnteredValue(FormElement formElement, DataLocation dataLocation, Value value, Value lastValue) {
		this.formElement = formElement;
		this.dataLocation = dataLocation;
		this.value = value;
		this.lastValue = lastValue;
	}

	/*
	 * Retaining backward compatibility with old getters and setters
	 */
	Value getValue() {
		if (valueString != null && cachedValue == null) this.cachedValue = new Value(valueString)
		return cachedValue
	}
	
	void setValue(Value value) {
		this.cachedValue = value
		this.valueString = value?.jsonValue
	}
	
	void setValueString(String valueString) {
		this.valueString = valueString
		this.cachedValue = null
	}
	
	void updateFromValidatable() {
		if (validatable != null) setValue(validatable.value)
		validatable = null
	}
	
	/*
	* Retaining backward compatibility with old getters and setters
	*/
	Value getLastValue() {
		if (lastValueString != null && cachedLastValue == null) this.cachedLastValue = new Value(lastValueString)
		return cachedLastValue
	}
   
	void setLastValue(Value lastValue) {
		this.cachedLastValue = lastValue
		this.lastValueString = lastValue?.jsonValue
	}

	void setLastValueString(String lastValueString) {
		this.lastValueString = lastValueString
		this.cachedLastValue = null
	}

   	public ValidatableValue getValidatable() {
		if (validatable == null) validatable = new ValidatableValue(value, formElement.getDataElement().getType());
		return validatable;
	}
	
	public Type getType() {
		return formElement.getDataElement().getType();
	}

	@Override
	public String toString() {
		return "FormEnteredValue [formElement=" + formElement+ ", value=" + value + ", lastValue=" + lastValue + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((dataLocation == null) ? 0 : dataLocation.hashCode());
		result = prime * result + ((formElement == null) ? 0 : formElement.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this.is(obj))
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
