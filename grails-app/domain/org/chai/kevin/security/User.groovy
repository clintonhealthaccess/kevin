package org.chai.kevin.security

import org.chai.kevin.util.Utils;

class User {
	
    String username
    String passwordHash
	String permissionString
	    
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
	
    static constraints = {
        username(nullable: false, blank: false)
    }
}
