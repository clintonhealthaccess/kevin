package org.chai.kevin.security

class LoginLog {

	User user
	Date loginDate
	String username
	Boolean success
	
	static constraints = {
		user (nullable: true)
	}
}
