package org.chai.kevin.security

import org.chai.kevin.location.DataLocationEntity;

class SurveyUser extends User {

	// TODO assign several surveys to a user
	// instead of an location unit
	Long entityId;

	def getDataLocation () {
		return DataLocationEntity.get(entityId)
	}
	
	static constraints = {
		entityId (nullable: false)	
	}
	
}
