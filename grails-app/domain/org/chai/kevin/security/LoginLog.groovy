package org.chai.kevin.security

class LoginLog {

	User user
	Date loginDate
	String username
	String ipAddress
	Boolean success
	
	static constraints = {
		user (nullable: true)
		ipAddress (nullable: true)
	}
	
	static mapping = {
		table 'dhsst_security_login_log'
	}
}
