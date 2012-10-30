package org.chai.kevin.form

class FormValidationRuleDependency {

	FormElement formElement
	static belongsTo = [validationRule: FormValidationRule]
	
	static mapping = {
		table 'dhsst_form_validation_dependencies'
		
		formElement column: 'dependencies'
		validationRule column: 'dhsst_form_validation_rule'
		version false
	}
	
	static constraints = {
		formElement (nullable: false)
	}
	
}
