package org.chai.kevin.location;

import org.chai.kevin.AbstractEntityController;

class DataLocationController extends AbstractEntityController {

	def locationService
	
	def bindParams(def entity) {
		entity.properties = params
		
		if (params.names!=null) entity.names = params.names
	}

	def getModel(def entity) {
		def locations = []
		if (entity.location != null) locations << entity.location
		[location: entity, types: DataEntityType.list(), locations: locations]
	}

	def getEntity(def id) {
		return DataLocationEntity.get(id);
	}

	def createEntity() {
		return new DataLocationEntity();
	}

	def getTemplate() {
		return '/entity/location/createDataLocation'
	}

	def getLabel() {
		return 'dataLocation.label';
	}

	def list = {
		adaptParamsForList()
		
		List<DataLocationEntity> locations = DataLocationEntity.list(params);

		render (view: '/entity/list', model:[
			template:"location/dataLocationList",
			entities: locations,
			entityCount: DataLocationEntity.count(),
			code: getLabel()
		])
	}
	
	def search = {
		adaptParamsForList()
		
		List<DataLocationEntity> locations = locationService.searchLocation(DataLocationEntity.class, params['q'], params)
				
		render (view: '/entity/list', model:[
			template:"location/dataLocationList",
			entities: locations,
			entityCount: locationService.countLocation(DataLocationEntity.class, params['q']),
			code: getLabel()
		])
	}
	
}
