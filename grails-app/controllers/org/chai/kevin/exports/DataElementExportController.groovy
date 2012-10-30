/**
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
package org.chai.kevin.exports

import org.apache.commons.lang.math.NumberUtils
import org.chai.kevin.AbstractEntityController
import org.chai.kevin.LanguageService
import org.chai.kevin.Period
import org.chai.kevin.data.DataElement
import org.chai.kevin.util.Utils
import org.chai.kevin.value.DataValue
import org.chai.location.CalculationLocation
import org.chai.location.DataLocationType
import org.chai.location.LocationService

/**
 * @author Jean Kahigiso M.
 *
 */
class DataElementExportController extends AbstractEntityController {
	
	def dataLocationService;
	def dataExportService;
	def dataElementExportService;
	def languageService;
	def dataService;
	
	def getEntity(def id) {
		return DataElementExport.get(id);
	}

	def createEntity() {
		return new DataElementExport();
	}

	def getTemplate() {
		return "/entity/dataExport/createDataElementExport"
	}
	
	def getLabel() {
		return "dataelement.export.label"
	}
	
	def bindParams(def entity) {
		params.dataOld= entity.dataElements;
		params.locationsOld = entity.locations
		entity.properties= params
		
		if(entity.date==null)	
			entity.date = new Date();
		if(log.isDebugEnabled()) log.debug("export(bind="+entity+")")
		
		// we do this because automatic data binding does not work with polymorphic elements
		Set<Period> periods = new HashSet();;
		params.list('periodIds').each { id ->
			if (NumberUtils.isDigits(id)) {
				def period = Period.get(id)
				if (period != null && !periods.contains(period)) periods.add(period);
			}
		}
		entity.periods = periods
		
		Set<DataElement<DataValue>> dataElements = new HashSet();
		params.list('dataElementIds').each { id ->
			if (NumberUtils.isDigits(id)) {
				def dataElement = dataService.getData(Long.parseLong(id), DataElement.class)
				if (dataElement != null && !dataElements.contains(dataElement)) dataElements.add(dataElement);
			}
		}
		entity.dataElements = dataElements
		
		Set<CalculationLocation> dataLocations = new HashSet();
		params.list('locationIds').each { id ->
			if (NumberUtils.isDigits(id)) {
				def dataLocation = locationService.getCalculationLocation(Long.parseLong(id), CalculationLocation.class)
				if (dataLocation != null && !dataLocations.contains(dataLocation)) dataLocations.add(dataLocation);
			}
		}
		entity.locations = dataLocations
	}
	
	def getEntityClass() {
		return DataElementExport.class;
	}
	
	def getModel(def entity) {
		List<DataElement> dataElements=[]
		List<CalculationLocation> locations=[]
		if(entity.dataElements) dataElements = new ArrayList(entity.dataElements)
		if(entity.locations) locations= new ArrayList(entity.locations)
		[
			exporter: entity,
			periods: Period.list([cache: true]),
			types: DataLocationType.list([cache: true]),
			locations: locations,
			dataElements: dataElements
		]
	}
		
	def list = {
		adaptParamsForList()
		List<DataElementExport> exports = DataElementExport.list(params)
		this.getDataExportListModel(exports,list)
	}
	
	def search = {
		adaptParamsForList()
		List<DataElementExport> exports = dataExportService.searchDataExports(DataElementExport.class, params['q'],params);
		getDataExportListModel(exports,search)
	}
	
	def getDataExportListModel(def exports,def method){
		if(log.isDebugEnabled()) log.debug("getExporterModel(exports="+exports+",method="+method+")")
		render (view: '/entity/list', model:[
			template:"dataExport/dataElementExportList",
			entities: exports,
			entityCount: dataExportService.countDataExports(DataElementExport.class, params['q']),
			code: getLabel(),
			method: method,
			q:params['q']
		])
	}
	
	def clone = {
		DataElementExport exportExisting= DataElementExport.get(params.int('export.id'));
		if (log.isDebugEnabled()) log.debug("clone(exporter="+exportExisting+")")
		if (exportExisting) {
			def newExport= new DataElementExport()
			for (String language : languageService.getAvailableLanguages()) {
				newExport.getDescriptions().put(language,exportExisting.getDescriptions().get(language) + "(copy)")
			}		
			newExport.setDate(new Date());
			newExport.setTypeCodeString(exportExisting.getTypeCodeString());
			newExport.getLocations().addAll(exportExisting.getLocations());
			newExport.getPeriods().addAll(exportExisting.getPeriods());
			newExport.getDataElements().addAll(exportExisting.getDataElements());
			newExport.save(failOnError: true);
			
			if(newExport) flash.message = message(code: 'exporter.cloned')
		} else {
			flash.message = message(code: 'exporter.clone.failed')
		}
		
		redirect (action: 'list')	
	}

}
