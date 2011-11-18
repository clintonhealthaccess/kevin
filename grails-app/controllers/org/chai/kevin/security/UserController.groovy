package org.chai.kevin.security

import org.chai.kevin.AbstractEntityController
import org.codehaus.groovy.grails.commons.ConfigurationHolder

class UserController extends AbstractEntityController {

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

	def getModel(def entity) {
		[user:entity, roles: Role.list()]
	}

	def bindParams(def entity) {
		entity.properties = params
	}
	
	def list = {
		params.max = Math.min(params.max ? params.int('max') : ConfigurationHolder.config.site.entity.list.max, 100)
		params.offset = params.offset ? params.int('offset'): 0
		List<User> users = User.list(params);

		render (view: '/entity/list', model:[
			template:"user/userList",
			entities: users,
			entityCount: User.count(),
			code: getLabel()
		])
	}
		
}
