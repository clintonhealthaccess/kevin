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
package org.chai.kevin.imports;

import org.chai.kevin.AbstractController;
import org.chai.kevin.LocationService;
import org.chai.kevin.Period;
import org.chai.kevin.data.DataService;
import org.chai.kevin.data.RawDataElement;
import org.chai.kevin.imports.GeneralDataImporter;
import org.chai.kevin.imports.ImporterErrorManager;
import org.chai.kevin.value.ValueService;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

/**
 * @author Jean Kahigiso M.
 *
 */
class GeneralImporterController extends AbstractController {
	LocationService locationService;
	ValueService valueService;
	DataService dataService;
	final String IMPORT_FORM = "generalImport";
	final String IMPORT_OUTPUT = "importOutput";	
	
	def importer = {
		this.getModel(null,null,IMPORT_FORM);
	}
	
	def uploader = { GeneralImporterCommand cmd ->
		ImporterErrorManager errorManager = new ImporterErrorManager();
		errorManager.setNumberOfSavedRows(0)
		errorManager.setNumberOfUnsavedRows(0)
		errorManager.setNumberOfRowsSavedWithError(0)
		if (!cmd.hasErrors()) {
			if(log.isDebugEnabled()) log.debug("uploader(file="+cmd.file+",period="+cmd.period+")")
			
			GeneralDataImporter importer = new GeneralDataImporter(
				locationService, valueService, dataService,
				errorManager, cmd.period
				);			
			if(cmd.file.getContentType().equals(FILE_TYPE_ZIP))
				importer.importZipFiles(cmd.file.getInputStream())
			if(cmd.file.getContentType().equals(FILE_TYPE_CSV))
				importer.importCsvFile(cmd.file.getName(),cmd.file.getInputStream())
			cmd.file.getInputStream().close();
				
			this.getModel(cmd,errorManager,IMPORT_OUTPUT);
		}else{
			this.getModel(cmd,errorManager,IMPORT_FORM);
		}
	}
	
	def getModel(def cmd,ImporterErrorManager errorManager,String view) {
		if(log.isDebugEnabled()) log.debug("getModel(cmd="+cmd+",errorManager="+errorManager+",view="+view+")")
		List<Period> periods = Period.list([cache: true])
		render (view: '/import/'+view, model:[
					periods: periods,
					generalImporter: cmd,
					errorManager: errorManager
				])
	}

}

class GeneralImporterCommand {

	Period period;
	CommonsMultipartFile file;
	
	static constraints = {
		period(blank:false,nullable:false)
//		file(blank:false,nullable:false, validator: { val, obj ->
//			final String FILE_TYPE = "text/csv";
//			boolean valid = true;
//			if(val != null)
//				if(!val.contentType.equals(FILE_TYPE))
//					valid=false;
//			return valid;
//		})
	}
}
