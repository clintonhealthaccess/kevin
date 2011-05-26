package org.chai.kevin.cost

constraints = {
	code(nullable: false, blank: false, unique: true)
	expression (nullable: false)
	costRampUp (nullable: false)
	costType (nullable: false)
}
