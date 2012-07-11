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
package org.chai.kevin.imports


import java.io.InputStreamReader

import org.chai.kevin.AbstractFileUploadController
import org.chai.kevin.LocationService;
import org.chai.kevin.Period;
import org.chai.kevin.data.DataService;
import org.chai.kevin.data.RawDataElement;
import org.chai.kevin.data.Type;
import org.chai.kevin.util.Utils;
import org.chai.kevin.value.RawDataElementValue;
import org.chai.kevin.value.ValueService;
import org.hibernate.SessionFactory;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest
import org.springframework.web.multipart.commons.CommonsMultipartFile
import org.springframework.web.multipart.MultipartFile;
import org.chai.kevin.FileType


/**
 * @author Jean Kahigiso M.
 *
 */
class NominativeImporterController extends AbstractFileUploadController {
	
	LocationService locationService;
	ValueService valueService;
	DataService dataService;
	SessionFactory sessionFactory;
	PlatformTransactionManager transactionManager;
	
	final String IMPORT_FORM = "nominativeImport";
	final String IMPORT_OUTPUT = "importOutput";
	
	def importer = {
		this.getModel(null,null,IMPORT_FORM);
	}
	
	def uploader = { NominativeImporterCommand cmd ->
		ImporterErrorManager errorManager = new ImporterErrorManager();
		if (!cmd.hasErrors()) {
			if(log.isDebugEnabled()) log.debug("uploader(file="+cmd.file+",period="+cmd.period+",dataElement="+cmd.dataElement+" delimiter="+cmd.delimiter+",encoding="+cmd.encoding+")")
			
			NominativeDataImporter importer = new NominativeDataImporter(
				locationService, valueService, dataService,
				sessionFactory, transactionManager,
				errorManager, cmd.dataElement, cmd.period
			);
		
			importFile(importer, cmd.file, cmd.encoding, cmd.delimiter, errorManager)
			
			cmd.file.getInputStream().close();

			this.getModel(cmd,errorManager,IMPORT_OUTPUT);
		}else{
			this.getModel(cmd,errorManager,IMPORT_FORM);
		}	
	}

	def getModel(def cmd,ImporterErrorManager errorManager,String view) {
		if(log.isDebugEnabled()) log.debug("getModel(cmd="+cmd+",errorManager="+errorManager+",view="+view+")")
		
		List<Period> periods = Period.list([cache: true])
		List<RawDataElement> dataElements =[]
		if (cmd?.dataElement != null) dataElements << cmd.dataElement
		render (view: '/import/'+view, model:[
					periods: periods,
					dataElements: dataElements,
					nominativeImporter: cmd,
					errorManager: errorManager
				])
	}
}

class NominativeImporterCommand {

	Period period;
	MultipartFile file;
	String encoding;
	Character delimiter;
	RawDataElement dataElement;
	//TODO validate zip file
	static constraints = {
		file(blank:false, nullable:false, validator: {val, obj ->
			return !val.empty
		})
		delimiter(blank:false,nullable:false)
		encoding(blank:false,nullable:false)
		period(blank:false,nullable:false)
		dataElement(blank:false,nullable:false)
	}
}
