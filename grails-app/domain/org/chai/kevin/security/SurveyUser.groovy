package org.chai.kevin.security

import org.chai.kevin.location.DataLocation;

class SurveyUser extends User {

	// TODO assign several surveys to a user
	// instead of an location unit
	Long dataLocationId;

	def getDataLocation () {
		return DataLocation.get(dataLocationId)
	}
	
	static constraints = {
		dataLocationId (nullable: false)	
	}
	
}
