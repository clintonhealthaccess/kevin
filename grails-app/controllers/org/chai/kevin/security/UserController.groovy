package org.chai.kevin.security

class UserController extends UserAbstractController {

	def getEntity(def id) {
		return User.get(id)
	}

	def createEntity() {
		return new User()
	}

	def getLabel() {
		return 'user.label'
	}
	
	def getTemplate() {
		return "/entity/user/createUser"
	}
	
}