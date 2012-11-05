package org.chai.kevin.security

import groovy.transform.EqualsAndHashCode;

import org.chai.kevin.util.Utils;

@EqualsAndHashCode(includes='name')
class Role {

    String name
	String permissionString
	
    static hasMany = [ users: User ]
    static belongsTo = User

	static mapping = {
		table 'dhsst_security_role'
	}
	
	def getPermissions() {
		return Utils.split(permissionString, User.PERMISSION_DELIMITER)
	}
	
	def setPermissions(def permissions) {
		this.permissionString = Utils.unsplit(permissions, User.PERMISSION_DELIMITER)
	}
	
	def addToPermissions(def permission) {
		def permissions = getPermissions()
		permissions << permission
		this.permissionString = Utils.unsplit(permissions, User.PERMISSION_DELIMITER)
	}
	
    static constraints = {
        name(nullable: false, blank: false, unique: true)
    }
	
	String toString() {
		return name;
	}

}
