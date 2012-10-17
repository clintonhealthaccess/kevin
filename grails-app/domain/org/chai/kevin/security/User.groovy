package org.chai.kevin.security

import org.chai.location.DataLocation;
import org.chai.location.Location;
import org.chai.kevin.util.Utils;
import org.chai.kevin.security.PasswordToken;
import org.chai.kevin.security.RegistrationToken;

class User {

	static String PERMISSION_DELIMITER = ";"
	
	//Needed to enable cascading deletes, these fields should not be collections since
	//some code has been writen assuming its a one to many relationship
	RegistrationToken registrationToken
	PasswordToken passwordToken
	
	// TODO get rid of this, it is the uuid
	String code
	
	String email
    String username
	String uuid
    String passwordHash = ''
	String permissionString = ''
	Boolean confirmed = false
	Boolean active = false
	String defaultLanguage	
	String firstname, lastname, organisation, phoneNumber
	Long locationId
	UserType userType
	
	static hasMany = [ roles: Role ]
	
	User() {
		roles = []
	}
	
	def getLocation () {
		def location = Location.get(locationId) 
		if (location == null) location = DataLocation.get(locationId)
		return location
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
	
	def removeFromPermissions(def permission) {
		def permissions = getPermissions()
		permissions.remove(permission)
		this.permissionString = Utils.unsplit(permissions, User.PERMISSION_DELIMITER)
	}
	
	def setDefaultPermissions() {
		removeAllDefaultPermissions()
		addDefaultPermissions()
	}
	
	def setDefaultRoles() {
		removeAllDefaultRoles()
		addDefaultRoles()
	}
	
	private def addDefaultPermissions() {
		userType.defaultPermissions.each { permissionToAdd ->
			def permission = permissionToAdd.replaceAll('<id>', locationId+'')
			addToPermissions(permission)
		}
	}
	
	private def removeAllDefaultPermissions() {
		UserType.getAllPermissions().each { permissionToRemove ->
			def regexToCheck = permissionToRemove.replaceAll('\\*','\\\\*').replaceAll('<id>', "\\\\d*")
			permissions.each { permission ->
				if (permission.matches(regexToCheck)) {
					removeFromPermissions(permission)
				}
			}
		}
	}
	
	private def addDefaultRoles() {
		userType.defaultRoles.each { roleNameToAdd ->
			def roleToAdd = Role.findByName(roleNameToAdd)
			if (roleToAdd != null) roles.add(roleToAdd)
		}
	}
	
	private def removeAllDefaultRoles() {
		UserType.getAllRoles().each { roleNameToRemove ->
			def roleToRemove = Role.findByName(roleNameToRemove)
			if (roleToRemove != null) roles.remove(roleToRemove)
		}
	}	
	
	def canActivate() {
		return confirmed == true && active == false
	}
	
    static constraints = {
		// TODO get rid of this, it is the uuid
		code(nullable: false, blank: false, unique: true)
		
		email(email:true, unique: true, nullable: true)
        username(nullable: false, blank: false, unique: true)
		uuid(nullable: false, blank: false, unique: true)
		firstname(nullable: false, blank: false)
		lastname(nullable: false, blank: false)
		phoneNumber(phoneNumber: true, nullable: false, blank: false)
		organisation(nullable: false, blank: false)
		defaultLanguage(nullable: true)
		registrationToken(nullable: true)
		passwordToken(nullable: true)
		
		userType(nullable: false, blank: false)
		locationId(nullable: true, 
			validator: {val, obj -> 
				if (obj.userType != UserType.OTHER) {
					if (val == null) return false
					else {
						def location = DataLocation.get(val)
						if (location == null || (!(location instanceof DataLocation))) return false
					}
				}
				return true
			}
		)
    }
	
	static mapping = {
		cache true
	}
}
