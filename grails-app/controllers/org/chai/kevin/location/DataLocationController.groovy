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
		[location: entity, types: DataLocationType.list([cache: true]), locations: locations]
	}

	def getEntityClass(){
		return DataLocation.class;
	}
	
	def getEntity(def id) {
		return DataLocation.get(id);
	}

	def createEntity() {
		return new DataLocation();
	}

	def getTemplate() {
		return '/entity/location/createDataLocation'
	}

	def getLabel() {
		return 'datalocation.label';
	}

	def list = {
		adaptParamsForList()
		
		List<DataLocation> locations = DataLocation.list(params);

		render (view: '/entity/list', model:[
			template:"location/dataLocationList",
			entities: locations,
			entityCount: DataLocation.count(),
			code: getLabel(),
			entityClass: getEntityClass()
		])
	}
	
	def search = {
		adaptParamsForList()
		
		List<DataLocation> locations = locationService.searchLocation(DataLocation.class, params['q'], params)
				
		render (view: '/entity/list', model:[
			template:"location/dataLocationList",
			entities: locations,
			entityCount: locationService.countLocation(DataLocation.class, params['q']),
			code: getLabel()
		])
	}
	
	def getAjaxData = {
		def dataLocations = locationService.searchLocation(DataLocation.class, params['term'], [:])
		
		render(contentType:"text/json") {
			elements = array {
				dataLocations.each { dataLocation ->
					elem (
						key: dataLocation.id,
						value: i18n(field:dataLocation.location.names)+" - "+i18n(field:dataLocation.names)
					)
				}
			}
		}
	}
	
}
