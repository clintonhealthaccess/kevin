package org.chai.kevin

constraints = {
	code(nullable: false, blank: false, unique: true)
	expression(nullable: false, blank: false, expressionValid: true)
	type(nullable: false)
	// shortName(blank: false, unique: true)
}
