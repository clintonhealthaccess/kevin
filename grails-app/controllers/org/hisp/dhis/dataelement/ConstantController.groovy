package org.hisp.dhis.dataelement

import org.chai.kevin.AbstractEntityController;
import org.hisp.dhis.dataelement.Constant;
import org.chai.kevin.DataElement;

class ConstantController extends AbstractEntityController {
	
	def getEntity(def id) {
		return Constant.get(id)
	}
	
	def createEntity() {
		return new Constant()
	}
	
	def getTemplate() {
		return "createConstant";
	}
	
	def getModel(def entity) {
		return [constant: entity]
	}

	def validate(def entity) {
		return entity.validate()
	}
	
	def save(def entity) {
		entity.save()
	}
	
	def bindParams(def entity) {
		entity.properties = params
	}
	
	def list = {
		params.max = Math.min(params.max ? params.int('max') : 10, 100)
		[constants: Constant.list(params), constantCount: Constant.count()]
	}
	
}
