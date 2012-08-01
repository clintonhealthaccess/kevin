package org.chai.kevin.security

class PasswordToken {
	String token
	
	static belongsTo = [user:User]
	
	static constraints = {
	}
}
