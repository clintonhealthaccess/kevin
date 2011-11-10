package org.chai.kevin.security

import org.chai.kevin.util.Utils;

class User {
	
	String email
    String username
    String passwordHash = ''
	String permissionString = ''
	Boolean confirmed = false
	Boolean active = false
	String uuid
	
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
		firstname(nullable: true)
		lastname(nullable: true)
		organisation(nullable: true)
		uuid(nullable: false, blank: false, unique: true)
    }
}
