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

import java.util.Arrays;
import org.chai.kevin.AbstractFileUploadController;
import org.chai.kevin.LocationService;
import org.chai.kevin.Period;
import org.chai.kevin.PeriodService;
import org.chai.kevin.data.DataService;
import org.chai.kevin.data.RawDataElement;
import org.chai.kevin.imports.GeneralDataImporter;
import org.chai.kevin.imports.ImporterErrorManager;
import org.chai.kevin.imports.FileImporter;
import org.chai.kevin.imports.FileImporter.FileType
import org.chai.kevin.value.ValueService;
import org.hibernate.SessionFactory;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import org.chai.kevin.FileType

/**
 * @author Jean Kahigiso M.
 *
 */
class GeneralImporterController extends AbstractFileUploadController {
	
	LocationService locationService;
	ValueService valueService;
	DataService dataService;
	SessionFactory sessionFactory;
	PeriodService periodService;
	PlatformTransactionManager transactionManager;
	
	final String IMPORT_FORM = "generalImport";
	final String IMPORT_OUTPUT = "importOutput";
	
	def importer = {
		
		this.getModel(null,null,IMPORT_FORM);
	}
	
	def uploader = { GeneralImporterCommand cmd ->
		ImporterErrorManager errorManager = new ImporterErrorManager();

		if (!cmd.hasErrors()) {
			if(log.isDebugEnabled()) log.debug("(file="+cmd.file+",delimiter="+cmd.delimiter+",encoding="+cmd.encoding+")")
			GeneralDataImporter importer = new GeneralDataImporter(
					locationService, valueService, dataService,
					sessionFactory, transactionManager,
					errorManager,periodService
					);
				
			if (getFileType(cmd.file) == FileType.ZIP){
					importer.importZipFiles(cmd.file.getInputStream(), cmd.encoding, cmd.delimiter)
			}
			else if (getFileType(cmd.file) == FileType.CSV){
					importer.importCsvFile(cmd.file.getName(), cmd.file.getInputStream(), cmd.encoding, cmd.delimiter)
			}
			else {
				errorManager.getErrors().add(new ImporterError("\" " + cmd.file.getOriginalFilename() + " \"",1,"File Type Error","import.error.fileType.NotMatching"));
			}
			cmd.file.getInputStream().close();

			this.getModel(cmd,errorManager,IMPORT_OUTPUT);
			
		}else{
			if(log.isDebugEnabled()) log.debug("up ok");
			this.getModel(cmd,errorManager,IMPORT_FORM);
		}
	}
	
	def getModel(def cmd,ImporterErrorManager errorManager,String view) {
		
		render (view: '/import/'+view, model:[
					generalImporter: cmd,
					errorManager: errorManager
				])
	}
}

class GeneralImporterCommand {

	String encoding;
	Character delimiter;
	MultipartFile file;

	//TODO validate zip
	static constraints = {
		
		delimiter(blank:false,nullable:false)
		encoding(blank:false,nullable:false)
		file(blank:false, nullable:false, 
			validator: {val, obj ->
				return !val.empty
			}
		)
	}
}
