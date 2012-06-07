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

import java.text.SimpleDateFormat;
import org.chai.kevin.IntegrationTests;
import org.chai.kevin.LocationService;
import org.chai.kevin.Period;
import org.chai.kevin.data.DataService;
import org.chai.kevin.data.RawDataElement;
import org.chai.kevin.data.Type;
import org.chai.kevin.location.DataLocation;
import org.chai.kevin.location.DataLocationType;
import org.chai.kevin.location.Location;
import org.chai.kevin.location.LocationLevel;
import org.chai.kevin.value.RawDataElementValue;
import org.chai.kevin.value.Value;
import org.chai.kevin.value.ValueService;
import org.hibernate.SessionFactory;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.mock.web.MockMultipartHttpServletRequest;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

/**
 * @author Jean Kahigiso M.
 *
 */
class NominativeImporterControllerSpec extends IntegrationTests {
	
	// there is no rollback so each element inserted is kept between tests
	static transactional = false
	
	LocationService locationService;
	ValueService valueService;
	DataService dataService;
	SessionFactory sessionFactory;
	PlatformTransactionManager transactionManager;
	
	def importerService;
	def normalizedImporterController;

	def setupSpec() {
		setupLocationTree()
		newPeriod()
	}
	
	def cleanup() {
		RawDataElementValue.executeUpdate("delete RawDataElementValue")
		RawDataElement.executeUpdate("delete RawDataElement")
	}
	
	def cleanupSpec() {
		DataLocation.executeUpdate("delete DataLocation")
		Location.executeUpdate("delete Location")
		LocationLevel.executeUpdate("delete LocationLevel")
		DataLocationType.executeUpdate("delete DataLocationType")
		Period.executeUpdate("delete Period")
	}
	
	def "test system don't break if the file from zip is not csv"(){
		when:
		
		def typeDate = Type.TYPE_LIST(Type.TYPE_MAP(["birth_date": Type.TYPE_DATE()]))
		def dataDateElement = newRawDataElement(CODE(3), typeDate)

		def importerErrorManagerDate = new ImporterErrorManager();
		importerErrorManagerDate.setNumberOfSavedRows(0)
		importerErrorManagerDate.setNumberOfUnsavedRows(0)
		importerErrorManagerDate.setNumberOfRowsSavedWithError(0)
		
		NominativeDataImporter importer = new NominativeDataImporter(
			locationService, valueService, dataService,
			sessionFactory, transactionManager,
			importerErrorManagerDate, dataDateElement, Period.list()[0]
		);
	
	    File file = new File("test/integration/org/chai/kevin/importer/testFile.csv.zip");
		importer.importZipFiles(new FileInputStream(file));
		then:
		importerErrorManagerDate.errors.size() == 1
		RawDataElementValue.count()==1
		typeDate.getValue(RawDataElementValue.list()[0].value, "[0].birth_date").getDateValue().equals(new SimpleDateFormat("dd-MM-yyyy").parse("15-08-1971"));
		
	}
	
	//TODO find a way to test a controller using  CommonsMultipartFile
/*
	def "get uploader"(){
	
		setup:
		setupLocationTree()
		def period = newPeriod()
		def type = Type.TYPE_LIST(Type.TYPE_MAP(["marital_status": Type.TYPE_BOOL()]))
		def dataElement = newRawDataElement(CODE(1), type)
		def importerErrorManager = new ImporterErrorManager();
		
		def csvString =
		"code,marital_status\n"+
		BUTARO+",0\n"+
		BUTARO+",1\n"+
		BUTARO+",N\n"
		
		MockMultipartFile file = new MockMultipartFile("data.csv",csvString.getBytes());
		def cmd = new NormalizedImporterCommand();
		cmd.dataElement = dataElement;
		cmd.period = period;
		cmd.file = file;
		when:
		normalizedImporterController = new NormalizedImporterController()
		normalizedImporterController.uploader(cmd)
		then:
		normalizedImporterController.modelAndView.model.errorManager.errors.size()==1
		}
*/
	
	// TODO
//	def "shows error page after succesful import"(){ 
//		
//	}
	
}
