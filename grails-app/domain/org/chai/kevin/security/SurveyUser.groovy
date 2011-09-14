package org.chai.kevin.security

import org.hisp.dhis.organisationunit.OrganisationUnit;

class SurveyUser extends User {

	// TODO assign several surveys to a user
	// instead of an organisation unit
	int organisationUnitId;

	static constraints = {
		organisationUnitId (nullable: false)	
	}
	
}
