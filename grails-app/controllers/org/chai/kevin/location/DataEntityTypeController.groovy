package org.chai.kevin.location;

import org.chai.kevin.AbstractEntityController;

class DataEntityTypeController extends AbstractEntityController {

	def bindParams(def entity) {
		entity.properties = params
		
		if (params.names!=null) entity.names = params.names
	}

	def getModel(def entity) {
		[dataEntityType: entity]
	}

	def getEntity(def id) {
		return DataEntityType.get(id);
	}

	def createEntity() {
		return new DataEntityType();
	}
	
	def deleteEntity(def entity) {
		if (DataLocationEntity.findAllByType(entity).size() != 0) {
			flash.message = message(code: 'dataEntityType.hasentities', args: [message(code: getLabel(), default: 'entity'), params.id], default: 'Type {0} still has associated entities.')
		}
		else {
			super.deleteEntity(entity)
		}
	}
	
	def getTemplate() {
		return '/entity/location/createDataEntityType'
	}

	def getLabel() {
		return 'dataentitytype.label';
	}
	
	def list = {
		adaptParamsForList()
		
		List<DataEntityType> types = DataEntityType.list(params);

		render (view: '/entity/list', model:[
			template:"location/dataEntityTypeList",
			entities: types,
			entityCount: DataEntityType.count(),
			code: getLabel()
		])
	}
}
