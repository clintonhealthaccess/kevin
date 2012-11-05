package org.chai.kevin.security

class PasswordToken {
	String token
	
	static belongsTo = [user:User]
	
	static constraints = {
	}
	
	static mapping = {
		table 'dhsst_security_password_token'
	}
}
