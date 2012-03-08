package org.chai.kevin.security

import org.chai.kevin.location.DataLocationEntity;

class SurveyUserController  extends UserAbstractController {

	def getEntity(def id) {
		return SurveyUser.get(id)
	}

	def createEntity() {
		return new SurveyUser()
	}

	def getLabel() {
		return 'survey.user.label'
	}
	
	def getTemplate() {
		return "/entity/user/createSurveyUser"
	}
	
	def getModel(def entity) {
		def dataLocations = []
		if (entity.dataLocation != null) dataLocations << entity.dataLocation
		[
			user:entity, 
			roles: Role.list(), 
			dataLocations: dataLocations ,
			cmd: params['cmd']
			]
	}

}

