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
package org.chai.kevin.importer

import java.io.InputStreamReader

import org.chai.kevin.AbstractController
import org.chai.kevin.data.RawDataElement;
import org.chai.kevin.data.Type;
import org.chai.kevin.value.RawDataElementValue;
import org.hisp.dhis.period.Period
import org.springframework.web.multipart.MultipartHttpServletRequest
import org.springframework.web.multipart.commons.CommonsMultipartFile

/**
 * @author Jean Kahigiso M.
 *
 */
class ImporterEntityController extends AbstractController {
	ImporterService importerService;
	final String IMPORT_FORM = "import";
	final String IMPORT_OUTPUT = "importoutput";
	def importer = {
		this.getModel(null,null,IMPORT_FORM);
	}
	
	def uploader = { ImporterEntityCommand cmd ->
		ImporterErrorManager errorManager = new ImporterErrorManager();
		if (!cmd.hasErrors()) {
			InputStreamReader csvInputStreamReader = new InputStreamReader(cmd.file.getInputStream());
			importerService.importFile(cmd.dataElement,csvInputStreamReader, cmd.period,errorManager);
			this.getModel(cmd,errorManager,IMPORT_OUTPUT);
		}
		this.getModel(cmd,errorManager,IMPORT_FORM);
	}

	def getModel(def cmd,ImporterErrorManager errorManager,String view) {
		
		List<Period> periods = Period.list()
		List<RawDataElement> dataElements =[]
		if (cmd?.dataElement != null) dataElements << cmd.dataElement
		render (view: '/import/'+view, model:[
					periods: periods,
					dataElements: dataElements,
					importerEntity: cmd,
					errorManager: errorManager
				])
	}
}

class ImporterEntityCommand {

	Period period;
	CommonsMultipartFile file;
	RawDataElement dataElement;

	static constraints = {
		period(blank:false,nullable:false)
		dataElement(blank:false,nullable:false)
		file(blank:false,nullable:false, validator: { val, obj ->

			final String FILE_TYPE = "text/csv";
			boolean valid = true;
			if(val != null)
				if(!val.contentType.equals(FILE_TYPE))
					return valid=false;
			return valid;
		})
	}
}
