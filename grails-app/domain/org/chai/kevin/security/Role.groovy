package org.chai.kevin.security
class Role {
    String name
	String permissionString
	
    static hasMany = [ users: User ]
    static belongsTo = User

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
        name(nullable: false, blank: false, unique: true)
    }
}
