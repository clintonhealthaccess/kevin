package org.chai.kevin.security

import org.chai.kevin.location.DataLocation;
import org.chai.kevin.location.Location;
import org.chai.kevin.util.Utils;

class User {

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
	
	def getLocation () {
		def location = Location.get(locationId) 
		if (location == null) location = DataLocation.get(locationId)
		return location
	}
	
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
	
	def removeFromPermissions(def permission) {
		def permissions = getPermissions()
		permissions.remove(permission)
		this.permissionString = Utils.unsplit(permissions)
	}
	
	def setDefaultPermissions() {
		removeAllDefaultPermissions()
		addDefaultPermissions()
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
