package org.chai.kevin.security

import org.apache.shiro.crypto.hash.Sha256Hash
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
		[user:entity, roles: Role.list(), cmd: params['cmd']]
	}

	def validateEntity(def entity) {
		return entity.validate() && params['cmd'].validate()
	}
	
	def bindParams(def entity) {
		if (log.isDebugEnabled()) log.debug('binding params: '+params)
		bindData(entity,params,[exclude:['uuid','passwordHash']])
		
		if(entity.id==null)
			entity.uuid = UUID.randomUUID().toString();
			
		if(params['cmd']?.password != null && !params['cmd']?.password.equals(''))
			entity.passwordHash = new Sha256Hash(params['cmd'].password).toHex();
	}
	
	def list = {
		adaptParamsForList()
		
		List<User> users = User.list(params);

		render (view: '/entity/list', model:[
			template:"user/userList",
			entities: users,
			entityCount: User.count(),
			code: getLabel()
		])
	}
	
	def save = { PasswordCommand cmd ->
		if (log.isDebugEnabled()) log.debug("create.userPassword, params:"+params+"command"+cmd)
		params['cmd'] = cmd;
		super.save()

	}
	
		
}
class PasswordCommand {
	String password
	String repeat

	static constraints = {
		password(blank: true, minSize: 4)
		repeat(validator: {val, obj ->
			val == obj.password
		})
	}
	
	String toString() {
		return "PasswordCommand[password="+password+", repeat="+repeat+"]"
	}
}
