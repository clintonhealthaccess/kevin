package org.chai.kevin.location;

import org.chai.kevin.AbstractEntityController;
import org.chai.kevin.value.RawDataElementValue;
import org.chai.location.DataLocation;
import org.chai.location.DataLocationType;
import org.chai.location.Location;

class DataLocationController extends AbstractEntityController {

	def locationService
	def valueService
	def formElementService
	def surveyValueService
	
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
	
	def deleteEntity(def entity) {
		// we delete the entity only if there are no associated values
		// should we throw an exception in case we can't delete ?
		if (valueService.getNumberOfValues(entity, RawDataElementValue.class) != 0) {
			flash.message = message(code: "datalocation.delete.hasvalues", default: "Could not delete data location, it still has values");
		}
		else {
			// we delete all the values (there will be none of RawDataElementValue class)
			valueService.deleteValues(null, entity, null)
			
			// we delete all form entered values
			formElementService.deleteEnteredValues(entity)
			surveyValueService.deleteEnteredQuestions(entity)
			surveyValueService.deleteEnteredSections(entity)
			surveyValueService.deleteEnteredPrograms(entity)
			
			// we delete the entity
			if (entity.type != null) entity.type.removeFromDataLocations(entity)
			if (entity.location != null) entity.location.removeFromDataLocations(entity)
			entity.delete()
		}
	}

	def getDescription = {
		def dataLocation = DataLocation.get(params.int('id'))

		if (dataLocation == null) {
			render(contentType:"text/json") { result = 'error' }
		}
		else {
			render(contentType:"text/json") {
				result = 'success'
				html = g.render (template: '/entity/location/dataLocationDescription', model: [dataLocation: dataLocation])
			}
		}
	}

	def list = {
		adaptParamsForList()
		
		def location = Location.get(params.int('location'))
		def type = DataLocationType.get(params.int('type'))
		
		def locations = null
		if (location != null) locations = DataLocation.findAllByLocation(location, params)
		else if (type != null) locations = DataLocation.findAllByType(type, params)
		else locations = DataLocation.list(params);

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
		
		def locations = locationService.searchLocation(DataLocation.class, params['q'], params)
				
		render (view: '/entity/list', model:[
			template:"location/dataLocationList",
			entities: locations,
			entityCount: locations.totalCount,
			entityClass: getEntityClass(),
			code: getLabel()
		])
	}
	
}
