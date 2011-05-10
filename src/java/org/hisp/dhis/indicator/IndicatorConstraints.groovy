package org.hisp.dhis.indicator

constraints = {
	name(blank: false, unique: true, maxSize: 230)
	shortName(blank: false, unique: true, maxSize: 25)
	numerator(blank: false, expressionValid: true)
	denominator(blank: false, expressionValid: true)
}
