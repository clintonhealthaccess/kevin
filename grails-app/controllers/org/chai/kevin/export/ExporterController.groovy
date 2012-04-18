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
import org.chai.kevin.util.Utils;
import org.chai.kevin.value.DataValue;
/**
 * @author Jean Kahigiso M.
 *
 */
class ExporterController extends AbstractEntityController {
	def dataLocationService;
	def exporterService;
	def languageService;
	
	def getModel(def entity) {
	}

	def getEntity(def id) {
		return Exporter.get(id);
	}

	def createEntity() {
		return new Exporter();
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
		Exporter export= Exporter.get(params.int('id'));
		if(log.isDebugEnabled()) log.debug("export(export="+export+")")
		
		if(export){
			File csvFile = exporterService.exportData(export);
			def zipFile = Utils.getZipFile(csvFile, export.names[languageService.getCurrentLanguage()])
			
			if(zipFile.exists()){
				response.setHeader("Content-disposition", "attachment; filename=" + zipFile.getName());
				response.setContentType("application/zip");
				response.setHeader("Content-length", zipFile.length().toString());
				response.outputStream << zipFile.newInputStream()
				}
		}	
		if(params['method'].equals("search")) search()
		else list()	
	}
	
	
	def list={
		adaptParamsForList()
		List<Exporter> exports = Exporter.list();
		getExporterListModel(exports,exporterService,list)
	}
	
	def search = {
		adaptParamsForList()
		List<Exporter> exports = exporterService.searchExporter(Exporter.class, params['q'],params);
		getExporterListModel(exports,exporterService,search)
	}
	
	def getExporterListModel(def exports, def exporterService, def method){
		if(log.isDebugEnabled()) log.debug("getExporterModel(exports="+exports+",exporterService="+exporterService+")")
		render (view: '/entity/list', model:[
			template:"exporter/exporterList",
			entities: exports,
			entityCount: exporterService.countExporter(Exporter.class, params['q']),
			code: 'exporter.label',
			method: method,
			q:params['q']
		])
	}

}



