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
import org.chai.kevin.Period
import org.chai.kevin.data.Calculation
import org.chai.kevin.util.Utils
import org.chai.kevin.value.CalculationPartialValue
import org.chai.location.CalculationLocation
import org.chai.location.DataLocationType
import org.chai.location.LocationService

/**
 * @author Jean Kahigiso M.
 *
 */
class CalculationExportController extends AbstractEntityController {
	
	def dataLocationService;
	def dataExportService;
	def calculationExportService;
	def dataService;
	
	def getEntity(def id) {
		return CalculationExport.get(id);
	}

	def createEntity() {
		return new CalculationExport();
	}

	def getTemplate() {
		return "/entity/dataExport/createCalculationExport"
	}
	
	def getLabel() {
		return "calculation.export.label"
	}
	
	def bindParams(def entity) {
		params.dataOld= entity.calculations;
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
		
		Set<Calculation<CalculationPartialValue>> calculations = new HashSet();
		params.list('calculationIds').each { id ->
			if (NumberUtils.isDigits(id)) {
				def calculation = dataService.getData(Long.parseLong(id), Calculation.class)
				if (calculation != null && !calculations.contains(calculation)) calculations.add(calculation);
			}
		}
		entity.calculations = calculations
		
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
		return CalculationExport.class;
	}
	
	def getModel(def entity) {
		List<Calculation> calculations=[]
		List<CalculationLocation> locations=[]
		if(entity.calculations) calculations = new ArrayList(entity.calculations)
		if(entity.locations) locations= new ArrayList(entity.locations)
		[
			exporter: entity,
			periods: Period.list([cache: true]),
			types: DataLocationType.list([cache: true]),
			locations: locations,
			calculations: calculations
		]
	}
	
	def list = {
		adaptParamsForList()
		List<CalculationExport> exports = CalculationExport.list(params)
		this.getDataExportListModel(exports,list)
	}
	
	def search = {
		adaptParamsForList()
		List<CalculationExport> exports = dataExportService.searchDataExports(CalculationExport.class, params['q'],params);
		getDataExportListModel(exports,search)
	}
	
	def getDataExportListModel(def exports,def method){
		if(log.isDebugEnabled()) log.debug("getExporterModel(exports="+exports+",method="+method+")")
		render (view: '/entity/list', model:[
			template:"dataExport/calculationExportList",
			entities: exports,
			entityCount: dataExportService.countDataExports(CalculationExport.class, params['q']),
			code: getLabel(),
			method: method,
			q:params['q']
		])
	}
	
	def clone = {
		CalculationExport exportExisting= CalculationExport.get(params.int('export.id'));
		if (log.isDebugEnabled()) log.debug("clone(exporter="+exportExisting+")")
		if (exportExisting) {
			def newExport= new CalculationExport()
			Utils.copyI18nField(this, copy, "Descriptions")
			newExport.setDate(new Date());
			newExport.setTypeCodeString(exportExisting.getTypeCodeString());
			newExport.getLocations().addAll(exportExisting.getLocations());
			newExport.getPeriods().addAll(exportExisting.getPeriods());
			newExport.getCalculations().addAll(exportExisting.getCalculations());
			newExport.save(failOnError: true);
			
			if(newExport) flash.message = message(code: 'exporter.cloned')
		} else {
			flash.message = message(code: 'exporter.clone.failed')
		}
		
		redirect (action: 'list')	
	}

}
