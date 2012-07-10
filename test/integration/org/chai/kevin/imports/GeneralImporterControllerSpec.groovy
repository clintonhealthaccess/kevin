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

import org.chai.kevin.IntegrationTests;
import org.chai.kevin.data.Type;
import org.chai.kevin.value.RawDataElementValue;
import org.chai.kevin.value.Value;
import org.chai.kevin.imports.GeneralImporterCommand
import org.codehaus.groovy.grails.plugins.testing.GrailsMockMultipartFile
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.mock.web.MockMultipartHttpServletRequest;
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.chai.kevin.FileType
import org.chai.kevin.imports.GeneralImporterController;


/**
 * @author Jean Kahigiso M.
 *
 */
class GeneralImporterControllerSpec extends IntegrationTests {
	
	def generalImporterController;
	//TODO find a way to test a controller using  CommonsMultipartFile
	
	def "get uploader"(){
		
		setup:
			setupLocationTree()
			def period = newPeriod()
			def type = Type.TYPE_STRING()
			def dataElement = newRawDataElement(CODE(1), type)
			def importerErrorManager = new ImporterErrorManager();
			
			final String ENCODING = "UTF-8";
			final char DELIMITER = ',';
			
			File tempFileZip = new File("test/integration/org/chai/kevin/imports/nominativeDataUgin.zip");
			File tempFileCsv = new File("test/integration/org/chai/kevin/imports/nominativeDataUgin.csv");
			File tempFilePdf = new File("test/integration/org/chai/kevin/imports/Git1.pdf");
			
			GrailsMockMultipartFile grailsMockMultipartFileZip = new GrailsMockMultipartFile(
				"nominativeTestFile",
				"nominativeTestFile.csv.zip",
				"",
				 tempFileZip.getBytes())
			
			GrailsMockMultipartFile grailsMockMultipartFileCsv = new GrailsMockMultipartFile(
				"nominativeDataUgin",
				"nominativeDataUgin.csv",
				"",
				 tempFileCsv.getBytes())
			
			GrailsMockMultipartFile grailsMockMultipartFilePdf = new GrailsMockMultipartFile(
				"Git1",
				"Git1.pdf",
				"",
				 tempFilePdf.getBytes())
			
			def cmdZip = new GeneralImporterCommand();
			def cmdCsv = new GeneralImporterCommand();
			def cmdPdf = new GeneralImporterCommand();
			
			
			cmdZip.file = grailsMockMultipartFileZip;
			cmdZip.encoding = ENCODING;
			cmdZip.delimiter = DELIMITER;
			
			cmdCsv.file = grailsMockMultipartFileCsv;
			cmdCsv.encoding = ENCODING;
			cmdCsv.delimiter = DELIMITER;
			
			cmdPdf.file = grailsMockMultipartFilePdf;
			cmdPdf.encoding = ENCODING;
			cmdPdf.delimiter = DELIMITER;
		when:
			generalImporterController = new GeneralImporterController()
			generalImporterController.uploader(cmdZip)
		then:
			generalImporterController.modelAndView.model.errorManager.errors.size()==1		

	}
}
