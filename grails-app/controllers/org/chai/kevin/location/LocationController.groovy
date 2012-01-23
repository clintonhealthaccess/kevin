package org.chai.kevin.location;

import org.chai.kevin.AbstractEntityController;

class LocationController extends AbstractEntityController {

	def locationService
	
	def bindParams(def entity) {
		entity.properties = params
		
		if (params.names!=null) entity.names = params.names
	}

	def getModel(def entity) {
		def locations = []
		if (entity.parent != null) locations << entity.parent
		[location: entity, locations: locations, levels: LocationLevel.list()]
	}

	def getEntity(def id) {
		return LocationEntity.get(id);
	}

	def createEntity() {
		return new LocationEntity();
	}

	def deleteEntity(def entity) {
		if (entity.children.size() != 0) {
			flash.message = message(code: 'location.haschildren', args: [message(code: getLabel(), default: 'entity'), params.id], default: 'Location {0} still has associated children.')
		}
		else if (entity.dataEntities.size() != 0) {
			flash.message = message(code: 'location.hasdataentities', args: [message(code: getLabel(), default: 'entity'), params.id], default: 'Location {0} still has associated data entities.')
		}
		else {
			super.deleteEntity(entity)
		}
	}
	
	def getTemplate() {
		return '/entity/location/createLocation'
	}

	def getLabel() {
		return 'location.label';
	}

	def list = {
		adaptParamsForList()
		
		List<LocationEntity> locations = LocationEntity.list(params);

		render (view: '/entity/list', model:[
			template:"location/locationList",
			entities: locations,
			entityCount: LocationEntity.count(),
			code: getLabel()
		])
	}
	
	def getAjaxData = {
		def locations = locationService.searchLocation(LocationEntity.class, params['term'], [:])
		
		render(contentType:"text/json") {
			elements = array {
				locations.each { location ->
					elem (
						key: location.id,
						value: i18n(field:location.names)
					)
				}
			}
		}
	}
	
	def search = {
		adaptParamsForList()
		
		List<LocationEntity> locations = locationService.searchLocation(LocationEntity.class, params['q'], params)
				
		render (view: '/entity/list', model:[
			template:"location/locationList",
			entities: locations,
			entityCount: locationService.countLocation(LocationEntity.class, params['q']),
			code: getLabel()
		])
	}
	
}
