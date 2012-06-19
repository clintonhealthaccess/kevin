package org.chai.kevin.security

import org.chai.kevin.util.Utils;

class User {
	
	String email
    String username
	// TODO get rid of this
	String code
	String uuid
    String passwordHash = ''
	String permissionString = ''
	Boolean confirmed = false
	Boolean active = false
	String defaultLanguage
	
	String location
	
	String phoneNumber
	String firstname, lastname, organisation 
	
    static hasMany = [ roles: Role ]

	def getPermissions() {
		return Utils.split(permissionString)
	}
	
	def setPermissions(def permissions) {
		this.permissionString = Utils.unsplit(permissions)
	}
	
	def addToPermissions(def permission) {
		def permissions = getPermissions()
		permissions << permission
		this.permissionString = Utils.unsplit(permissions)
	}
	
	def canActivate() {
		return confirmed == true && active == false
	}
	
    static constraints = {
		email(email:true, unique: true, nullable: true)
        username(nullable: false, blank: false, unique: true)
		uuid(nullable: false, blank: false, unique: true)
		code(nullable: false, blank: false, unique: true)
		firstname(nullable: false, blank: false)
		lastname(nullable: false, blank: false)
		phoneNumber(phoneNumber: true, nullable: false, blank: false)
		organisation(nullable: false, blank: false)
		location(nullable: true)
		defaultLanguage(nullable: true)
    }
	
	static mapping = {
		cache true
	}
}
