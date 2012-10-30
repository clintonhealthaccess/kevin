package org.chai.kevin.location;

import org.chai.kevin.AbstractEntityController;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.chai.kevin.AbstractController;
import org.chai.kevin.LanguageService;
import org.chai.location.LocationService;
import org.chai.location.DataLocation;
import org.chai.location.DataLocationType;
import org.chai.location.Location;
import org.chai.location.LocationLevel;
import org.chai.kevin.value.DataValue;
import org.chai.kevin.util.Utils
import org.supercsv.io.CsvListWriter;
import org.supercsv.io.ICsvListWriter;
import org.supercsv.prefs.CsvPreference;

class LocationController extends AbstractEntityController {

	def locationService
	def languageService;
	def calculationLocationService
	def surveyExportService;
	
	def bindParams(def entity) {
		entity.properties = params
		
		if (params.names!=null) entity.names = params.names
	}

	def getModel(def entity) {
		def locations = []
		if (entity.parent != null) locations << entity.parent
		[location: entity, locations: locations, levels: LocationLevel.list([cache: true])]
	}

	def getEntityClass(){
		return Location.class;
	}
	
	def getEntity(def id) {
		return Location.get(id);
	}

	def createEntity() {
		return new Location();
	}

	def deleteEntity(def entity) {
		if (entity.children.size() != 0) {
			flash.message = message(code: 'location.haschildren', args: [message(code: getLabel(), default: 'entity'), params.id], default: 'Location {0} still has associated children.')
		}
		else if (entity.dataLocations.size() != 0) {
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
		
		def level = LocationLevel.get(params.int('level'))
		def location = Location.get(params.int('parent'))
		
		def locations = null
		if (level != null) locations = Location.findAllByLevel(level, params)
		else if (location != null) locations = Location.findAllByParent(location, params)
		else locations = Location.list(params);

		render (view: '/entity/list', model:[
			template:"location/locationList",
			entities: locations,
			entityCount: Location.count(),
			code: getLabel(),
			entityClass: getEntityClass()
		])
	}
	
	def getAjaxData = {
		def clazz = Location.class
		if (params['class'] != null) clazz = Class.forName('org.chai.location.'+params['class'], true, Thread.currentThread().contextClassLoader)
		
		def locations = locationService.searchLocation(clazz, params['term'], [:])		
		render(contentType:"text/json") {
			elements = array {
				locations.each { location ->
					elem (
						key: location.id,
						value: i18n(field:location.names) + ' ['+location.class.simpleName+']' 
					)
				}
			}
		}
	}
	
	def search = {
		adaptParamsForList()
		
		List<Location> locations = locationService.searchLocation(Location.class, params['q'], params)
				
		render (view: '/entity/list', model:[
			template:"location/locationList",
			entities: locations,
			entityCount: locationService.countLocation(Location.class, params['q']),
			entityClass: getEntityClass(),
			code: getLabel()
		])
	}
	
}
