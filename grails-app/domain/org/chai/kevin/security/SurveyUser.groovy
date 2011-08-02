package org.chai.kevin.security

import org.hisp.dhis.organisationunit.OrganisationUnit;

class SurveyUser extends User {

	// TODO assign several surveys to a user
	// instead of an organisation unit
	OrganisationUnit organisationUnit;
	
	static constraints = {
		organisationUnit(nullable: false)	
	}
	
}
