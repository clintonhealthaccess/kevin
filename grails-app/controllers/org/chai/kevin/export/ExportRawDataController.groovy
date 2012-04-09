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
package org.chai.kevin.export

import org.chai.kevin.AbstractController
import org.chai.kevin.AbstractEntityController;
import org.chai.kevin.Period;
import org.chai.kevin.PeriodSorter
import org.chai.kevin.data.Data;
import org.chai.kevin.location.DataLocation;
import org.chai.kevin.location.DataLocationType;
import org.chai.kevin.location.Location;
import org.chai.kevin.value.DataValue;
/**
 * @author Jean Kahigiso M.
 *
 */
class ExportRawDataController extends AbstractEntityController {
	def dataLocationService;
	def exporterService;
	
	def getModel(def entity) {
	}

	def getEntity(def id) {
		return ExporterRawDataElement.get(id);
	}

	def createEntity() {
		return new ExporterRawDataElement();
	}

	def getTemplate() {
		return "/exporter/createExport"
	}
	
	def getLabel() {
		return "export.label"
	}
	
	def bindParams(def entity) {
	
	}
		
	def export ={
		Location location = getLocation()
		Set<DataLocationType> dataLocationTypes = getLocationTypes()
		List<DataLocation> dataLocations = location.collectDataLocations(null, dataLocationTypes)
		
		render (view: '/exporter/exporter', model:[
			locations: Location.list(),
			periods: Period.list(),
			locationsTypes: DataLocationType.list(),
			dataLocations: dataLocations,
			currentLocation: location,
			currentPeriods: getPeriod(),
			currentLocationTypes: dataLocationTypes

		])
		
	}
	
	def list={
		adaptParamsForList()
		List<ExporterRawDataElement> exports = ExporterRawDataElement.list();
		
		render (view: '/entity/list', model:[
			template:"exporter/exporterList",
			entities: exports,
			entityCount: exporterService.countExporter(ExporterRawDataElement.class, params['q']),
			code: 'exporter.label',
			q:params['q']
		])
		
	}
	
	def search = {
		List<ExporterRawDataElement> exports = exporterService.searchExporter(ExporterRawDataElement.class, params['q'],params);
		
		render (view: '/entity/list', model:[
			template:"exporter/exporterList",
			entities: exports,
			entityCount: exporterService.countExporter(ExporterRawDataElement.class, params['q']),
			code: 'exporter.label',
			q:params['q']
		])
		
	}

	

	
}



