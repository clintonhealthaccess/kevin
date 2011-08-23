package org.chai.kevin

/*
* Copyright (c) 2011, Clinton Health Access Initiative.
*
* All rights reserved.
*
* Redistribution and use in source and binary forms, with or without
* modification, are permitted provided that the following conditions are met:
*     * Redistributions of source code must retain the above copyright
*       notice, this list of conditions and the following disclaimer.
*     * Redistributions in binary form must reproduce the above copyright
*       notice, this list of conditions and the following disclaimer in the
*       documentation and/or other materials provided with the distribution.
*     * Neither the name of the <organization> nor the
*       names of its contributors may be used to endorse or promote products
*       derived from this software without specific prior written permission.
*
* THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
* ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
* WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
* DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
* DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
* (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
* LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
* ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
* (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
* SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;
import org.chai.kevin.data.DataElement;
import org.hisp.dhis.dataset.DataSet;

abstract class AbstractEntityController {
	
	def localeService
	
	def index = {
        redirect(action: "list", params: params)
    }

	def delete = {
		def entity = getEntity(params['id']);
		if (log.isInfoEnabled()) log.info("deleting entity: "+entity)
		
		if (entity != null) {
			try {
				deleteEntity(entity)
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
			saveWithoutTokenCheck()
		}.invalidToken {
			log.warn("clicked twice");
		}
	}
	
	def saveWithoutTokenCheck = {
		log.debug ('saving entity with params:'+params)
		
		def entity = getEntity(params.id);
		if (entity == null) {
			entity = createEntity()
		}
		bindParams(entity)
		log.debug('bound params, entity: '+entity)
		if (!validateEntity(entity)) {
			log.info ("validation error in ${entity}: ${entity.errors}}")
			def htmlText = g.render (template:getTemplate(), model:getModel(entity))
			render(contentType:"text/json") {
				result = 'error'
				html = htmlText
			}
		}
		else {
			saveEntity(entity);
			
			render(contentType:"text/json") {
				result = 'success'
				language = localeService.getCurrentLanguage()
				newEntity = {
					id = entity.id
					if (entity.hasProperty("names")) names = entity.names
				}
				html = html(entity)
			}
		}
	}
	
	protected def html(def entity) {return ""};
	
	protected abstract def bindParams(def entity);
	
	protected abstract def saveEntity(def entity);
	
	protected abstract def deleteEntity(def entity);
	
	protected abstract def validateEntity(def entity);
	
	protected abstract def getModel(def entity);
		
	protected abstract def getEntity(def id);
	
	protected abstract def createEntity();
	
	protected abstract def getTemplate();
	
	
}
