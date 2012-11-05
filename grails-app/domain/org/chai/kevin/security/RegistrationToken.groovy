package org.chai.kevin.security

class RegistrationToken {
	String token
	Boolean used = false
	
	static belongsTo = [user:User]
	
	static constraints = {
	}
	
	static mapping = {
		table 'dhsst_security_registration_token'
	}
}
