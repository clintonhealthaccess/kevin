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

import org.chai.kevin.IntegrationTests;
import org.chai.kevin.data.Type;
import org.chai.kevin.value.RawDataElementValue;
import org.chai.kevin.value.Value;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.mock.web.MockMultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

/**
 * @author Jean Kahigiso M.
 *
 */
class GeneralImporterControllerSpec extends IntegrationTests {
	
	def generalImporterController;
	//TODO find a way to test a controller using  CommonsMultipartFile
	/*
	def "get uploader"(){
		
		setup:
		setupLocationTree()
		def period = newPeriod()
		def type = Type.TYPE_STRING()
		def dataElement = newRawDataElement(CODE(1), type)
		def importerErrorManager = new ImporterErrorManager();
				
		def csvCodeString =
		"code,raw_data_element,data_value\n"+
		BUTARO+","+dataElement.code+",overrideStringOne\n"+
		BUTARO+","+dataElement.code+",overrideStringTwo\n"
		
		MockMultipartFile file = new MockMultipartFile("data.csv",csvCodeString.getBytes());
		def cmd = new GeneralImporterCommand();
		cmd.period = period;
		cmd.file = file;
		when:
		generalImporterController = new GeneralImporterController()
		generalImporterController.uploader(cmd)
		then:
		generalImporterController.modelAndView.model.errorManager.errors.size()==1		

	}
	*/
	
	// TODO
	//	def "shows error page after succesful import"(){
	//
	//	}
}
