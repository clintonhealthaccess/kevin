package org.chai.kevin.form

import i18nfields.I18nFields;

import java.util.Map;

@I18nFields
class FormElementHeadersMap implements Serializable {
 
	String header
	String names
	
	static i18nFields = ['names']
	
	static belongsTo = [formElement: FormElement]
	
	static transients = ['namesMap']
	
	static mapping = {
		table 'dhsst_form_element_headers'
		id composite: ['header', 'formElement']
		
		header column: 'headers_KEY'
		formElement column: 'FormElement'
		version false
	}
	
	static constraints = {
		header (nullable: false)
		names (nullable: true)
	}
	
	void setNamesMap(Map<String, String> namesMap) {
		namesMap.each {
			setNames(it.value, new Locale(it.key))
		}
	}
	
}
