package org.chai.kevin.security

import org.apache.shiro.crypto.hash.Sha256Hash
import org.chai.kevin.AbstractEntityController
import org.codehaus.groovy.grails.commons.ConfigurationHolder
import org.chai.kevin.security.DataUser;

abstract class UserAbstractController extends AbstractEntityController {

	def getModel(def entity) {
		[user:entity, roles: Role.list(), cmd: params['cmd']]
	}

	def validateEntity(def entity) {
		boolean valid = entity.validate() && params['cmd'].validate()
		if (log.isDebugEnabled()) log.debug ("validation for command object ${params['cmd']}: ${params['cmd'].errors}}")
		return valid;
	}
	
	def bindParams(def entity) {
		if (log.isDebugEnabled()) log.debug('binding params: '+params)
		bindData(entity,params,[exclude:['uuid','passwordHash']])
		
		if(entity.id==null)
			entity.uuid = UUID.randomUUID().toString();
					
		if(params['cmd']?.password != null && !params['cmd']?.password.equals(''))
			entity.passwordHash = new Sha256Hash(params['cmd'].password).toHex();
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
		password(blank: true,nullable:true, minSize: 4)
		repeat(nullable:true, validator: {val, obj ->
			val == obj.password
		})
	}
	
	String toString() {
		return "PasswordCommand[password="+password+", repeat="+repeat+"]"
	}
}
