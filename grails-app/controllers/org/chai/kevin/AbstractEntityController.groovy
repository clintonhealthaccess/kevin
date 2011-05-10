package org.chai.kevin

import org.apache.commons.lang.math.NumberUtils;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataset.DataSet;

abstract class AbstractEntityController {

	def index = {
        redirect(action: "list", params: params)
    }

	def delete = {
		def entity = getEntity(params['id']);
		if (log.isInfoEnabled()) log.info("deleting entity: "+entity)
		
		if (entity != null) {
			try {
				entity.delete(flush: true)
				render(contentType:"text/json") {
					result = 'success'
				}
			}
			catch (org.springframework.dao.DataIntegrityViolationException e) {
				render(contentType:"text/json") {
					result = 'error'
				}
			}
		}
		render(contentType:"text/json") {
			result = 'success'
		}
	}
	
	
	def edit = {
		def entity = getEntity(params['id']);
		if (log.isInfoEnabled()) log.info("editing entity: "+entity);
		render(contentType:"text/json") {
			result = 'success'
			html = g.render(template:getTemplate(), model:getModel(entity))
		}
	}
	
	def create = {
		def entity = createEntity()
		bindParams(entity);

		render(contentType:"text/json") {
			result = 'success'
			html = g.render(template:getTemplate(), model:getModel(entity))
		}
	}
	
	def save = {
		withForm {
			def entity = getEntity(params.id);
			if (entity == null) {
				entity = createEntity()
			}
			bindParams(entity)
			if (!validate(entity)) {
				log.info ("validation error in ${entity}: ${entity.errors}}")
				def htmlText = g.render (template:getTemplate(), model:getModel(entity))
				render(contentType:"text/json") {
					result = 'error'
					html = htmlText
				}
			}
			else {
				save(entity);
				
				render(contentType:"text/json") {
					result = 'success'
					newEntity = {
						id = entity.id
						if (entity.hasProperty("name")) name = entity.name
					}
				}
			}
		}.invalidToken {
			log.warn("clicked twice");
		}
	}
	
	protected abstract def bindParams(def entity);
	
	protected abstract def save(def entity);
	
	protected abstract def validate(def entity);
	
	protected abstract def getModel(def entity);
		
	protected abstract def getEntity(def id);
	
	protected abstract def createEntity();
	
	protected abstract def getTemplate();
	
}
