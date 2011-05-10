package org.hisp.dhis.dataelement

constraints = {
	name(nullable: false, blank: false, unique: true, maxSize: 230)
	shortName(unique: true, maxSize: 25)
	value(nullable: false, blank: false)
}
