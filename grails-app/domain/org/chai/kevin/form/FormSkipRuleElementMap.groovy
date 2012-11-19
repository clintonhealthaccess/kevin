package org.chai.kevin.form

class FormSkipRuleElementMap implements Serializable {

	String skippedFormElements
	FormElement formElement
	
	static belongsTo = [skipRule: FormSkipRule]
	
	static mapping = {
		table 'dhsst_form_skipped_form_elements'
		id composite: ['formElement', 'skipRule']
		
		skippedFormElements column: 'skippedFormElements'
		formElement column: 'skippedFormElements_KEY'
		skipRule column: 'FormSkipRule'
		version false
	}
	
	static constraints = {
		skippedFormElements (nullable: false)
		formElement (nullable: false)
	}
	
}
